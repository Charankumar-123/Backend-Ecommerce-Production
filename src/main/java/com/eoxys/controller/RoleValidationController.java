package com.eoxys.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true")
@RestController
@RequestMapping("/api")
public class RoleValidationController {

    @GetMapping("/seller/profile")
    @PreAuthorize("hasAnyRole('SELLER', 'ADMIN')")
    public ResponseEntity<Map<String, Object>> getSellerPortal(Authentication auth) {
        Map<String, Object> res = new HashMap<>();
        res.put("status", true);
        res.put("portal", "SELLER");
        res.put("user", auth.getName());
        res.put("authorities", auth.getAuthorities().toString());
        res.put("message", "Authorized access to Seller Portal");
        return ResponseEntity.ok(res);
    }

    @GetMapping("/inventory/overview")
    @PreAuthorize("hasAnyRole('INVENTORY', 'ADMIN')")
    public ResponseEntity<Map<String, Object>> getInventoryPortal(Authentication auth) {
        Map<String, Object> res = new HashMap<>();
        res.put("status", true);
        res.put("portal", "INVENTORY");
        res.put("user", auth.getName());
        res.put("authorities", auth.getAuthorities().toString());
        res.put("message", "Authorized access to Inventory Management");
        return ResponseEntity.ok(res);
    }

    @GetMapping("/warehouse/overview")
    @PreAuthorize("hasAnyRole('WAREHOUSE', 'ADMIN')")
    public ResponseEntity<Map<String, Object>> getWarehousePortal(Authentication auth) {
        Map<String, Object> res = new HashMap<>();
        res.put("status", true);
        res.put("portal", "WAREHOUSE");
        res.put("user", auth.getName());
        res.put("authorities", auth.getAuthorities().toString());
        res.put("message", "Authorized access to Warehouse Management");
        return ResponseEntity.ok(res);
    }

    @GetMapping("/admin/overview")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> getAdminPortal(Authentication auth) {
        Map<String, Object> res = new HashMap<>();
        res.put("status", true);
        res.put("portal", "ADMIN");
        res.put("user", auth.getName());
        res.put("authorities", auth.getAuthorities().toString());
        res.put("message", "Authorized access to Admin Console");
        return ResponseEntity.ok(res);
    }
}

