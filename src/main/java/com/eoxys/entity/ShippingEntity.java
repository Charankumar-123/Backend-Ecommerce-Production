package com.eoxys.entity;

    import jakarta.persistence.*;
	import java.util.Date;
import java.util.List;

	@Entity
	@Table(name = "tbl_shipping")
	public class ShippingEntity {

	    @Id
	    @GeneratedValue(strategy = GenerationType.IDENTITY)
	    @Column(name = "shipping_id")
	    private Long shippingId;

	    @Column(name = "order_id", nullable = false)
	    private Long orderId;

	    @Column(name = "tracking_number", unique = true)
	    private String trackingNumber;

	    @Column(name = "carrier")
	    private String carrier = "fedx"; // e.g., FedEx, UPS

	    @Column(name = "estimated_delivery_date")
	    @Temporal(TemporalType.DATE)
	    private Date estimatedDeliveryDate;

	    @Column(name = "status")
	    private String status; // "Processing", "Shipped", "Delivered"
	    
	    @OneToOne(fetch = FetchType.EAGER, cascade = {CascadeType.DETACH, CascadeType.PERSIST, CascadeType.REFRESH})
	    @JoinColumn(name="order_id", referencedColumnName = "order_id", insertable = false, nullable = true, updatable = false)
	    private OrdersEntity orderShipping;

	    public ShippingEntity() {}

	    public ShippingEntity(Long orderId, String trackingNumber, String carrier, Date estimatedDeliveryDate, String status) {
	        this.orderId = orderId;
	        this.trackingNumber = trackingNumber;
	        this.carrier = carrier;
	        this.estimatedDeliveryDate = estimatedDeliveryDate;
	        this.status = status;
	    }

		public Long getShippingId() {
			return shippingId;
		}

		public void setShippingId(Long shippingId) {
			this.shippingId = shippingId;
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
			return carrier;
		}

		public void setCarrier(String carrier) {
			this.carrier = carrier;
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

		public OrdersEntity getOrderShipping() {
			return orderShipping;
		}

		public void setOrderShipping(OrdersEntity orderShipping) {
			this.orderShipping = orderShipping;
		}


	    

	
	    
	}



