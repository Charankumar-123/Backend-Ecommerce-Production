package com.eoxys.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
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

import com.eoxys.dto.Response;
import com.eoxys.entity.Product;
import com.eoxys.service.ProductService;

@RequestMapping("/products")
@RestController
public class ProductController {
	
	@Autowired
	private ProductService productService;
	
	@PostMapping("/addproducts")
	@CrossOrigin(origins = "http://localhost:3000")
	public ResponseEntity<Product> addProduct(@RequestBody Product product){
		return new ResponseEntity<>(productService.addProduct(product), HttpStatus.ACCEPTED);
	}
	
	@GetMapping("/getproducts")
	@CrossOrigin(origins = "http://localhost:3000")
	public ResponseEntity<List<Product>> getProduct(){
		return new ResponseEntity<>(productService.getProduct(), HttpStatus.ACCEPTED);
		
	}
	
//	@PutMapping("/updateProducts")
//	public ResponseEntity<Product> updateProduct(PathVariable Long Id, @RequestBody Product updatedProduct){
//		return ResponseEntity<>(productService.updateProduct(Id, updatedProduct));
//		
//		   
//	}
	@PutMapping("/update/{id}")
	@CrossOrigin(origins = "http://localhost:3000")
	public ResponseEntity<Response> updateProduct(@PathVariable("id") Long id, @RequestBody Product product){
		Response updatedEntity = productService.updateProduct(id, product);
		
		if(updatedEntity.isStatus()) {
			return ResponseEntity.ok(updatedEntity);
		}else {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(updatedEntity);
		}
		
	}
	
	@DeleteMapping("/delete/{id}")
	@CrossOrigin(origins = "http://localhost:3000")
	public ResponseEntity<Response> deleteProduct(@PathVariable("id") Long id){
		Response deletedProduct = productService.deleteProduct(id);
		
		if(deletedProduct.isStatus()) {
			return ResponseEntity.ok(deletedProduct);
		}else {
			return (ResponseEntity<Response>) ResponseEntity.status(HttpStatus.NOT_FOUND);
		}
		
	}
	

}

