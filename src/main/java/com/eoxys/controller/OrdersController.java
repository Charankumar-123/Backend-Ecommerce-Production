package com.eoxys.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
//import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.eoxys.dto.Response;
import com.eoxys.entity.OrderItemsEntity;
import com.eoxys.entity.OrdersEntity;
import com.eoxys.service.OrdersService;

@RequestMapping("/api/orders")
@RestController
public class OrdersController {
	
	@Autowired
	private OrdersService ordersService;

	@PostMapping("/create-order/{userId}")
    public ResponseEntity<OrdersEntity> createOrderOrAddOrder(@PathVariable Long userId) {
        OrdersEntity order = ordersService.createOrderOrAddOrder(userId);
        return ResponseEntity.ok(order);
        }
	
    @GetMapping("/get-orders")
    public ResponseEntity<Response> getOrderList(){
    	return new ResponseEntity<>(ordersService.getOrderList(),HttpStatus.ACCEPTED);
    	
    }
	
}
