package com.eoxys.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.eoxys.dto.CartDto;
import com.eoxys.entity.CartEntity;
import com.eoxys.entity.Product;
import com.eoxys.service.CartService;




@RestController
public class CartController {
	
	@Autowired
	private CartService cartService;
	
	@PostMapping("/create-cart-or-update/{userId}")
    public ResponseEntity<CartEntity> createOrUpdateCart(@RequestBody CartDto cartDto) {
        CartEntity cart = cartService.createOrUpdateCart(cartDto);
        return ResponseEntity.ok(cart);
	}
	@GetMapping("/get-cart-items")
	public ResponseEntity<List<CartEntity>> getCartItems() {
	    List<CartEntity> cartItems = cartService.getCartItems();
	    return ResponseEntity.ok(cartItems);
	}
	
	
    @GetMapping("/get-listof-cart-items/{userId}") 
    public ResponseEntity<List<Product>> getProductsByUserId(@PathVariable Long userId) {
        List<Product> products = cartService.getProductsByUserId(userId); 
        return ResponseEntity.ok(products); 
    }
}
	
//	@GetMapping("/gettingcart")
//	public ResponseEntity<CartEntity> gettingCart(Long userId){
//		return new ResponseEntity<>(cartService.gettingCart(userId),HttpStatus.ACCEPTED);
//}
//	
//	@PostMapping("/additemstocart")
//	public ResponseEntity<CartEntity> addItemToCart(Long userId){
//		CartEntity updatedCart = cartService.addItemToCart(userId);
//		return  new ResponseEntity<>(updatedCart,HttpStatus.OK);
//	}
	
	

