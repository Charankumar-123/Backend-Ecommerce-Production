package com.eoxys.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Example;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.eoxys.dto.Response;
import com.eoxys.entity.UsersEntity;
import com.eoxys.repository.UsersRepository;

import jakarta.transaction.Transactional;

@Service
public class UsersService {
	
	@Autowired
	AuthenticationManager authManager;
	
	@Autowired
	private JWTService jwtService;

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
	
	public UsersEntity findByEmail(String email) {
        return usersRepository.findByEmail(email).orElse(null);
    }


	
	public Response validationUser(String email, String password) {
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        Optional<UsersEntity> userOptional = usersRepository.findByEmail(email);
        Response response = new Response();

        if (userOptional.isEmpty()) {
            response.setStatus(false);
            response.setMessage("The email is invalid");
            response.setData(null);
            return response;
        }

        UsersEntity user = userOptional.get();

        if (password == null || !passwordEncoder.matches(password, user.getPassword())) {
            response.setStatus(false);
            response.setMessage("Invalid password");
            response.setData(null);
            return response;
        }

        // Generate JWT token using email
        String token = jwtService.generateToken(user.getEmail());
        System.out.println("token from userservice=>"+token);

        // Prepare response data
        Map<String, Object> responseData = new HashMap<>();
        responseData.put("token", token);
        responseData.put("userID", user.getUserID());
        responseData.put("userName", user.getUserName());
        responseData.put("email", user.getEmail());
        responseData.put("mobile", user.getMobile());
        responseData.put("role", user.getRole());

        response.setStatus(true);
        response.setMessage("Login successful");
        response.setData(responseData);

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
			
			
			BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
	        userEntity.setPassword(passwordEncoder.encode(userEntity.getPassword()));
	        
	        
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



	}
	public String verify(UsersEntity user) {
	    try {
	        // Fetch user from DB to get the hashed password
	        Optional<UsersEntity> existingUser = usersRepository.findByEmail(user.getEmail());

	        if (existingUser.isEmpty()) {
	            return "User not found";
	        }

	        UsersEntity dbUser = existingUser.get();
	        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

	        // Validate password using BCrypt
	        if (!passwordEncoder.matches(user.getPassword(), dbUser.getPassword())) {
	            return "Invalid password";
	        }

	        // Authenticate user
	        Authentication authentication = authManager.authenticate(
	            new UsernamePasswordAuthenticationToken(user.getEmail(), user.getPassword())
	        );

	        if (authentication.isAuthenticated()) {
	            return jwtService.generateToken(user.getEmail());
	        } else {
	            return "Authentication failed";
	        }
	    } catch (BadCredentialsException e) {
	        return "Invalid credentials";
	    } catch (Exception e) {
	        return "Error during authentication: " + e.getMessage();
	    }
	}
		
		
		
	}

