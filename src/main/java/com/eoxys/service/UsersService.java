package com.eoxys.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Service;

import com.eoxys.dto.Response;
import com.eoxys.entity.UsersEntity;
import com.eoxys.repository.UsersRepository;

import jakarta.transaction.Transactional;

@Service
public class UsersService {

	@Autowired
	private UsersRepository usersRepository;

	@Transactional
	public Response createUser(UsersEntity userEntity) {
		if (usersRepository.existsByEmail(userEntity.getEmail())) {
			Response response = new Response();
			response.setStatus(false);
			response.setMessage("user already exists");
			response.setData(null);
			return response;
		}
		try {
			UsersEntity savedUser = usersRepository.save(userEntity);

			Response response = new Response();
			response.setStatus(true);
			response.setMessage("user successfully created");
			response.setData(savedUser);
			return response;

		} catch (DataIntegrityViolationException e) {
			throw new RuntimeException("error while saving the user");

		}

//		Response userjson = new Response();
//		
//		UsersEntity usersData = usersRepository.save(userEntity);
//		userjson.setStatus(true);
//		userjson.setMessage("user added successfully");
//		userjson.setData(usersData);
//		System.out.println("DATA JSON => " + userjson);
//		return userjson;
	}

	public Response gettingUser() {

		Response userjson = new Response();
//		UsersEntity usersData = usersRepository.save(userEntity);
		List<UsersEntity> userList = usersRepository.findAll();
		System.out.println("user data" + userList);
		userjson.setStatus(true);
		userjson.setMessage("message getting by browser successfully");
		userjson.setData(userList);
		return userjson;

	}

	public Response validationUser(String email, String password) {
		// Step 1: Check if the email exists in the database
		Optional<UsersEntity> userOptional = usersRepository.findByEmail(email);
		Response response = new Response();
      
		if (userOptional.isEmpty()) {
			response.setStatus(false);
			response.setMessage("the above email is invalid");
			response.setData(null);
		} else {
			UsersEntity user = userOptional.get();

			if (password == null) {
				response.setStatus(false);
				response.setMessage("the password is empty");
				response.setData(null);
			} else {
				if (!user.getPassword().equals(password)) {
					response.setStatus(false);
					response.setMessage("the passwords is invalid");
					response.setData(null);
				} else {
					response.setStatus(true);
					response.setMessage("login success");
					response.setData(user);
//					return response;
				}

			}
		}

		// Step 3: Validate the password

		// Step 2: Get the user details from the database

		return response;
	}

	@Transactional
	public Response signUp(UsersEntity userEntity) {
		Response response = new Response();
		try {

			Optional<UsersEntity> existingUser = usersRepository.findByEmail(userEntity.getEmail());
			if (existingUser.isPresent()) {
				response.setStatus(false);
				response.setMessage("User already exists");
				return response;
			}
//    	userEntity.setPassword(passwordEncoder.encode(userEntity.getPassword()));
			UsersEntity registerUser = usersRepository.save(userEntity);
			response.setStatus(true);
			response.setMessage("user is successfully registered");
			response.setData(registerUser);
			return response;

		} catch (DataIntegrityViolationException e) {
			response.setStatus(false);
			response.setMessage("user is violated");
			response.setData(null);
			return response;

		}

//	public UsersEntity createUser(UsersEntity userEntity) {
//		
//		if(usersRepository.existsByEmail(userEntity.getEmail())) {
//			throw new IllegalArgumentException("user already exist in this email");
//		}
//		
//	}

	}
}
