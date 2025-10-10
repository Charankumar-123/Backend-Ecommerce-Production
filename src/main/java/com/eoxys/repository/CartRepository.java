package com.eoxys.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.eoxys.entity.CartEntity;
import com.eoxys.entity.Product;
@Repository
public interface CartRepository extends JpaRepository<CartEntity, Long> {
	
	Optional<CartEntity> findByUserIdAndProductId(Long userId,Long productId);
	
	List<CartEntity> findByUserId(Long userId);

//	List<Product> findByUserId(Long userId);

}
