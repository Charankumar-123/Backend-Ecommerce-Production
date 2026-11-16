package com.eoxys.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.eoxys.entity.OrderItemsEntity;
import com.eoxys.entity.OrderTrackingEntity;
import com.eoxys.entity.OrdersEntity;
import com.eoxys.repository.OrderItemsRepository;
import com.eoxys.repository.OrderTrackingRepository;
import com.eoxys.repository.OrdersRepository;
import com.eoxys.service.OrderTrackingService;

@RestController
@RequestMapping("/api/warehouse")
@CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true")
@PreAuthorize("hasAnyRole('WAREHOUSE', 'ADMIN')")
public class WarehouseController {

    @Autowired
    private OrdersRepository ordersRepository;

    @Autowired
    private OrderItemsRepository orderItemsRepository;

    @Autowired
    private OrderTrackingRepository orderTrackingRepository;

    @Autowired
    private OrderTrackingService orderTrackingService;

    /**
     * Retrieve all real customer orders for the Warehouse Fulfillment Queue.
     */
    @GetMapping("/orders")
    public ResponseEntity<?> getWarehouseFulfillmentOrders() {
        List<OrdersEntity> orders = ordersRepository.findAll(Sort.by(Sort.Direction.DESC, "orderId"));
        List<Map<String, Object>> response = new ArrayList<>();

        for (OrdersEntity order : orders) {
            Map<String, Object> map = new HashMap<>();
            Long orderId = order.getOrderId();
            map.put("orderId", orderId);
            map.put("userId", order.getUserId());
            map.put("totalPrice", order.getTotalPrice());

            // Customer details
            if (order.getUserOrderInfo() != null) {
                map.put("customerName", order.getUserOrderInfo().getUserName());
                map.put("customerEmail", order.getUserOrderInfo().getEmail());
                map.put("customerMobile", order.getUserOrderInfo().getMobile());
            } else {
                map.put("customerName", "Customer #" + order.getUserId());
                map.put("customerEmail", "N/A");
                map.put("customerMobile", "N/A");
            }

            // Delivery Location (Live GPS / Address)
            map.put("deliveryAddress", order.getDeliveryAddress());
            map.put("deliveryCity", order.getDeliveryCity());
            map.put("deliveryPincode", order.getDeliveryPincode());
            map.put("deliveryState", order.getDeliveryState());
            map.put("latitude", order.getLatitude());
            map.put("longitude", order.getLongitude());
            map.put("recipientName", order.getRecipientName() != null ? order.getRecipientName() : map.get("customerName"));
            map.put("recipientPhone", order.getRecipientPhone() != null ? order.getRecipientPhone() : map.get("customerMobile"));

            // Line items
            List<OrderItemsEntity> items = orderItemsRepository.findByOrderId(orderId);
            List<Map<String, Object>> itemsList = new ArrayList<>();
            for (OrderItemsEntity item : items) {
                Map<String, Object> itMap = new HashMap<>();
                itMap.put("productId", item.getProductId());
                itMap.put("quantity", item.getQuantity());
                itMap.put("price", item.getPrice());
                if (item.getProductOrderInfo() != null) {
                    itMap.put("productName", item.getProductOrderInfo().getName());
                    itMap.put("category", item.getProductOrderInfo().getCategory());
                    itMap.put("image", item.getProductOrderInfo().getImage());
                } else {
                    itMap.put("productName", "Product #" + item.getProductId());
                }
                itemsList.add(itMap);
            }
            map.put("items", itemsList);

            // Real-Time Tracking status
            OrderTrackingEntity tracking = orderTrackingRepository.findByOrderId(orderId);
            if (tracking != null) {
                map.put("fulfillmentStatus", tracking.getStatus());
                map.put("trackingNumber", tracking.getTrackingNumber());
                map.put("carrier", tracking.getCarrier());
                map.put("estimatedDeliveryDate", tracking.getEstimatedDeliveryDate());
            } else {
                map.put("fulfillmentStatus", order.getStatus() != null ? order.getStatus().name() : "ORDERED");
                map.put("trackingNumber", "TRK-" + (orderId * 89234L));
                map.put("carrier", "ModernStore Express");
            }

            response.add(map);
        }

        return ResponseEntity.ok(response);
    }

    /**
     * Warehouse Operator updates fulfillment milestone.
     * Persists status and immediately publishes to /topic/order-tracking/{orderId}.
     */
    @PostMapping("/update-status")
    public ResponseEntity<?> updateFulfillmentStatus(@RequestBody Map<String, Object> body) {
        if (!body.containsKey("orderId") || !body.containsKey("status")) {
            return ResponseEntity.badRequest().body(Map.of("status", false, "message", "Missing orderId or status"));
        }

        Long orderId = Long.valueOf(body.get("orderId").toString());
        String status = (String) body.get("status");
        String carrier = body.getOrDefault("carrier", "ModernStore Express").toString();
        String location = body.getOrDefault("location", "Central Fulfillment Hub, Bangalore").toString();

        OrderTrackingEntity updated = orderTrackingService.updateAndPublishTracking(orderId, status, carrier, location);

        Map<String, Object> res = new HashMap<>();
        res.put("status", true);
        res.put("message", "Fulfillment status updated and pushed via WebSocket to Customer timeline");
        res.put("data", updated);

        return ResponseEntity.ok(res);
    }

    /**
     * Summary KPIs for the Warehouse station
     */
    @GetMapping("/stats")
    public ResponseEntity<?> getWarehouseStats() {
        List<OrdersEntity> allOrders = ordersRepository.findAll();
        long pending = 0;
        long packed = 0;
        long dispatched = 0;
        long delivered = 0;

        for (OrdersEntity ord : allOrders) {
            OrderTrackingEntity trk = orderTrackingRepository.findByOrderId(ord.getOrderId());
            String st = trk != null ? trk.getStatus().toUpperCase() : (ord.getStatus() != null ? ord.getStatus().name() : "ORDERED");
            if (st.contains("DELIVER")) {
                delivered++;
            } else if (st.contains("TRANSIT") || st.contains("DISPATCH") || st.contains("SHIP")) {
                dispatched++;
            } else if (st.contains("PACK")) {
                packed++;
            } else {
                pending++;
            }
        }

        Map<String, Object> stats = new HashMap<>();
        stats.put("totalOrders", allOrders.size());
        stats.put("pendingPick", pending);
        stats.put("packedCount", packed);
        stats.put("dispatchedCount", dispatched);
        stats.put("deliveredCount", delivered);

        return ResponseEntity.ok(stats);
    }
}

