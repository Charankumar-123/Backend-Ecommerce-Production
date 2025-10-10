package com.eoxys.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.eoxys.entity.ShippingEntity;
import com.eoxys.service.ShippingService;

@RestController
public class ShippingController {
	
	@Autowired
	private ShippingService shippingService;
	
	@PostMapping("/create/shipping")
	public ResponseEntity<ShippingEntity> createShipping(@RequestBody ShippingEntity shippingEntity){
		ShippingEntity shipping = shippingService.createShipping(shippingEntity);
		 return ResponseEntity.ok(shipping);
	}
	
	@GetMapping("/shipping/{orderId}")
	public ResponseEntity<Optional<ShippingEntity>> getShippingByOrderId(@PathVariable Long orderId){
		Optional<ShippingEntity> shipping = shippingService.getShippingByOrderId(orderId);
		return ResponseEntity.ok(shipping);
	}
	
	@GetMapping("/all/shipping")
	public ResponseEntity<List<ShippingEntity>> getShippingAll(){
		return ResponseEntity.ok(shippingService.getShippingAll());
	}
	
	@PutMapping("/updateStatus/{shippingId}")
	public ResponseEntity<Optional<ShippingEntity>> updateShippingStatus(@PathVariable Long shippingId, @RequestParam String status){
		return ResponseEntity.ok(shippingService.updateShippingStatus(shippingId,status));
	}
	
	

}
