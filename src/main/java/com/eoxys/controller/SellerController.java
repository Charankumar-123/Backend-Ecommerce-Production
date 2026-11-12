package com.eoxys.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.eoxys.dto.Response;
import com.eoxys.entity.OrderItemsEntity;
import com.eoxys.entity.Product;
import com.eoxys.entity.UserPrincipal;
import com.eoxys.repository.OrderItemsRepository;
import com.eoxys.service.SellerService;

@RestController
@RequestMapping("/api/seller")
@CrossOrigin(origins = "http://localhost:3000")
@PreAuthorize("hasAnyRole('SELLER', 'ADMIN')")
public class SellerController {

    @Autowired
    private SellerService sellerService;

    @Autowired
    private OrderItemsRepository orderItemsRepository;

    private Long getCurrentUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getPrincipal() instanceof UserPrincipal principal) {
            return principal.getUserId();
        }
        return null;
    }

    private boolean isCurrentUserAdmin() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getPrincipal() instanceof UserPrincipal principal) {
            return "ADMIN".equalsIgnoreCase(principal.getRole());
        }
        return false;
    }

    @GetMapping("/products")
    public ResponseEntity<?> getSellerProducts(@RequestParam(value = "sellerId", required = false) Long paramSellerId) {
        Long sellerId = (isCurrentUserAdmin() && paramSellerId != null) ? paramSellerId : getCurrentUserId();
        if (sellerId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("status", false, "message", "User identity could not be verified"));
        }
        List<Product> products = sellerService.getSellerProducts(sellerId);
        return ResponseEntity.ok(products);
    }

    @PostMapping("/products")
    public ResponseEntity<?> addSellerProduct(@RequestBody Product product) {
        Long sellerId = getCurrentUserId();
        if (sellerId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("status", false, "message", "User identity could not be verified"));
        }
        Product saved = sellerService.addSellerProduct(product, sellerId);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @PutMapping("/products/{id}")
    public ResponseEntity<Response> updateSellerProduct(@PathVariable("id") Long id, @RequestBody Product product) {
        Long sellerId = getCurrentUserId();
        boolean isAdmin = isCurrentUserAdmin();
        Response res = sellerService.updateSellerProduct(id, product, sellerId, isAdmin);
        if (res.isStatus()) {
            return ResponseEntity.ok(res);
        } else {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(res);
        }
    }

    @DeleteMapping("/products/{id}")
    public ResponseEntity<Response> deleteSellerProduct(@PathVariable("id") Long id) {
        Long sellerId = getCurrentUserId();
        boolean isAdmin = isCurrentUserAdmin();
        Response res = sellerService.deleteSellerProduct(id, sellerId, isAdmin);
        if (res.isStatus()) {
            return ResponseEntity.ok(res);
        } else {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(res);
        }
    }

    @GetMapping("/dashboard-stats")
    public ResponseEntity<?> getDashboardStats(@RequestParam(value = "sellerId", required = false) Long paramSellerId) {
        Long sellerId = (isCurrentUserAdmin() && paramSellerId != null) ? paramSellerId : getCurrentUserId();
        if (sellerId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("status", false, "message", "User identity could not be verified"));
        }
        Map<String, Object> stats = sellerService.getSellerDashboardStats(sellerId);
        return ResponseEntity.ok(stats);
    }

    @GetMapping("/sales-history")
    public ResponseEntity<?> getSalesHistory() {
        Long sellerId = getCurrentUserId();
        if (sellerId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("status", false, "message", "User identity could not be verified"));
        }
        List<OrderItemsEntity> sales = orderItemsRepository.findBySellerId(sellerId);
        return ResponseEntity.ok(sales);
    }
}

