package com.eoxys.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.eoxys.entity.PaymentEntity;

@Repository
public interface PaymentRepository extends JpaRepository<PaymentEntity, Long> {
//	List<PaymentEntity> findByOrderId(Long orderId);
	Optional<PaymentEntity> findByOrderId(Long orderId);
	

}
