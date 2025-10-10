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
import com.eoxys.utils.OrderStatus;
import com.eoxys.utils.PaymentStatus;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;

@Service
public class PaymentService {
	
	 @PersistenceContext
	    private EntityManager entityManager;
	
	@Autowired
	private PaymentRepository paymentRepository;
	
	@Autowired
	private OrdersRepository orderRepository;
	
	public PaymentEntity createPayment(PaymentEntity paymentEntity) {
		Optional<OrdersEntity> optionalOrder = orderRepository.findById(paymentEntity.getOrderId());
		
		// If order is not found, throw an exception
		if (optionalOrder.isPresent()) {
//			throw new RuntimeException("The order ID is not found in the history");
		}
		
		OrdersEntity order = optionalOrder.get(); // Extract OrdersEntity from Optional
		
		// Create a new PaymentEntity and set properties
		PaymentEntity orderPayment = new PaymentEntity();
		orderPayment.setOrderId(order.getOrderId());
		orderPayment.setPaymentStatus(PaymentStatus.PENDING); // Example: Initial payment status
		orderPayment.setAmount(paymentEntity.getAmount()); // Copy amount from input
		
		// Save the new PaymentEntity and return it
		return paymentRepository.save(orderPayment);
	}
	
	@Transactional
	public Response updatePaymentStatus(Long orderId) {
		Response response = new Response();
		Optional<OrdersEntity>  optionalOrder = orderRepository.findById(orderId);
		if(optionalOrder.isEmpty()) {
			response.setStatus(false);
			response.setMessage("order id is not found");
			response.setData(optionalOrder);
			return response;
			
		}
		OrdersEntity order = optionalOrder.get();
		
		Optional<PaymentEntity> optionalPayment = paymentRepository.findByOrderId(orderId);
	    
//	    if (optionalPayment.isPresent()) {
//	        response.setStatus(true);
//	        response.setMessage("payment found for this order!");
//	        response.setData(optionalPayment);
//	        return response;
//	    }

	    if (optionalPayment.isEmpty()) {
	        response.setStatus(false);
	        response.setMessage("No payment found for this order!");
	        response.setData(null);
	        return response;
	    }
	    PaymentEntity payment = optionalPayment.get();
		
	    
//	    if (order.getStatus().equalsIgnoreCase("ORDERED")) {
//	        payment.setPaymentStatus(PaymentStatus.success);
//	    } else if (order.getStatus().equalsIgnoreCase("CANCELLED")) {
//	        payment.setPaymentStatus(PaymentStatus.fail);
//	    } else {
//	        payment.setPaymentStatus(PaymentStatus.Pending);
//	    }
	    
	    if (order.getStatus() == OrderStatus.ORDERED || order.getStatus() == OrderStatus.PROCESSING) {
	        payment.setPaymentStatus(PaymentStatus.SUCCESS);
	    } else if (order.getStatus() == OrderStatus.CANCELLED || order.getStatus() == OrderStatus.RETURNED) {
	        payment.setPaymentStatus(PaymentStatus.FAIL);
	    } else {
	        payment.setPaymentStatus(PaymentStatus.PENDING);
	    }

	    
	    PaymentEntity updatedPayment = paymentRepository.save(payment);
	    entityManager.refresh(updatedPayment);

	   
	    response.setStatus(true);
        response.setMessage("Payment status updated successfully!");
        response.setData(updatedPayment);
       
        paymentRepository.save(updatedPayment);
        return response;
		
		
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
