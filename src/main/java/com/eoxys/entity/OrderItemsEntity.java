package com.eoxys.entity;

import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name="tbl_order_items")
public class OrderItemsEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="order_item_id")
	private Long orderItemId;
	
	@Column(name="order_id")
	private Long orderId;
	
	@Column(name="product_id")
	private Long productId;
	
	@Column(name="quantity")
	private Long quantity;
	
	@Column(name="price")
	private float price;
	
	@ManyToOne(fetch = FetchType.EAGER, cascade = { CascadeType.DETACH, CascadeType.PERSIST, CascadeType.REFRESH })
	@JoinColumn(name="product_id",referencedColumnName ="product_id",insertable = false, nullable = true, updatable = false)
	private Product productOrderInfo;

	public Long getOrderItemId() {
		return orderItemId;
	}

	public void setOrderItemId(Long orderItemId) {
		this.orderItemId = orderItemId;
	}

	public Long getOrderId() {
		return orderId;
	}

	public void setOrderId(Long orderId) {
		this.orderId = orderId;
	}


	public Long getQuantity() {
		return quantity;
	}

	public void setQuantity(Long quantity) {
		this.quantity = quantity;
	}

	public float getPrice() {
		return price;
	}

	public void setPrice(float price) {
		this.price = price;
	}
	

	

	public Long getProductId() {
		return productId;
	}

	public void setProductId(Long productId) {
		this.productId = productId;
	}

	public Product getProductOrderInfo() {
		return productOrderInfo;
	}

	public void setProductOrderInfo(Product productOrderInfo) {
		this.productOrderInfo = productOrderInfo;
	}

	@Override
	public String toString() {
		return "OrderItemsEntity [orderItemId=" + orderItemId + ", orderId=" + orderId + ", productId=" + productId
				+ ", quantity=" + quantity + ", price=" + price + ", productOrderInfo=" + productOrderInfo + "]";
	}

	public OrderItemsEntity(Long orderItemId, Long orderId, Long quantity, float price) {
		super();
		this.orderItemId = orderItemId;
		this.orderId = orderId;
		this.quantity = quantity;
		this.price = price;
	}

	public OrderItemsEntity() {
		super();
	}
	
	

}
