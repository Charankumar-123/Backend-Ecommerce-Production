package com.eoxys.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.eoxys.entity.OrderItemsEntity;

public interface OrderItemsRepository extends JpaRepository<OrderItemsEntity, Long> {
//	OrderItemsEntity  getOrderItemsByOrderId(Long orderItemid);
	List<OrderItemsEntity> findByOrderId(Long orderId);

}
