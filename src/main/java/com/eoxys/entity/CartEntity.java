package com.eoxys.entity;

import java.util.ArrayList;
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
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "tbl_cart")
public class CartEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "cart_id")
    private Long cartId;
    
    @Column(name="product_id")
    private Long productId;
    
    @Column(name = "user_id") //reference only
    private Long userId;
    @Column(name="quantity")
    private Long quantity;

    
    @ManyToOne(fetch = FetchType.EAGER, cascade = { CascadeType.DETACH, CascadeType.PERSIST, CascadeType.REFRESH })
	@JoinColumn(name = "user_id", referencedColumnName = "user_id", insertable = false, nullable = true, updatable = false)
	private UsersEntity userInfo;
    
    @ManyToOne(fetch = FetchType.EAGER, cascade = { CascadeType.DETACH, CascadeType.PERSIST, CascadeType.REFRESH })
	@JoinColumn(name = "product_id", referencedColumnName = "product_id", insertable = false, nullable = true, updatable = false)
	private Product productInfo;


	public Long getCartId() {
		return cartId;
	}


	public void setCartId(Long cartId) {
		this.cartId = cartId;
	}


	public Long getProductId() {
		return productId;
	}


	public void setProductId(Long productId) {
		this.productId = productId;
	}

	public Long getUserId() {
		return userId;
	}


	public void setUserId(Long userId) {
		this.userId = userId;
	}


	public UsersEntity getUserInfo() {
		return userInfo;
	}


	public void setUserInfo(UsersEntity userInfo) {
		this.userInfo = userInfo;
	}
	


	public Product getProductInfo() {
		return productInfo;
	}


	public void setProductInfo(Product productInfo) {
		this.productInfo = productInfo;
	}
	
	


	public Long getQuantity() {
		return quantity;
	}


	public void setQuantity(Long quantity) {
		this.quantity = quantity;
	}


	


	


	public CartEntity(Long cartId, Long productId, Long userId, Long quantity, UsersEntity userInfo,
			Product productInfo) {
		super();
		this.cartId = cartId;
		this.productId = productId;
		this.userId = userId;
		this.quantity = quantity;
		this.userInfo = userInfo;
		this.productInfo = productInfo;
	}





	@Override
	public String toString() {
		return "CartEntity [cartId=" + cartId + ", productId=" + productId + ", userId=" + userId + ", quantity="
				+ quantity + ", userInfo=" + userInfo + ", productInfo=" + productInfo + "]";
	}


	public CartEntity() {
		// TODO Auto-generated constructor stub
	}


	public void setQuantity(long l) {
		// TODO Auto-generated method stub
		
	}
    
    
    
    
    
    

    

    

	
}
