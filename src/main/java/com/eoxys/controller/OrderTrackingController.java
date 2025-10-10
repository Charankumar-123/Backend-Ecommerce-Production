package com.eoxys.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.eoxys.entity.OrderTrackingEntity;
import com.eoxys.service.OrderTrackingService;

@RestController
public class OrderTrackingController {
	
	@Autowired
	private OrderTrackingService orderTrackingService;
	 
	@GetMapping("/tracking/{orderId}")
	public OrderTrackingEntity getTrackingDetails(@PathVariable Long orderId) {
		return orderTrackingService.getTrackingDetails(orderId);
	}

}
