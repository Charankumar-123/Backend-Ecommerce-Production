package com.eoxys.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.eoxys.entity.Product;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
	
	@Query("SELECT p FROM Product p WHERE p.productId = :productId")
	Optional<Product> getByProductId(@Param("productId") Long productId);

	List<Product> findBySellerId(Long sellerId);

	long countBySellerId(Long sellerId);

	Product save(Optional<Product> existingProduct);
}
