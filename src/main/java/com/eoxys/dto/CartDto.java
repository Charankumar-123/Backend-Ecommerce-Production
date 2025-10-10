package com.eoxys.dto;

import jakarta.persistence.Column;

public class CartDto {
	@Column(name="user_id")
	 Long userId;
	private Long productId;
	private Long quantity = 1L;
	public Long getUserId() {
		return userId;
	}
	public void setUserId(Long userId) {
		this.userId = userId;
	}
	public Long getProductId() {
		return productId;
	}
	public void setProductId(Long productId) {
		this.productId = productId;
	}
	public Long getQuantity() {
		return quantity;
	}
	public void setQuantity(Long quantity) {
		this.quantity = quantity;
	}
	@Override
	public String toString() {
		return "CartDto [userId=" + userId + ", productId=" + productId + ", quantity=" + quantity + "]";
	}
	public CartDto(Long userId, Long productId, Long quantity) {
		super();
		this.userId = userId;
		this.productId = productId;
		this.quantity = quantity;
	}
	public CartDto() {
		super();
	}
	
	

}
