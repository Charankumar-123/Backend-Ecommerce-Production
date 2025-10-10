package com.eoxys.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.eoxys.entity.OrderTrackingEntity;

@Repository
public interface OrderTrackingRepository extends JpaRepository<OrderTrackingEntity, Long> {
	OrderTrackingEntity findByOrderId(Long orderId); 

}
