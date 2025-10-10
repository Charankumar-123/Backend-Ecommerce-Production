package com.eoxys.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.eoxys.dto.Response;
import com.eoxys.entity.OrdersEntity;
import com.eoxys.entity.PaymentEntity;
import com.eoxys.repository.OrdersRepository;
import com.eoxys.repository.PaymentRepository;
import com.eoxys.utils.PaymentStatus;

@Service
public class PaymentService {
	
	@Autowired
	private PaymentRepository paymentRepository;
	
	@Autowired
	private OrdersRepository orderRepository;
	
	public PaymentEntity createPayment(PaymentEntity paymentEntity) {
		Optional<OrdersEntity> optionalOrder = orderRepository.findById(paymentEntity.getOrderId());
		
		// If order is not found, throw an exception
		if (optionalOrder.isEmpty()) {
//			throw new RuntimeException("The order ID is not found in the history");
		}
		
		OrdersEntity order = optionalOrder.get(); // Extract OrdersEntity from Optional
		
		// Create a new PaymentEntity and set properties
		PaymentEntity orderPayment = new PaymentEntity();
		orderPayment.setOrderId(order.getOrderId());
		orderPayment.setPaymentStatus(PaymentStatus.Pending); // Example: Initial payment status
		orderPayment.setAmount(paymentEntity.getAmount()); // Copy amount from input
		
		// Save the new PaymentEntity and return it
		return paymentRepository.save(orderPayment);
	}
	public Response getPayments() {
		Response response = new Response();
		List<PaymentEntity> orderpayments = paymentRepository.findAll();
		response.setStatus(true);
		response.setMessage("payment history list");
		response.setData(orderpayments);
		return response;
		
		
	}
}
