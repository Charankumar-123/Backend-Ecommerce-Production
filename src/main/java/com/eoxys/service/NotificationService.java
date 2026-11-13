package com.eoxys.service;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.eoxys.entity.OrderItemsEntity;
import com.eoxys.entity.OrdersEntity;
import com.eoxys.entity.UsersEntity;

import jakarta.mail.Authenticator;
import jakarta.mail.Message;
import jakarta.mail.PasswordAuthentication;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;

@Service
public class NotificationService {

    private static final Logger log = LoggerFactory.getLogger(NotificationService.class);

    @Value("${app.notification.email.enabled:false}")
    private boolean emailEnabled;

    @Value("${app.notification.email.from:orders@modernstore.com}")
    private String emailFrom;

    @Value("${spring.mail.host:smtp.gmail.com}")
    private String smtpHost;

    @Value("${spring.mail.port:587}")
    private int smtpPort;

    @Value("${spring.mail.username:}")
    private String smtpUsername;

    @Value("${spring.mail.password:}")
    private String smtpPassword;

    @Value("${app.notification.sms.enabled:false}")
    private boolean smsEnabled;

    @Value("${app.notification.sms.api-key:}")
    private String smsApiKey;

    @Value("${app.notification.sms.provider:FAST2SMS}")
    private String smsProvider;

    /**
     * Dispatches Email and SMS asynchronously so order placement response is instantaneous.
     */
    @Async
    public void sendOrderPlacedNotifications(UsersEntity user, OrdersEntity order, List<OrderItemsEntity> items) {
        if (user == null || order == null) {
            log.warn("Cannot dispatch notification: User or Order entity is null");
            return;
        }

        try {
            sendEmailNotification(user, order, items);
        } catch (Exception ex) {
            log.error("Error processing email notification for order #{}: {}", order.getOrderId(), ex.getMessage());
        }

        try {
            sendSmsNotification(user, order);
        } catch (Exception ex) {
            log.error("Error processing SMS notification for order #{}: {}", order.getOrderId(), ex.getMessage());
        }
    }

    /**
     * Formats and delivers Customer Order Confirmation Email.
     */
    private void sendEmailNotification(UsersEntity user, OrdersEntity order, List<OrderItemsEntity> items) {
        String recipientEmail = user.getEmail();
        String customerName = user.getUserName() != null ? user.getUserName() : "Valued Customer";
        String subject = "Order Confirmation - #ORD-" + order.getOrderId() + " | ModernStore";

        StringBuilder itemsListText = new StringBuilder();
        StringBuilder itemsListHtml = new StringBuilder();

        if (items != null) {
            for (OrderItemsEntity it : items) {
                String pName = it.getProductOrderInfo() != null ? it.getProductOrderInfo().getName() : ("Product #" + it.getProductId());
                itemsListText.append(String.format("   • %dx %s - ₹%.2f\n", it.getQuantity(), pName, it.getPrice()));
                itemsListHtml.append(String.format(
                    "<tr>" +
                    "<td style='padding: 10px; border-bottom: 1px solid #e2e8f0; font-size: 14px;'><strong>%s</strong></td>" +
                    "<td style='padding: 10px; border-bottom: 1px solid #e2e8f0; font-size: 14px; text-align: center;'>%d</td>" +
                    "<td style='padding: 10px; border-bottom: 1px solid #e2e8f0; font-size: 14px; text-align: right; font-weight: 600;'>₹%.2f</td>" +
                    "</tr>",
                    pName, it.getQuantity(), it.getPrice()
                ));
            }
        }

        // 1. Console Receipt Preview
        String emailConsolePreview = String.format(
            "\n========================================================================================\n" +
            "📧 [EMAIL NOTIFICATION] ORDER CONFIRMATION PREVIEW\n" +
            "----------------------------------------------------------------------------------------\n" +
            "To       : %s <%s>\n" +
            "From     : ModernStore <%s>\n" +
            "Subject  : %s\n" +
            "Order ID : #ORD-%d\n" +
            "Amount   : ₹%.2f\n" +
            "Status   : %s\n" +
            "Items Purchased:\n%s" +
            "Delivery : ModernStore Express (Est: 3 Days)\n" +
            "Tracking : http://localhost:3000/orders\n" +
            "========================================================================================",
            customerName, recipientEmail, emailFrom, subject, order.getOrderId(), order.getTotalPrice(),
            order.getStatus(), itemsListText.toString()
        );

        log.info(emailConsolePreview);

        // 2. Real SMTP Delivery if enabled and configured
        if (emailEnabled) {
            if (smtpUsername == null || smtpUsername.isBlank() || smtpPassword == null || smtpPassword.isBlank()) {
                log.warn("⚠️ Live email is enabled (app.notification.email.enabled=true), but spring.mail.username or spring.mail.password is missing in application.properties.");
                return;
            }

            try {
                log.info("Initiating live SMTP dispatch to: {} from: {}", recipientEmail, emailFrom);
                String htmlBody = buildOrderConfirmationHtml(customerName, order, itemsListHtml.toString());
                sendSmtpEmail(recipientEmail, subject, htmlBody);
                log.info("✅ Live email successfully sent to customer inbox: {}", recipientEmail);
            } catch (Exception e) {
                log.error("❌ Failed to send live email to {}: {}", recipientEmail, e.getMessage(), e);
            }
        } else {
            log.info("ℹ️ Live email delivery is currently OFF (app.notification.email.enabled=false). Console preview logged above. Set app.notification.email.enabled=true in application.properties with your Gmail App Password to send directly to inboxes.");
        }
    }

