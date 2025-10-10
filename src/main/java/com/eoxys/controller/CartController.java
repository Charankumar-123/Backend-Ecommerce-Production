package com.eoxys.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
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
	
	  @PostMapping("/addToCart")
	  @CrossOrigin(origins = "http://localhost:3000")
	    public ResponseEntity<CartEntity> addToCart(@RequestBody CartDto cartDto) {
	        CartEntity updatedCart = cartService.addToCart(cartDto);
	        return ResponseEntity.ok(updatedCart);
	    }

	    /** 
	     * API to get all cart items for a user.
	     */
	    
	  @GetMapping("/get-all-cart-items/{userId}")
	  @CrossOrigin(origins = "http://localhost:3000")
	  public ResponseEntity<List<CartEntity>> getCartItems(@PathVariable Long userId) {
	      List<CartEntity> cartItems = cartService.getCartItemsByUserId(userId); // Corrected to return CartEntity
	      System.out.println("cartItems => " + cartItems);
	      return ResponseEntity.ok(cartItems);
	  }


	    /** 
	     * API to remove a product from the cart.
	     */
	    @DeleteMapping("/remove/{userId}/{productId}")
	    @CrossOrigin(origins = "http://localhost:3000")
	    public ResponseEntity<String> removeFromCart(@PathVariable Long userId, @PathVariable Long productId) {
	        cartService.removeItemFromCart(userId, productId);
	        return ResponseEntity.ok("Product removed from cart.");
	    }

	    /** 
	     * API to get the total cart price for a user.
	     */
	    @GetMapping("/total/{userId}")
	    @CrossOrigin(origins = "http://localhost:3000")
	    public ResponseEntity<Double> getTotalPrice(@PathVariable Long userId) {
	        double totalPrice = cartService.getTotalCartPrice(userId);
	        return ResponseEntity.ok(totalPrice);
	    }
	    
	    @PutMapping("/updateCartQuantity")
	    @CrossOrigin(origins = "http://localhost:3000")
	    public ResponseEntity<CartEntity> updateCartQuantity(@RequestBody CartDto cartDto) {
	        CartEntity updatedCart = cartService.updateCartQuantity(cartDto);
	        System.out.println("updatedCart => "+updatedCart);
	        return ResponseEntity.ok(updatedCart);
	    }

	}
	
	

