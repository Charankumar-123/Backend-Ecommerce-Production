package com.eoxys.entity;	

import com.eoxys.utils.PaymentStatus;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

@Entity
@Table(name="tbl_payment")
public class PaymentEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="payment_id")
	private Long paymentId;
	
	@Column(name="order_id")
	private Long orderId;
	
	private Long amount;
	private PaymentStatus paymentStatus;
	private String paymentMethod = "creditcard";
	
//	mapping between this payment page to order page to display all orders via order id in payment page
	@OneToOne(fetch = FetchType.EAGER, cascade = {CascadeType.DETACH, CascadeType.PERSIST, CascadeType.REFRESH})
    @JoinColumn(name = "order_id", referencedColumnName = "order_id", insertable = false, nullable = true, updatable = false)
    private OrdersEntity order_payment_info;

	
	
	public Long getPaymentId() {
		return paymentId;
	}
	public void setPaymentId(Long paymentId) {
		this.paymentId = paymentId;
	}
	public Long getOrderId() {
		return orderId;
	}
	public void setOrderId(Long orderId) {
		this.orderId = orderId;
	}
	public Long getAmount() {
		return amount;
	}
	public void setAmount(Long amount) {
		this.amount = amount;
	}
	public PaymentStatus getPaymentStatus() {
		return paymentStatus;
	}
	public void setPaymentStatus(PaymentStatus pending) {
		this.paymentStatus = pending;
	}
	public String getPaymentMethod() {
		return paymentMethod;
	}
	public void setPaymentMethod(String paymentMethod) {
		this.paymentMethod = paymentMethod;
	}
	public PaymentEntity(Long paymentId, Long orderId, Long amount, PaymentStatus paymentStatus, String paymentMethod) {
		super();
		this.paymentId = paymentId;
		this.orderId = orderId;
		this.amount = amount;
		this.paymentStatus = paymentStatus;
		this.paymentMethod = paymentMethod;
	}
	public PaymentEntity() {
		super();
	}
	@Override
	public String toString() {
		return "PaymentEntity [paymentId=" + paymentId + ", orderId=" + orderId + ", amount=" + amount
				+ ", paymentStatus=" + paymentStatus + ", paymentMethod=" + paymentMethod + "]";
	}
	
	
	
	

}