    /**
     * Sends live SMTP Email via Jakarta Mail (Gmail / SendGrid / AWS SES).
     */
    private void sendSmtpEmail(String recipientEmail, String subject, String htmlContent) throws Exception {
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.starttls.required", "true");
        props.put("mail.smtp.ssl.protocols", "TLSv1.2");
        props.put("mail.smtp.ssl.trust", "*");
        props.put("mail.smtp.host", smtpHost != null && !smtpHost.isBlank() ? smtpHost.trim() : "smtp.gmail.com");
        props.put("mail.smtp.port", String.valueOf(smtpPort > 0 ? smtpPort : 587));
        props.put("mail.smtp.connectiontimeout", "10000");
        props.put("mail.smtp.timeout", "10000");

        final String cleanUsername = smtpUsername != null ? smtpUsername.trim() : "";
        final String cleanPassword = smtpPassword != null ? smtpPassword.trim().replaceAll("\\s+", "") : "";

        Session session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(cleanUsername, cleanPassword);
            }
        });

        Message message = new MimeMessage(session);
        message.setFrom(new InternetAddress(emailFrom != null && !emailFrom.isBlank() ? emailFrom.trim() : cleanUsername));
        message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipientEmail.trim()));
        message.setSubject(subject);
        message.setContent(htmlContent, "text/html; charset=utf-8");

        Transport.send(message);
    }

    /**
     * Builds responsive HTML receipt email.
     */
    private String buildOrderConfirmationHtml(String customerName, OrdersEntity order, String itemsHtml) {
        return "<!DOCTYPE html>" +
            "<html>" +
            "<body style='font-family: -apple-system, BlinkMacSystemFont, Segoe UI, Roboto, Helvetica, Arial, sans-serif; background-color: #f8fafc; margin: 0; padding: 24px;'>" +
            "  <div style='max-width: 600px; margin: 0 auto; background: #ffffff; border-radius: 12px; overflow: hidden; box-shadow: 0 4px 12px rgba(0,0,0,0.06);'>" +
            "    <div style='background: linear-gradient(135deg, #00796b 0%, #004d40 100%); padding: 24px 30px; text-align: center; color: #ffffff;'>" +
            "      <h1 style='margin: 0; font-size: 24px; font-weight: 800;'>🛍️ ModernStore</h1>" +
            "      <p style='margin: 6px 0 0; font-size: 14px; opacity: 0.9;'>Order Confirmation</p>" +
            "    </div>" +
            "    <div style='padding: 30px;'>" +
            "      <h2 style='color: #1e293b; font-size: 20px; margin-top: 0;'>Thank you for your order, " + customerName + "!</h2>" +
            "      <p style='color: #64748b; font-size: 14px; line-height: 1.6;'>We've received your order <strong>#ORD-" + order.getOrderId() + "</strong> and are preparing it for fulfillment.</p>" +
            "      <table style='width: 100%; border-collapse: collapse; margin: 20px 0;'>" +
            "        <thead>" +
            "          <tr style='background: #f1f5f9; text-align: left;'>" +
            "            <th style='padding: 10px; font-size: 12px; text-transform: uppercase; color: #475569;'>Item</th>" +
            "            <th style='padding: 10px; font-size: 12px; text-transform: uppercase; color: #475569; text-align: center;'>Qty</th>" +
            "            <th style='padding: 10px; font-size: 12px; text-transform: uppercase; color: #475569; text-align: right;'>Total</th>" +
            "          </tr>" +
            "        </thead>" +
            "        <tbody>" +
            itemsHtml +
            "        </tbody>" +
            "      </table>" +
            "      <div style='border-top: 2px solid #f1f5f9; padding-top: 15px; text-align: right;'>" +
            "        <span style='font-size: 15px; color: #64748b;'>Grand Total: </span>" +
            "        <strong style='font-size: 22px; color: #00796b;'>₹" + String.format("%.2f", order.getTotalPrice()) + "</strong>" +
            "      </div>" +
            "      <div style='margin-top: 30px; text-align: center;'>" +
            "        <a href='http://localhost:3000/orders' style='background: #00796b; color: #ffffff; text-decoration: none; padding: 12px 26px; border-radius: 8px; font-weight: 700; font-size: 14px; display: inline-block;'>Track Package Live</a>" +
            "      </div>" +
            "    </div>" +
            "  </div>" +
            "</body>" +
            "</html>";
    }

    /**
     * Formats and delivers Customer Order Confirmation SMS.
     */
    private void sendSmsNotification(UsersEntity user, OrdersEntity order) {
        Long mobileNum = user.getMobile();
        String customerName = user.getUserName() != null ? user.getUserName() : "Customer";
        String smsMessage = String.format(
            "Hi %s, your ModernStore order #ORD-%d of ₹%.2f has been confirmed! Track package: http://localhost:3000/orders",
            customerName, order.getOrderId(), order.getTotalPrice()
        );

        String phoneStr = mobileNum != null ? String.valueOf(mobileNum) : "N/A";

        // Console SMS preview
        String smsPreview = String.format(
            "\n========================================================================================\n" +
            "📱 [SMS NOTIFICATION] ORDER CONFIRMATION PREVIEW\n" +
            "----------------------------------------------------------------------------------------\n" +
            "Recipient Phone : +91 %s\n" +
            "Sender ID       : MODSTR\n" +
            "SMS Gateway     : %s\n" +
            "Message Body    :\n   \"%s\"\n" +
            "========================================================================================",
            phoneStr, smsProvider, smsMessage
        );

        log.info(smsPreview);

        // Real SMS dispatch if enabled and API key is present
        if (smsEnabled && smsApiKey != null && !smsApiKey.isBlank() && mobileNum != null) {
            triggerLiveSmsGateway(phoneStr, smsMessage);
        } else {
            log.info("ℹ️ Live SMS gateway is currently OFF (app.notification.sms.enabled=false). Console preview logged above. Set app.notification.sms.enabled=true with your Fast2SMS/Twilio API key in application.properties to send live SMS to phones.");
        }
    }

    /**
     * Dispatches real SMS using Fast2SMS / Twilio HTTP REST API (Standard Java HttpURLConnection).
     */
    private void triggerLiveSmsGateway(String phone, String message) {
        try {
            String jsonPayload = String.format(
                "{\"route\":\"q\",\"message\":\"%s\",\"language\":\"english\",\"numbers\":\"%s\"}",
                message.replace("\"", "\\\""), phone
            );

            URL url = URI.create("https://www.fast2sms.com/dev/bulkV2").toURL();
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("authorization", smsApiKey);
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);
            conn.setConnectTimeout(5000);
            conn.setReadTimeout(5000);

            try (OutputStream os = conn.getOutputStream()) {
                byte[] input = jsonPayload.getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }

            int responseCode = conn.getResponseCode();
            log.info("✅ SMS Gateway responded with HTTP {}", responseCode);
        } catch (Exception ex) {
            log.error("SMS dispatch error: {}", ex.getMessage());
        }
    }
}
