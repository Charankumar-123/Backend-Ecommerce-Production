package com.eoxys.crud;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import com.eoxys.entity.OrderItemsEntity;
import com.eoxys.entity.OrdersEntity;
import com.eoxys.entity.UsersEntity;
import com.eoxys.service.NotificationService;
import com.eoxys.utils.OrderStatus;

public class NotificationServiceTest {

    @Test
    public void testNotificationFormattingAndDispatch() {
        NotificationService notificationService = new NotificationService();

        UsersEntity user = new UsersEntity();
        user.setEmail("customer@example.com");
        user.setUserName("John Doe");
        user.setMobile(9876543210L);

        OrdersEntity order = new OrdersEntity();
        order.setOrderId(101L);
        order.setTotalPrice(1999.0f);
        order.setStatus(OrderStatus.ORDERED);

        List<OrderItemsEntity> items = new ArrayList<>();
        OrderItemsEntity item1 = new OrderItemsEntity();
        item1.setProductId(1L);
        item1.setQuantity(2L);
        item1.setPrice(999.0f);
        items.add(item1);

        assertDoesNotThrow(() -> {
            notificationService.sendOrderPlacedNotifications(user, order, items);
        });
    }
}

