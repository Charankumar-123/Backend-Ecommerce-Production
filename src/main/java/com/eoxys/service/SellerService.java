package com.eoxys.service;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.eoxys.dto.Response;
import com.eoxys.entity.OrderItemsEntity;
import com.eoxys.entity.Product;
import com.eoxys.repository.OrderItemsRepository;
import com.eoxys.repository.ProductRepository;

@Service
public class SellerService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private OrderItemsRepository orderItemsRepository;

    public List<Product> getSellerProducts(Long sellerId) {
        if (sellerId == null) {
            return List.of();
        }
        return productRepository.findBySellerId(sellerId);
    }

    public Product addSellerProduct(Product product, Long sellerId) {
        product.setSellerId(sellerId);
        return productRepository.save(product);
    }

    public Response updateSellerProduct(Long productId, Product updatedProduct, Long sellerId, boolean isAdmin) {
        Response response = new Response();
        Optional<Product> existingOpt = productRepository.getByProductId(productId);

        if (existingOpt.isEmpty()) {
            response.setStatus(false);
            response.setMessage("Product not found with ID: " + productId);
            return response;
        }

        Product existing = existingOpt.get();
        boolean isOwner = existing.getSellerId() != null && existing.getSellerId().equals(sellerId);

        if (!isAdmin && !isOwner) {
            response.setStatus(false);
            response.setMessage("Access denied: You can only edit your own products.");
            return response;
        }

        if (updatedProduct.getName() != null) existing.setName(updatedProduct.getName());
        if (updatedProduct.getDescription() != null) existing.setDescription(updatedProduct.getDescription());
        if (updatedProduct.getPrice() > 0) existing.setPrice(updatedProduct.getPrice());
        if (updatedProduct.getQuantity() != null) existing.setQuantity(updatedProduct.getQuantity());
        if (updatedProduct.getCategory() != null) existing.setCategory(updatedProduct.getCategory());
        if (updatedProduct.getImage() != null) existing.setImage(updatedProduct.getImage());

        Product saved = productRepository.save(existing);
        response.setStatus(true);
        response.setMessage("Product updated successfully");
        response.setData(saved);
        return response;
    }

    public Response deleteSellerProduct(Long productId, Long sellerId, boolean isAdmin) {
        Response response = new Response();
        Optional<Product> existingOpt = productRepository.getByProductId(productId);

        if (existingOpt.isEmpty()) {
            response.setStatus(false);
            response.setMessage("Product not found with ID: " + productId);
            return response;
        }

        Product existing = existingOpt.get();
        boolean isOwner = existing.getSellerId() != null && existing.getSellerId().equals(sellerId);

        if (!isAdmin && !isOwner) {
            response.setStatus(false);
            response.setMessage("Access denied: You can only delete your own products.");
            return response;
        }

        productRepository.deleteById(productId);
        response.setStatus(true);
        response.setMessage("Product deleted successfully");
        return response;
    }

    public Map<String, Object> getSellerDashboardStats(Long sellerId) {
        Map<String, Object> stats = new HashMap<>();

        long activeListings = productRepository.countBySellerId(sellerId);
        List<OrderItemsEntity> sellerSales = orderItemsRepository.findBySellerId(sellerId);

        double totalRevenue = 0.0;
        long totalUnitsSold = 0;
        Set<Long> uniqueOrders = new HashSet<>();

        for (OrderItemsEntity item : sellerSales) {
            long qty = item.getQuantity() != null ? item.getQuantity() : 1L;
            totalRevenue += (item.getPrice() * qty);
            totalUnitsSold += qty;
            if (item.getOrderId() != null) {
                uniqueOrders.add(item.getOrderId());
            }
        }

        stats.put("sellerId", sellerId);
        stats.put("activeListings", activeListings);
        stats.put("totalRevenue", Math.round(totalRevenue * 100.0) / 100.0);
        stats.put("ordersCount", uniqueOrders.size());
        stats.put("totalUnitsSold", totalUnitsSold);
        stats.put("sellerRating", 4.9);

        return stats;
    }
}

