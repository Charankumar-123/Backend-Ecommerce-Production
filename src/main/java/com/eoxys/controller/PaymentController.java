package com.eoxys.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.eoxys.dto.Response;
import com.eoxys.entity.PaymentEntity;
import com.eoxys.service.PaymentService;

@RestController
public class PaymentController {
	
	@Autowired
	private PaymentService paymentService;
	
	@PostMapping("/addpayment")
	public ResponseEntity<PaymentEntity> createPayment(@RequestBody PaymentEntity paymentEntity){
		return new ResponseEntity<>(paymentService.createPayment(paymentEntity),HttpStatus.ACCEPTED);
	}
	
//	@GetMapping("/getorderpayments")
//    public ResponseEntity<List<PaymentEntity>> getPayments() {
//        List<PaymentEntity> payments =  (List<PaymentEntity>) paymentService.getPayments();
//        if (payments.isEmpty()) {
//            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(payments);
//        }
//        return ResponseEntity.ok(payments);
//    }
	
//	@GetMapping("/getorderpayments")
//	public List<Response> getOrderPayments() {
//	    return  (List<Response>) paymentService.getPayments(); // Ensure this returns a List<Response>
//	}
	
	@GetMapping("getorderpayments")
	public ResponseEntity<Response> getOrderPayments() {
	    Response response = paymentService.getPayments(); // Directly return Response object
	    return ResponseEntity.ok(response);
	}
	
	@PutMapping("/update/{orderId}")
    public Response updatePaymentStatus(@PathVariable Long orderId) {
        return paymentService.updatePaymentStatus(orderId);
    }
}
