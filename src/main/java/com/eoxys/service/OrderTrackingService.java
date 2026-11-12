package com.eoxys.service;

import java.sql.Date;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import com.eoxys.entity.OrderTrackingEntity;
import com.eoxys.entity.OrdersEntity;
import com.eoxys.repository.OrderTrackingRepository;
import com.eoxys.repository.OrdersRepository;
import com.eoxys.utils.OrderStatus;

@Service
public class OrderTrackingService {

    @Autowired
    private OrderTrackingRepository orderTrackingRepository;

    @Autowired
    private OrdersRepository ordersRepository;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    /**
     * Get tracking details for an order.
     * If no record exists yet, automatically create an initial ORDERED record.
     */
    public OrderTrackingEntity getTrackingDetails(Long orderId) {
        OrderTrackingEntity tracking = orderTrackingRepository.findByOrderId(orderId);
        if (tracking == null) {
            tracking = new OrderTrackingEntity();
            tracking.setOrderId(orderId);
            tracking.setStatus("ORDERED");
            tracking.setCarrier("ModernStore Express");
            tracking.setTrackingNumber("TRK-" + (orderId != null ? (orderId * 89234L) : System.currentTimeMillis()));
            tracking.setEstimatedDeliveryDate(new Date(System.currentTimeMillis() + 3L * 24 * 60 * 60 * 1000));
            tracking = orderTrackingRepository.save(tracking);
            System.out.println("Initialized new tracking record for orderId: " + orderId);
        }
        return tracking;
    }

    /**
     * Called by Warehouse, Seller, Admin, or Logistics API to update order tracking.
     * Persists changes to the database and publishes to the WebSocket topic.
     */
    public OrderTrackingEntity updateAndPublishTracking(Long orderId, String newStatus, String carrier, String location) {
        // 1. Fetch or initialize tracking entity
        OrderTrackingEntity tracking = orderTrackingRepository.findByOrderId(orderId);
        if (tracking == null) {
            tracking = new OrderTrackingEntity();
            tracking.setOrderId(orderId);
            tracking.setTrackingNumber("TRK-" + (orderId != null ? (orderId * 89234L) : System.currentTimeMillis()));
        }

        if (newStatus != null && !newStatus.trim().isEmpty()) {
            tracking.setStatus(newStatus.trim().toUpperCase());
        }

        if (carrier != null && !carrier.trim().isEmpty()) {
            tracking.setCarrier(carrier.trim());
        }

        // Set estimated delivery if missing
        if (tracking.getEstimatedDeliveryDate() == null) {
            tracking.setEstimatedDeliveryDate(new Date(System.currentTimeMillis() + 3L * 24 * 60 * 60 * 1000));
        }

        // Save tracking to DB
        OrderTrackingEntity savedTracking = orderTrackingRepository.save(tracking);

        // 2. Also update status in OrdersEntity (tbl_orders)
        try {
            OrdersEntity order = ordersRepository.findByOrderId(orderId);
            if (order != null) {
                // Map status string to enum if valid
                try {
                    String normStatus = newStatus.trim().toUpperCase();
                    if (normStatus.equals("DISPATCHED")) normStatus = "PROCESSING";
                    if (normStatus.equals("IN_TRANSIT")) normStatus = "SHIPPED";
                    if (normStatus.equals("OUT_FOR_DELIVERY")) normStatus = "SHIPPED";
                    order.setStatus(OrderStatus.valueOf(normStatus));
                    ordersRepository.save(order);
                } catch (Exception ex) {
                    // Ignore enum mismatch, tracking entity holds accurate tracking status
                }
            }
        } catch (Exception e) {
            System.err.println("Could not sync order entity status: " + e.getMessage());
        }

        // 3. Prepare real-time payload with timestamp and location
        Map<String, Object> messagePayload = new HashMap<>();
        messagePayload.put("trackingId", savedTracking.getTrackingId());
        messagePayload.put("orderId", savedTracking.getOrderId());
        messagePayload.put("status", savedTracking.getStatus());
        messagePayload.put("carrier", savedTracking.getCarrier());
        messagePayload.put("trackingNumber", savedTracking.getTrackingNumber());
        messagePayload.put("estimatedDeliveryDate", savedTracking.getEstimatedDeliveryDate());
        messagePayload.put("location", location != null ? location : "In Transit Facility");
        messagePayload.put("updatedAt", System.currentTimeMillis());

        // 4. PUBLISH to WebSocket STOMP topic: /topic/order-tracking/{orderId}
        String destination = "/topic/order-tracking/" + orderId;
        System.out.println(">>> WebSocket Publishing to " + destination + " | Status: " + savedTracking.getStatus());
        messagingTemplate.convertAndSend(destination, messagePayload);

        return savedTracking;
    }
}
