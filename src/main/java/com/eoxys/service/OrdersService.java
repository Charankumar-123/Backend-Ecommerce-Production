//package com.eoxys.service;

package com.eoxys.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.eoxys.dto.Response;
import com.eoxys.entity.CartEntity;
import com.eoxys.entity.OrderItemsEntity;
import com.eoxys.entity.OrdersEntity;
import com.eoxys.entity.PaymentEntity;
import com.eoxys.entity.Product;
import com.eoxys.repository.CartRepository;
import com.eoxys.repository.OrderItemsRepository;
import com.eoxys.repository.OrdersRepository;
import com.eoxys.repository.PaymentRepository;
import com.eoxys.repository.ProductRepository;
import com.eoxys.utils.OrderStatus;
import com.eoxys.utils.PaymentStatus;

import jakarta.transaction.Transactional;

@Service
public class OrdersService {

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private OrdersRepository orderRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private OrderItemsRepository orderItemsRepository;

    @Autowired
    private PaymentRepository paymentRepository;

    @Transactional
    public Response createOrderOrAddOrder(Long userId) {
        Response response = new Response();

        // Retrieve cart items for the user
        List<CartEntity> cartItems = cartRepository.findByUserId(userId);
        if (cartItems.isEmpty()) {
            response.setStatus(false);
            response.setMessage("The cart is empty.");
            return response;
        }

        OrdersEntity orderEntity = new OrdersEntity();
        orderEntity.setUserId(userId);

        // Calculate total price
        float totalPrice = 0;
        for (CartEntity cartEntity : cartItems) {
            Optional<Product> productInfo = productRepository.findById(cartEntity.getProductId());
            if (productInfo.isPresent()) {
                totalPrice += cartEntity.getQuantity() * productInfo.get().getPrice();
            }
        }

        // Set order status and price
        orderEntity.setTotalPrice(totalPrice);
        orderEntity.setStatus(OrderStatus.ORDERED);

        // Save the order entity
        OrdersEntity savedOrder = orderRepository.save(orderEntity);

        // Create and save order items
        for (CartEntity cartEntity : cartItems) {
            Optional<Product> productInfo = productRepository.findById(cartEntity.getProductId());
            if (productInfo.isPresent()) {
                OrderItemsEntity orderItem = new OrderItemsEntity();
                orderItem.setOrderId(savedOrder.getOrderId());
                orderItem.setProductId(cartEntity.getProductId());
                orderItem.setQuantity(cartEntity.getQuantity());
                orderItem.setPrice(cartEntity.getQuantity() * productInfo.get().getPrice());
                orderItemsRepository.save(orderItem);
            }
        }

        // Remove cart items for the user
        cartRepository.deleteAll(cartItems);

        // Update payment status based on order status
        Optional<PaymentEntity> optionalPayment = paymentRepository.findByOrderId(savedOrder.getOrderId());
        if (optionalPayment.isPresent()) {
            PaymentEntity payment = optionalPayment.get();
            if (savedOrder.getStatus() == OrderStatus.ORDERED) {
                payment.setPaymentStatus(PaymentStatus.SUCCESS);
            } else {
                payment.setPaymentStatus(PaymentStatus.FAIL);
            }
            paymentRepository.save(payment); // Save the updated payment status
        }

        // Set the response
        response.setStatus(true);
        response.setMessage("Order created successfully.");
        response.setData(savedOrder);  // Return the saved order entity in the response

        return response;
    }

    public Response getOrderList(Long userId) {
        Response res = new Response();
        List<OrdersEntity> ordersList = orderRepository.findByUserId(userId);

        for (OrdersEntity order : ordersList) {
            List<OrderItemsEntity> orderItems = orderItemsRepository.findByOrderId(order.getOrderId());
            order.setOrderItemsInfo(orderItems); // Setting order items in order entity
        }

        res.setMessage("Order list fetched successfully");
        res.setStatus(true);
        res.setData(ordersList);
        return res;
    }
}

