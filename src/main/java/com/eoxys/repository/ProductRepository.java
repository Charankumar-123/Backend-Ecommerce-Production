package com.eoxys.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.eoxys.entity.Product;
@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
	
	
	@Query("SELECT p FROM Product p WHERE p.id = :productId")
	Optional<Product> getByProductId(@Param("productId") Long productId);
	

//	Optional<Product> getByProductId(Long id);

	Product save(Optional<Product> existingProduct);
	
	

}
