package com.eoxys.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.eoxys.dto.Response;
import com.eoxys.entity.Product;
import com.eoxys.repository.ProductRepository;

@Service
public class ProductService {

	@Autowired
	private ProductRepository productRepository;

	public Product addProduct(Product productEntity) {
		return productRepository.save(productEntity);
	}

	public List<Product> getProduct() {
		return productRepository.findAll();
	}

	public Response updateProduct(Long id, Product updatedProduct) {
		Optional<Product> existingProductOptional = productRepository.getByProductId(id);
		
		Response response = new Response();
		
		if(existingProductOptional.isPresent()) {
			
			Product existingProduct = existingProductOptional.get();
		
		existingProduct.setName(updatedProduct.getName());
		existingProduct.setDescription(updatedProduct.getDescription());
		existingProduct.setQuantity(updatedProduct.getQuantity());
		existingProduct.setPrice(updatedProduct.getPrice());
		existingProduct.setCategory(updatedProduct.getCategory());
		Product updated = productRepository.save(existingProduct);
		response.setStatus(true);
		response.setMessage("product updated successfully");
		response.setData(updated);
		}
		else {
			response.setStatus(false);
			response.setMessage("The product is not found in the existing database");
			response.setData(null);
		}
		return response;
		

	}
	public Optional<Product> findProductById(Long productId) {
	    return productRepository.getByProductId(productId);  // No need for Optional.ofNullable here
	}
	
	public Response deleteProduct(Long id) {
		Optional<Product> existingProductOptional = productRepository.getByProductId(id);
		
		Response response = new Response();
		
		if(existingProductOptional.isPresent()) {
			
			productRepository.deleteById(id);
			response.setStatus(true);
			response.setMessage("The product is deleted successfully");
			response.setData(null);
			
		}else {
			response.setStatus(false);
			response.setMessage("The product is not there in database to delete that product");
			response.setData(null);
		}
		return response;
	}

//	public void deleteProduct(Long id) {
//		Product product = getProductById(id);
//		productRepository.delete(product);

}
