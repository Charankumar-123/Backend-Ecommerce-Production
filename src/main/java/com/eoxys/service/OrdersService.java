package com.eoxys.service;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.eoxys.dto.Response;
import com.eoxys.entity.CartEntity;
import com.eoxys.entity.OrderItemsEntity;
import com.eoxys.entity.OrdersEntity;
import com.eoxys.entity.Product;
import com.eoxys.entity.UsersEntity;
import com.eoxys.repository.CartRepository;
import com.eoxys.repository.OrderItemsRepository;
import com.eoxys.repository.OrdersRepository;
import com.eoxys.repository.ProductRepository;
import com.eoxys.repository.UsersRepository;

import jakarta.transaction.Transactional;

@Service
public class OrdersService {

	@Autowired
	private UsersRepository userRepository;

	@Autowired
	private CartRepository cartRepository;

	@Autowired
	private OrdersRepository orderRepository;
	@Autowired
	private ProductRepository productRepository;
	
	@Autowired
	private OrderItemsRepository orderItemsRepository;

	public OrdersEntity createOrderOrAddOrder(Long userId) {

		Response response = new Response();
		// Retrieve cart items for the user
		List<CartEntity> cartItems = cartRepository.findByUserId(userId);
		if (cartItems.isEmpty()) {
			response.setStatus(false);
			response.setMessage("The cartentity is empty because the items is not there");
//			response.setData(cartItems);
		}
		OrdersEntity orderEntity = new OrdersEntity();
		orderEntity.setUserId(userId);

//		OrdersEntity savedOrder = orderRepository.save(orderEntity);

//		Double totalPrice = 0.0;
//		Integer totalQuantity = 0;

		float totalPriceContainer = 0; // Use an array to hold total price
//		Integer[] totalQuantityContainer = {0}; // Use an array to hold total quantity

		for (CartEntity cartEntity : cartItems) {
			Optional<Product> productInfor = productRepository.findById(cartEntity.getProductId());
			totalPriceContainer = totalPriceContainer + (cartEntity.getQuantity() * productInfor.get().getPrice());
		}

//		orderEntity.setProducts(orderProducts);
		orderEntity.setTotalPrice(totalPriceContainer);
//		orderEntity.setTotalQuantity(totalQuantityContainer[0].intValue());
		OrdersEntity savedOrder = orderRepository.save(orderEntity);

		for (CartEntity cartEntity : cartItems) {
			Optional<Product> productInfor = productRepository.findById(cartEntity.getProductId());
			OrderItemsEntity orderItem = new OrderItemsEntity();
			orderItem.setOrderId(savedOrder.getOrderId());
			orderItem.setProductId(cartEntity.getProductId());
			orderItem.setQuantity(cartEntity.getQuantity());
			orderItem.setPrice(cartEntity.getQuantity() * productInfor.get().getPrice());

		}

		// Remove cart items for the user
		cartRepository.deleteAll(cartItems);

		return savedOrder;
		
//		public Optional<OrdersEntity> getOrders(Long orderId) {
//		    return orderRepository.findByOrderId(orderId);
//		}
	}
		
		
		
		
		public Response getOrderList() {
			Response res = new Response();
			List<OrdersEntity> ordersList = orderRepository.findAll();
			res.setMessage("orderlist getting successfullly");
			res.setStatus(true);
			res.setData(ordersList);
			return res;
			
		}


	
//		Set<OrderItemsEntity> orderItems = orderRepository.findByOrderId(savedOrder);
//		if(orderItems.isEmpty()) {
//			response.setMessage("no items is present");
//		}
//		OrderItemsEntity itemOrder = new OrderItemsEntity();
//		itemOrder.setOrderId(orderEntity.getOrderId());
//		itemOrder.setPrice(totalPriceContainer);
//		itemOrder.setQuantity()

	

//	@Transactional
//	public List<OrdersEntity> getOrdersByUserId(Long userId) {
//		return orderRepository.findByUserId(userId);
//	}
}


