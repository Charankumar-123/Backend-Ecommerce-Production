package com.eoxys.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.eoxys.entity.OrderItemsEntity;

public interface OrderItemsRepository extends JpaRepository<OrderItemsEntity, Long> {
	List<OrderItemsEntity> findByOrderId(Long orderId);

	@Query("SELECT oi FROM OrderItemsEntity oi WHERE oi.productOrderInfo.sellerId = :sellerId")
	List<OrderItemsEntity> findBySellerId(@Param("sellerId") Long sellerId);
}
