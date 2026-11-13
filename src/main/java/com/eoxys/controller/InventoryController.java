package com.eoxys.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.eoxys.entity.Product;
import com.eoxys.repository.ProductRepository;

@RestController
@RequestMapping("/api/inventory")
@CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true")
@PreAuthorize("hasAnyRole('INVENTORY', 'ADMIN')")
public class InventoryController {

    @Autowired
    private ProductRepository productRepository;

    @GetMapping("/stock-list")
    public ResponseEntity<List<Product>> getInventoryStockList() {
        List<Product> products = productRepository.findAll();
        return ResponseEntity.ok(products);
    }

    @PutMapping("/update-stock/{id}")
    public ResponseEntity<?> updateStockQuantity(@PathVariable Long id, @RequestBody Map<String, Object> body) {
        Optional<Product> prodOpt = productRepository.getByProductId(id);
        if (prodOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("status", false, "message", "Product not found"));
        }

        if (!body.containsKey("quantity")) {
            return ResponseEntity.badRequest().body(Map.of("status", false, "message", "Missing quantity field"));
        }

        int newQty = Integer.parseInt(body.get("quantity").toString());
        Product prod = prodOpt.get();
        prod.setQuantity(newQty);
        Product saved = productRepository.save(prod);

        Map<String, Object> res = new HashMap<>();
        res.put("status", true);
        res.put("message", "Stock updated successfully for " + prod.getName());
        res.put("product", saved);
        return ResponseEntity.ok(res);
    }

    @GetMapping("/stats")
    public ResponseEntity<?> getInventoryStats() {
        List<Product> products = productRepository.findAll();

        long totalSkus = products.size();
        long lowStockCount = 0;
        long outOfStockCount = 0;
        long totalUnits = 0;

        for (Product p : products) {
            int qty = p.getQuantity() != null ? p.getQuantity() : 0;
            totalUnits += qty;
            if (qty <= 0) {
                outOfStockCount++;
            } else if (qty <= 5) {
                lowStockCount++;
            }
        }

        Map<String, Object> stats = new HashMap<>();
        stats.put("totalSkus", totalSkus);
        stats.put("lowStockCount", lowStockCount);
        stats.put("outOfStockCount", outOfStockCount);
        stats.put("totalUnits", totalUnits);

        return ResponseEntity.ok(stats);
    }
}

