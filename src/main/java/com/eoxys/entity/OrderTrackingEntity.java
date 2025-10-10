package com.eoxys.entity;

import java.sql.Date;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

@Entity
@Table(name="tbl_orderTracking")
public class OrderTrackingEntity {
	
	@Id
	 @GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="tracking_id")
	private Long trackingId;
	
	@Column(name="order_id")
	private Long orderId;
	
	private String trackingNumber;
	private String Carrier;
	private Date estimatedDeliveryDate;
	private String status;
	
	@OneToOne(fetch = FetchType.EAGER, cascade = {CascadeType.DETACH, CascadeType.PERSIST, CascadeType.REFRESH})
    @JoinColumn(name = "order_id", referencedColumnName = "order_id", insertable = false, nullable = true, updatable = false)
    private OrdersEntity orderTrackingInfo;

	public Long getTrackingId() {
		return trackingId;
	}

	public void setTrackingId(Long trackingId) {
		this.trackingId = trackingId;
	}

	public Long getOrderId() {
		return orderId;
	}

	public void setOrderId(Long orderId) {
		this.orderId = orderId;
	}

	public String getTrackingNumber() {
		return trackingNumber;
	}

	public void setTrackingNumber(String trackingNumber) {
		this.trackingNumber = trackingNumber;
	}

	public String getCarrier() {
		return Carrier;
	}

	public void setCarrier(String carrier) {
		Carrier = carrier;
	}

	public Date getEstimatedDeliveryDate() {
		return estimatedDeliveryDate;
	}

	public void setEstimatedDeliveryDate(Date estimatedDeliveryDate) {
		this.estimatedDeliveryDate = estimatedDeliveryDate;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public OrdersEntity getOrderTrackingInfo() {
		return orderTrackingInfo;
	}

	public void setOrderTrackingInfo(OrdersEntity orderTrackingInfo) {
		this.orderTrackingInfo = orderTrackingInfo;
	}

	public OrderTrackingEntity(Long trackingId, Long orderId, String trackingNumber, String carrier,
			Date estimatedDeliveryDate, String status, OrdersEntity orderTrackingInfo) {
		super();
		this.trackingId = trackingId;
		this.orderId = orderId;
		this.trackingNumber = trackingNumber;
		Carrier = carrier;
		this.estimatedDeliveryDate = estimatedDeliveryDate;
		this.status = status;
		this.orderTrackingInfo = orderTrackingInfo;
	}

	public OrderTrackingEntity() {
		super();
	}

	@Override
	public String toString() {
		return "OrderTrackingEntity [trackingId=" + trackingId + ", orderId=" + orderId + ", trackingNumber="
				+ trackingNumber + ", Carrier=" + Carrier + ", estimatedDeliveryDate=" + estimatedDeliveryDate
				+ ", status=" + status + ", orderTrackingInfo=" + orderTrackingInfo + "]";
	}
	
	
	
	
	
	
	

}
