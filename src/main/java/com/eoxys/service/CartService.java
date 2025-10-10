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
public class CartService{
	
	
	@Autowired
	private CartRepository cartRepository;
	
	@Autowired
	private ProductRepository productRepository;
	
	@Autowired
	private UsersRepository userRepository;

	   public CartEntity createOrUpdateCart(CartDto cartDto) {
		   
		   System.out.println("CartDto Quantity: " + cartDto.getQuantity());
		    System.out.println("CartDto UserId: " + cartDto.getUserId());
		    System.out.println("CartDto ProductId: " + cartDto.getProductId());
//		   System.out.println("cartDto" +cartDto);
	        // Check if an entry already exists for this user and product
	        Optional<CartEntity> existingCartOptional = cartRepository.findByUserIdAndProductId(cartDto.getUserId(), cartDto.getProductId());

	        CartEntity cartEntity;

	        if (existingCartOptional.isPresent()) {
	            // Update the quantity if the product already exists in the cart
	            cartEntity = existingCartOptional.get();
	            System.out.println("Existing CartEntity Found: " + cartEntity);
	            System.out.println("Existing Quantity: " + cartEntity.getProductInfo().getQuantity());
	            
	            cartEntity.setQuantity(cartEntity.getProductInfo().getQuantity() + cartDto.getQuantity());
	            System.out.println("Updated Quantity: " + cartEntity.getProductInfo().getQuantity());
	            
	            return cartRepository.save(cartEntity);
	            
	        } else {
	            // Create a new cart entry if the product is not already in the cart
	        	CartEntity cartCreateEntity;
	            cartCreateEntity = new CartEntity();  // new entity
	            cartCreateEntity.setUserId(cartDto.getUserId());
	            cartCreateEntity.setProductId(cartDto.getProductId());
	            cartCreateEntity.setQuantity(cartDto.getQuantity());
	            System.out.println("Creating New CartEntity: " + cartCreateEntity);
	            
	            CartEntity savedEntity =  cartRepository.save(cartCreateEntity);
	            System.out.println("Saved New CartEntity: " + savedEntity);
	            return savedEntity;
	            
	            // make save to db here 
	        }
	        
	 

	        
	    }
	   public List<Product> getProductsByUserId(Long userId) {
		// Fetch the CartEntity list
		   List<CartEntity> cartEntities = cartRepository.findByUserId(userId);
		   
		   // Extract products from the CartEntity list
		   List<Product> products = new ArrayList<>();
		   for(CartEntity cartEntity : cartEntities) {
			   if(cartEntity.getProductInfo()!= null) {
				   products.add(cartEntity.getProductInfo());
				   
			   }
		   }
		   return products;
		   

	    }
	
	
	   
	   public List<CartEntity> getCartItems(){
		   return cartRepository.findAll();
	   }
	
	
//	Response response = new Response();
	
//	public CartEntity createCart(Long userId) {
//		
//		CartEntity newCart = new CartEntity();
//		
//		//checking the user is there or not based on user id we are checking 
//		if(!userRepository.existsById(userId))
//			response.setStatus(false);
//		    response.setMessage("for the above userid the user exists");
//		    response.setData(userId);
//		
//		// above we are retrieving the user for that user we will check the cart is there or not if there is cart present then we retrieve existing cart
//		CartEntity existingCart = cartRepository.findByUserId(userId);
//		if(existingCart != null) {
//			
//			response.setStatus(true);
//			response.setMessage("the cart is there for the above userid");
//			response.setData(existingCart);
//			return existingCart;
//		}else {
//			newCart.setProductInfo(null);
//		newCart.setCartId(userId);
//		newCart.setTotalPrice(0.0);
//		return cartRepository.save(newCart);
//		}
		
		
//	public CartEntity getCartByUserId(Long userId) {
//		CartEntity getCart = cartRepository.findByUserId(userId);
//		return getCart;
//		
//		}

//	public CartEntity getCartByUserId(Long userId) {
//		return cartRepository.findByUserId(userId);
//	}
//		
				
		
////		Response response = new Response();
//		CartEntity cart = new CartEntity();
//    	cart.setUserId(userId);
//		cart.setTotalPrice(0.0);
////		cart.setCreatedDate(LocalDateTime.now());
////		cart.setUpdatedDate(LocalDateTime.now());
//		return cartRepository.save(cart);
//		}
//	
//	public CartEntity getCartByUserId(Long userId) {
//		return cartRepository.findByUserId(userId);
//	}
//	
//	public CartEntity addItemToCart(Long userId) {
//	CartEntity cart = getCartByUserId(userId);
//	if(cart == null) {
//		cart = createCart(userId);
//	}
//	Optional<Product> product = productRepository.findById(item.getProductId());
//	
//	item.setPrice(product.get().getPrice());
//	
//	item.setCart(cart);
//	cart.getItems().add(item);
//	cart.setTotalPrice(cart.getTotalPrice() + (item.getPrice() * item.getQuantity()));
//	return cartRepository.save(cart);
//
//	}
//	
//	public CartEntity updateCartEntityInCart(Long userId,Long itemId,int quantity) {
//		CartEntity cart = getCartByUserId(userId);
//		if(cart != null) {
//			for(CartItem item : cart.getItems()) {
//				if(item.getId().equals(itemId)) {
//					item.setQuantity(quantity);
//					cart.setTotalPrice(cart.getItems().stream().mapToDouble(i -> i.getPrice() * i.getQuantity()).sum());
//					break;
//				}
//			}
//		}
//		return cartRepository.save(cart);
//	}
//	
//	public void removeItemFromCart(Long userId,Long itemId) {
//		CartEntity cart = getCartByUserId(userId);
//		if(cart != null) {
//			cart.getItems().removeIf(item -> item.getId().equals(itemId));
//			cart.setTotalPrice(cart.getItems().stream().mapToDouble(i -> i.getPrice()* i.getQuantity()).sum());
//			cartRepository.save(cart);
//			
//			
//		}
//
//}
}

