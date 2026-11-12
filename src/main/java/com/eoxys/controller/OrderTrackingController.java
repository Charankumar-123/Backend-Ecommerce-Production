package com.eoxys.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.eoxys.entity.OrderTrackingEntity;
import com.eoxys.service.OrderTrackingService;

@CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true")
@RestController
public class OrderTrackingController {

    @Autowired
    private OrderTrackingService orderTrackingService;

    /**
     * Customer UI calls this when opening the Track Package modal
     * to fetch initial tracking data.
     */
    @GetMapping("/tracking/{orderId}")
    public ResponseEntity<OrderTrackingEntity> getTrackingDetails(@PathVariable Long orderId) {
        OrderTrackingEntity details = orderTrackingService.getTrackingDetails(orderId);
        return ResponseEntity.ok(details);
    }

    /**
     * Seller, Warehouse, or Delivery Agent calls this to update status.
     * Triggers WebSocket publish to /topic/order-tracking/{orderId}.
     */
    @PostMapping("/tracking/update")
    public ResponseEntity<?> updateTracking(
            @RequestParam Long orderId,
            @RequestParam String status,
            @RequestParam(required = false) String carrier,
            @RequestParam(required = false) String location) {

        OrderTrackingEntity updated = orderTrackingService.updateAndPublishTracking(orderId, status, carrier, location);
        Map<String, Object> res = new HashMap<>();
        res.put("status", true);
        res.put("message", "Tracking status updated and broadcasted via WebSocket");
        res.put("data", updated);
        return ResponseEntity.ok(res);
    }

    /**
     * JSON body version for Seller / Warehouse frontend portals
     */
    @PostMapping("/tracking/update-json")
    public ResponseEntity<?> updateTrackingJson(@RequestBody Map<String, Object> body) {
        Long orderId = Long.valueOf(body.get("orderId").toString());
        String status = (String) body.get("status");
        String carrier = (String) body.get("carrier");
        String location = (String) body.get("location");

        OrderTrackingEntity updated = orderTrackingService.updateAndPublishTracking(orderId, status, carrier, location);
        Map<String, Object> res = new HashMap<>();
        res.put("status", true);
        res.put("message", "Tracking updated & broadcasted");
        res.put("data", updated);
        return ResponseEntity.ok(res);
    }
}
