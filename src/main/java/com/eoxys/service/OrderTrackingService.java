package com.eoxys.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.eoxys.entity.OrderTrackingEntity;
import com.eoxys.repository.OrderTrackingRepository;

@Service
public class OrderTrackingService {
	
	@Autowired
	private OrderTrackingRepository orderTrackingRepository;
	
//	public OrderTrackingEntity getTrackingDetails(Long orderId) {
//		return orderTrackingRepository.findByOrderId(orderId);
//	}
	public OrderTrackingEntity getTrackingDetails(Long orderId) {
        OrderTrackingEntity tracking = orderTrackingRepository.findByOrderId(orderId);
        if (tracking == null) {
            System.out.println("No tracking details found for orderId: " + orderId);
        } else {
            System.out.println("Tracking details: " + tracking);
        }
        return tracking;
    }

}