//
//import java.util.List;
//import java.util.Optional;
//import java.util.Set;
//import java.util.stream.Collectors;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//
//import com.eoxys.dto.Response;
//import com.eoxys.entity.CartEntity;
//import com.eoxys.entity.OrderItemsEntity;
//import com.eoxys.entity.OrdersEntity;
//import com.eoxys.entity.PaymentEntity;
//import com.eoxys.entity.Product;
//import com.eoxys.entity.UsersEntity;
//import com.eoxys.repository.CartRepository;
//import com.eoxys.repository.OrderItemsRepository;
//import com.eoxys.repository.OrdersRepository;
//import com.eoxys.repository.PaymentRepository;
//import com.eoxys.repository.ProductRepository;
//import com.eoxys.repository.UsersRepository;
//import com.eoxys.utils.OrderStatus;
//import com.eoxys.utils.PaymentStatus;
//
//import jakarta.transaction.Transactional;
//
//@Service
//public class OrdersService {
//
//	@Autowired
//	private UsersRepository userRepository;
//
//	@Autowired
//	private CartRepository cartRepository;
//
//	@Autowired
//	private OrdersRepository orderRepository;
//	@Autowired
//	private ProductRepository productRepository;
//	
//	@Autowired
//	private OrderItemsRepository orderItemsRepository;
//	
//	@Autowired
//	private PaymentRepository paymentRepository;
//
////	public Response createOrderOrAddOrder(Long userId) {
////
////		Response response = new Response();
////		// Retrieve cart items for the user
////		List<CartEntity> cartItems = cartRepository.findByUserId(userId);
////		if (cartItems.isEmpty()) {
////			response.setStatus(false);
////			response.setMessage("The cartentity is empty because the items is not there");
//////			response.setData(cartItems);
////		}
////		OrdersEntity orderEntity = new OrdersEntity();
////		orderEntity.setUserId(userId);
////
//////		OrdersEntity savedOrder = orderRepository.save(orderEntity);
////
//////		Double totalPrice = 0.0;
//////		Integer totalQuantity = 0;
////
////		float totalPriceContainer = 0; // Use an array to hold total price
//////		Integer[] totalQuantityContainer = {0}; // Use an array to hold total quantity
////
////		for (CartEntity cartEntity : cartItems) {
////			Optional<Product> productInfor = productRepository.findById(cartEntity.getProductId());
////			totalPriceContainer = totalPriceContainer + (cartEntity.getQuantity() * productInfor.get().getPrice());
////		}
////
//////		orderEntity.setProducts(orderProducts);
////		orderEntity.setTotalPrice(totalPriceContainer);
////		orderEntity.setStatus(OrderStatus.ORDERED);
//////		orderEntity.setTotalQuantity(totalQuantityContainer[0].intValue());
////		OrdersEntity savedOrder = orderRepository.save(orderEntity);
////
////		for (CartEntity cartEntity : cartItems) {
////			Optional<Product> productInfor = productRepository.findById(cartEntity.getProductId());
////			OrderItemsEntity orderItem = new OrderItemsEntity();
////			orderItem.setOrderId(savedOrder.getOrderId());
////			orderItem.setProductId(cartEntity.getProductId());
////			orderItem.setQuantity(cartEntity.getQuantity());
////			orderItem.setPrice(cartEntity.getQuantity() * productInfor.get().getPrice());
////			
////			orderItemsRepository.save(orderItem);
////
////
////		}
////		
////		// Remove cart items for the user
////		cartRepository.deleteAll(cartItems);
////		
////		Optional<PaymentEntity> optionalPayment = paymentRepository.findByOrderId(savedOrder.getOrderId());
////        if (optionalPayment.isPresent()) {
////            PaymentEntity payment = optionalPayment.get();
////            if ("ORDERED".equals(savedOrder.getStatus())) {
////                payment.setPaymentStatus(PaymentStatus.success);
////            } else {
////                payment.setPaymentStatus(PaymentStatus.fail);
////            }
////            paymentRepository.save(payment); // Save the updated payment status
////            response.setStatus(true);
////            response.setMessage("Order and Payment status updated successfully.");
////            response.setData(savedOrder);
////            return response;
////        }
//	
//	@Transactional
//	public Response createOrderOrAddOrder(Long userId) {
//	    Response response = new Response();
//
//	    // Retrieve cart items for the user
//	    List<CartEntity> cartItems = cartRepository.findByUserId(userId);
//	    if (cartItems.isEmpty()) {
//	        response.setStatus(false);
//	        response.setMessage("The cart is empty.");
//	        return response;
//	    }
//
//	    OrdersEntity orderEntity = new OrdersEntity();
//	    orderEntity.setUserId(userId);
//
//	    // Calculate total price
//	    float totalPrice = 0;
//	    for (CartEntity cartEntity : cartItems) {
//	        Optional<Product> productInfo = productRepository.findById(cartEntity.getProductId());
//	        if (productInfo.isPresent()) {
//	            totalPrice += cartEntity.getQuantity() * productInfo.get().getPrice();
//	        }
//	    }
//
//	    // Set order status and price
//	    orderEntity.setTotalPrice(totalPrice);
//	    orderEntity.setStatus(OrderStatus.ORDERED);
//	    
//	    // Save the order entity
//	    OrdersEntity savedOrder = orderRepository.save(orderEntity);
//
//	    // Create and save order items
//	    for (CartEntity cartEntity : cartItems) {
//	        Optional<Product> productInfo = productRepository.findById(cartEntity.getProductId());
//	        if (productInfo.isPresent()) {
//	            OrderItemsEntity orderItem = new OrderItemsEntity();
//	            orderItem.setOrderId(savedOrder.getOrderId());
//	            orderItem.setProductId(cartEntity.getProductId());
//	            orderItem.setQuantity(cartEntity.getQuantity());
//	            orderItem.setPrice(cartEntity.getQuantity() * productInfo.get().getPrice());
//	            orderItemsRepository.save(orderItem);
//	        }
//	    }
//
//	    // Remove cart items for the user
//	    cartRepository.deleteAll(cartItems);
//
//	    // Update payment status based on order status
//	    Optional<PaymentEntity> optionalPayment = paymentRepository.findByOrderId(savedOrder.getOrderId());
//	    if (optionalPayment.isPresent()) {
//	        PaymentEntity payment = optionalPayment.get();
//	        if ("ORDERED".equals(savedOrder.getStatus())) {
//	            payment.setPaymentStatus(PaymentStatus.success);
//	        } else {
//	            payment.setPaymentStatus(PaymentStatus.fail);
//	        }
//	        paymentRepository.save(payment); // Save the updated payment status
//	    }
//
//	    // Set the response
//	    response.setStatus(true);
//	    response.setMessage("Order created successfully.");
//	    response.setData(savedOrder);  // Return the saved order entity in the response
//
//	    return response;
//	}
//
//        
//        
//        
//
//		
//		
////		public Optional<OrdersEntity> getOrders(Long orderId) {
////		    return orderRepository.findByOrderId(orderId);
////		}
//	
//		
//		
//		
//		
////		public Response getOrderList(Long userId) {
////			Response res = new Response();
////			List<OrdersEntity> ordersList = orderRepository.findByUserId(userId);
////			System.out.println("ordersList" + ordersList);
////			res.setMessage("orderlist getting successfullly");
////			res.setStatus(true);
////			res.setData(ordersList);
////			return res;
////			
////		}
//	
//	public Response getOrderList(Long userId) {
//        Response res = new Response();
//        List<OrdersEntity> ordersList = orderRepository.findByUserId(userId);
//
//        for (OrdersEntity order : ordersList) {
//            List<OrderItemsEntity> orderItems = orderItemsRepository.findByOrderId(order.getOrderId());
////            order.setOrderItemsInfo(orderItems); // Setting order items in order entity
//        }
//
//        res.setMessage("Order list fetched successfully");
//        res.setStatus(true);
//        res.setData(ordersList);
//        return res;
//    }
//}
//}
//}
//
//
//
//	
//
//
//
//
