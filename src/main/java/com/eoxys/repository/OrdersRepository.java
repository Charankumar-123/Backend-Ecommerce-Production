package com.eoxys.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.eoxys.entity.OrdersEntity;
@Repository
public interface OrdersRepository extends JpaRepository<OrdersEntity, Long>{
	List<OrdersEntity> findByUserId(Long userId);
	OrdersEntity findByOrderId(Long orderId);

}
