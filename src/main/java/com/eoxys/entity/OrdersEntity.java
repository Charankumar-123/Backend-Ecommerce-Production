package com.eoxys.entity;
import java.util.List;
import java.util.Set;

import com.eoxys.utils.OrderStatus;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "tbl_orders")
public class OrdersEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_id")
    private Long orderId;

    @Column(name = "user_id")
    private Long userId;
    
//    private String status = "ORDERED";
    @Enumerated(EnumType.STRING) // Store the status as a string in DB
    private OrderStatus status = OrderStatus.ORDERED;
    
    private float totalPrice; 

    @Column(name = "delivery_address", length = 1000)
    private String deliveryAddress;

    @Column(name = "delivery_city")
    private String deliveryCity;

    @Column(name = "delivery_pincode")
    private String deliveryPincode;

    @Column(name = "delivery_state")
    private String deliveryState;

    @Column(name = "latitude")
    private Double latitude;

    @Column(name = "longitude")
    private Double longitude;

    @Column(name = "recipient_name")
    private String recipientName;

    @Column(name = "recipient_phone")
    private String recipientPhone;

    @ManyToOne(fetch = FetchType.EAGER, cascade = {CascadeType.DETACH, CascadeType.PERSIST, CascadeType.REFRESH})
    @JoinColumn(name = "user_id", referencedColumnName = "user_id", insertable = false, nullable = true, updatable = false)
    private UsersEntity userOrderInfo;

//    @ManyToMany(fetch = FetchType.EAGER, cascade = {CascadeType.DETACH, CascadeType.PERSIST, CascadeType.REFRESH})
//    @JoinTable(
//        name = "Order_Product_Table",
//        joinColumns = @JoinColumn(name = "order_id", referencedColumnName = "order_id"),
//        inverseJoinColumns = @JoinColumn(name = "product_id", referencedColumnName = "product_id")
//    )
//    private Set<Product> products;
    
    @OneToMany(fetch = FetchType.EAGER, cascade = {CascadeType.DETACH, CascadeType.PERSIST, CascadeType.REFRESH})
    @JoinColumn(name="order_id", referencedColumnName = "order_id", insertable = false, nullable = true, updatable = false)
    private List<OrderItemsEntity> orderItemsInfo;
    
    

    // Getters and Setters
    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

   
	public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

	public float getTotalPrice() {
		return totalPrice;
	}

	public void setTotalPrice(float totalPrice) {
		this.totalPrice = totalPrice;
	}

	public UsersEntity getUserOrderInfo() {
        return userOrderInfo;
    }

    public void setUserOrderInfo(UsersEntity userOrderInfo) {
        this.userOrderInfo = userOrderInfo;
    }
    public OrderStatus getStatus() {
        return status;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }
    
    public List<OrderItemsEntity> getOrderItemsInfo() {
        return orderItemsInfo;
    }

    public void setOrderItemsInfo(List<OrderItemsEntity> orderItemsInfo) {
        this.orderItemsInfo = orderItemsInfo;
    }

    public String getDeliveryAddress() {
        return deliveryAddress;
    }

    public void setDeliveryAddress(String deliveryAddress) {
        this.deliveryAddress = deliveryAddress;
    }

    public String getDeliveryCity() {
        return deliveryCity;
    }

    public void setDeliveryCity(String deliveryCity) {
        this.deliveryCity = deliveryCity;
    }

    public String getDeliveryPincode() {
        return deliveryPincode;
    }

    public void setDeliveryPincode(String deliveryPincode) {
        this.deliveryPincode = deliveryPincode;
    }

    public String getDeliveryState() {
        return deliveryState;
    }

    public void setDeliveryState(String deliveryState) {
        this.deliveryState = deliveryState;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public String getRecipientName() {
        return recipientName;
    }

    public void setRecipientName(String recipientName) {
        this.recipientName = recipientName;
    }

    public String getRecipientPhone() {
        return recipientPhone;
    }

    public void setRecipientPhone(String recipientPhone) {
        this.recipientPhone = recipientPhone;
    }


	

	

	public OrdersEntity(Long orderId, Long userId, OrderStatus status,
			float totalPrice, UsersEntity userOrderInfo,
			List<OrderItemsEntity> orderItemsInfo) {
		super();
		this.orderId = orderId;
		this.userId = userId;
		this.status = status;
		this.totalPrice = totalPrice;
		this.userOrderInfo = userOrderInfo;
		this.orderItemsInfo = orderItemsInfo;
	}

	public OrdersEntity() {
		super();
	}
    
}
