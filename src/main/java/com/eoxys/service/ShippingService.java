package com.eoxys.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.eoxys.entity.ShippingEntity;
import com.eoxys.repository.ShippingRepository;

@Service
public class ShippingService {
	
	@Autowired
	private ShippingRepository shippingRepository;
	
	//create a new shipping record 
	public ShippingEntity createShipping(ShippingEntity shippingEntity) {
		ShippingEntity shipping = new ShippingEntity();
		shipping.setOrderId(shippingEntity.getOrderId());
		shipping.setTrackingNumber(shippingEntity.getTrackingNumber());
		shipping.setCarrier(shippingEntity.getCarrier());
		shipping.setEstimatedDeliveryDate(shippingEntity.getEstimatedDeliveryDate());
		shipping.setStatus(shippingEntity.getStatus());
		
		return shippingRepository.save(shipping);
		
	}
	
	public Optional<ShippingEntity> getShippingByOrderId(Long orderId){
		return shippingRepository.findByOrderId(orderId);
	}
	
	public List<ShippingEntity> getShippingAll() {
        return shippingRepository.findAll();
    }
	
	public Optional<ShippingEntity> updateShippingStatus(Long shippingId, String newStatus){
		Optional<ShippingEntity> shipping = shippingRepository.findById(shippingId);
		if(shipping.isPresent()) {
			shipping.get().setStatus(newStatus);
			return Optional.of(shippingRepository.save(shipping.get()));
		}
		return Optional.empty();
	}

}
