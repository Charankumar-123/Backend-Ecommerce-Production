package com.eoxys.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.eoxys.dto.CartDto;
import com.eoxys.entity.CartEntity;
import com.eoxys.entity.Product;
import com.eoxys.repository.CartRepository;
import com.eoxys.repository.ProductRepository;
import com.eoxys.repository.UsersRepository;

@Service
public class CartService {

	@Autowired
	private CartRepository cartRepository;

	@Autowired
	private ProductRepository productRepository;

	@Autowired
	private UsersRepository userRepository;

	public CartEntity addToCart(CartDto cartDto) {
		System.out.println("CartDto Details: UserId=" + cartDto.getUserId() + ", ProductId=" + cartDto.getProductId()
				+ ", Quantity=" + cartDto.getQuantity());

		// Validate product existence
		Optional<Product> productOptional = productRepository.findById(cartDto.getProductId());
		if (!productOptional.isPresent()) {
			throw new RuntimeException("Product with ID " + cartDto.getProductId() + " not found.");
		}

		// Check if the product is already in the cart
		Optional<CartEntity> existingCartOptional = cartRepository.findByUserIdAndProductId(cartDto.getUserId(),
				cartDto.getProductId());

		CartEntity cartEntity;

		Long requestedQuantity = Optional.ofNullable(cartDto.getQuantity()).orElse(1L);
		if (existingCartOptional.isPresent()) {
			// If the product exists, update the quantity
			cartEntity = existingCartOptional.get();
			Long currentQuantity = Optional.ofNullable(cartEntity.getQuantity()).orElse(0L); // Default to 1
			Long newQuantity = currentQuantity + requestedQuantity; // Ensure quantity is at least 1
			cartEntity.setQuantity(newQuantity);
			System.out.println("Updated Cart Quantity: " + newQuantity);
		} else {
			// Create a new cart entry
			cartEntity = new CartEntity();
			cartEntity.setUserId(cartDto.getUserId());
			cartEntity.setProductId(cartDto.getProductId());
			cartEntity.setQuantity(requestedQuantity); // Ensure quantity is at least 1
			System.out.println("Creating New Cart Entry: " + cartEntity);
		}

		return cartRepository.save(cartEntity);
	}

	public boolean checkIfProductInCart(Long userId, Long productId) {
		return cartRepository.findByUserIdAndProductId(userId, productId).isPresent();
	}

	public List<CartEntity> getCartItemsByUserId(Long userId) {
	    List<CartEntity> cartEntities = cartRepository.findByUserId(userId);
	    List<CartEntity> validCartEntities = new ArrayList<>();
	    
	    if (cartEntities.isEmpty()) {
	        System.out.println("No cart items found for userId: " + userId);
	    } else {
	        System.out.println("cartEntities before validation => " + cartEntities);
	    }

	    // Check if the product exists before adding the cart entity to the list
	    for (CartEntity cartEntity : cartEntities) {
	        Optional<Product> productOptional = productRepository.findById(cartEntity.getProductId());
	        if (productOptional.isPresent()) {
	            validCartEntities.add(cartEntity);
	        } else {
	            System.out.println("Product not found for cart item: " + cartEntity);
	        }
	    }

	    System.out.println("Valid cartEntities after validation => " + validCartEntities);
	    return validCartEntities; // Returning only cart items with valid products
	}


	public List<CartEntity> getCartItems() {
		return cartRepository.findAll();
	}

	public void removeItemFromCart(Long userId, Long productId) {
		Optional<CartEntity> cartItemOptional = cartRepository.findByUserIdAndProductId(userId, productId);
		if (cartItemOptional.isPresent()) {
			cartRepository.delete(cartItemOptional.get());
			System.out.println("Removed product " + productId + " from user " + userId + "'s cart.");
		} else {
			throw new RuntimeException("Cart item not found for user " + userId + " and product " + productId);
		}
	}

//    public double getTotalCartPrice(Long userId) {
//        List<CartEntity> cartEntities = cartRepository.findByUserId(userId);
//        double totalPrice = 0.0;
//
//        for (CartEntity cartEntity : cartEntities) {
//            Optional<Product> productOptional = productRepository.findById(cartEntity.getProductId());
//            if (productOptional.isPresent()) {
//            	Long quantity = Optional.ofNullable(cartEntity.getQuantity()).orElse(1L); 
//                totalPrice += productOptional.get().getPrice() * quantity;
//            }
//        }
//
//        return totalPrice;
//    }

	public double getTotalCartPrice(Long userId) {
		List<CartEntity> cartEntities = cartRepository.findByUserId(userId);
		double totalPrice = 0.0;

		for (CartEntity cartEntity : cartEntities) {
			Optional<Product> productOptional = productRepository.findById(cartEntity.getProductId());
			if (productOptional.isPresent()) {
				Long quantity = Optional.ofNullable(cartEntity.getQuantity()).orElse(1L);
				totalPrice += productOptional.get().getPrice() * quantity;
			}
		}

		return totalPrice;
	}

	public CartEntity updateCartQuantity(CartDto cartDto) {
		System.out.println("Received CartDto: " + cartDto);

		Optional<CartEntity> cartEntityOptional = cartRepository.findByUserIdAndProductId(cartDto.getUserId(),
				cartDto.getProductId());
		System.out.println("Cart item found: " + cartEntityOptional.toString());

		if (!cartEntityOptional.isPresent()) {
			throw new RuntimeException("Cart item not found");
		}

		CartEntity cartEntity = cartEntityOptional.get();
		System.out.println("Previous Quantity=>" + cartEntity);

		if (cartDto.getQuantity() > 0) {
			System.out.println("cartDto.getQuantity=>" + cartDto.getQuantity());

			cartEntity.setQuantity(cartDto.getQuantity());
			CartEntity updatedCart = cartRepository.save(cartEntity); // ✅ Save once
			System.out.println("Updated Quantity: " + updatedCart.getQuantity());
			return updatedCart;
		} else {
			cartRepository.delete(cartEntity); // ✅ Remove item if quantity is 0
			System.out.println("Item removed from cart");
			return null;
		}
	}
}
