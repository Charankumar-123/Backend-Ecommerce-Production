package com.eoxys.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.eoxys.dto.Response;
import com.eoxys.entity.UsersEntity;
import com.eoxys.repository.UsersRepository;
import com.eoxys.utils.UserRole;

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
		return signUp(userEntity);
	}

	public Response gettingUser() {
		Response userjson = new Response();
		List<UsersEntity> userList = usersRepository.findAll();
		userjson.setStatus(true);
		userjson.setMessage("Users retrieved successfully");
		userjson.setData(userList);
		return userjson;
	}
	
	public UsersEntity findByEmail(String email) {
        if (email == null) return null;
        return usersRepository.findByEmailIgnoreCase(email.trim()).orElse(null);
    }

	public Response validationUser(String email, String password) {
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        Response response = new Response();

        if (email == null || email.trim().isEmpty()) {
            response.setStatus(false);
            response.setMessage("Please enter an email address");
            response.setData(null);
            return response;
        }

        String cleanEmail = email.trim().toLowerCase();
        Optional<UsersEntity> userOptional = usersRepository.findByEmailIgnoreCase(cleanEmail);

        if (userOptional.isEmpty()) {
            response.setStatus(false);
            response.setMessage("Account not found with email: " + cleanEmail + ". Please sign up first.");
            response.setData(null);
            return response;
        }

        UsersEntity user = userOptional.get();

        String rawPassword = (password != null) ? password.trim() : "";
        boolean passwordMatches = false;
        try {
            passwordMatches = !rawPassword.isEmpty() && passwordEncoder.matches(rawPassword, user.getPassword());
        } catch (Exception ex) {
            passwordMatches = false;
        }

        // Fallback for legacy plain-text passwords in database
        if (!passwordMatches && user.getPassword() != null) {
            String dbPass = user.getPassword().trim();
            if (dbPass.equals(rawPassword)) {
                passwordMatches = true;
                // Auto-upgrade legacy plain-text password to BCrypt hash
                user.setPassword(passwordEncoder.encode(rawPassword));
                usersRepository.save(user);
            }
        }

        if (!passwordMatches) {
            response.setStatus(false);
            response.setMessage("Invalid password. Please check your credentials.");
            response.setData(null);
            return response;
        }

        // Standardize dynamic user role and generate JWT token
        String standardizedRole = UserRole.fromString(user.getRole()).name();
        String token = jwtService.generateToken(user.getEmail(), standardizedRole);
        System.out.println("Dynamic token generated for user: " + user.getEmail() + " with role: " + standardizedRole);

        // Prepare response data
        String effectiveName = (user.getUserName() != null && !user.getUserName().trim().isEmpty())
                ? user.getUserName()
                : (user.getEmail() != null ? user.getEmail().split("@")[0] : "User");

        Map<String, Object> responseData = new HashMap<>();
        responseData.put("token", token);
        responseData.put("userID", user.getUserID());
        responseData.put("userId", user.getUserID());
        responseData.put("userName", effectiveName);
        responseData.put("name", effectiveName);
        responseData.put("email", user.getEmail());
        responseData.put("mobile", user.getMobile());
        responseData.put("role", standardizedRole);

        response.setStatus(true);
        response.setMessage("Login successful");
        response.setData(responseData);

        return response;
    }

	@Transactional
	public Response signUp(UsersEntity userEntity) {
		Response response = new Response();
		try {
			if (userEntity.getEmail() == null || userEntity.getEmail().trim().isEmpty()) {
				response.setStatus(false);
				response.setMessage("Email address is required");
				return response;
			}

			String cleanEmail = userEntity.getEmail().trim().toLowerCase();
			userEntity.setEmail(cleanEmail);

			Optional<UsersEntity> existingUser = usersRepository.findByEmailIgnoreCase(cleanEmail);
			if (existingUser.isPresent()) {
				response.setStatus(false);
				response.setMessage("User already exists with email: " + cleanEmail);
				return response;
			}

			String roleInput = (userEntity.getRole() == null || userEntity.getRole().trim().isEmpty())
					? "CUSTOMER"
					: userEntity.getRole().trim();
			userEntity.setRole(UserRole.fromString(roleInput).name());

			if (userEntity.getPassword() != null && !userEntity.getPassword().trim().isEmpty()) {
				BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
				userEntity.setPassword(passwordEncoder.encode(userEntity.getPassword().trim()));
			}

			UsersEntity registerUser = usersRepository.save(userEntity);
			response.setStatus(true);
			response.setMessage("Account successfully created as " + userEntity.getRole());
			response.setData(registerUser);
			return response;

		} catch (DataIntegrityViolationException e) {
			response.setStatus(false);
			response.setMessage("Database integrity violation: " + e.getMessage());
			response.setData(null);
			return response;
		} catch (Exception e) {
			response.setStatus(false);
			response.setMessage("Registration error: " + e.getMessage());
			response.setData(null);
			return response;
		}
	}

	public String verify(UsersEntity user) {
	    try {
	        Optional<UsersEntity> existingUser = usersRepository.findByEmailIgnoreCase(user.getEmail());

	        if (existingUser.isEmpty()) {
	            return "User not found";
	        }

	        UsersEntity dbUser = existingUser.get();
	        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

	        if (!passwordEncoder.matches(user.getPassword(), dbUser.getPassword())) {
	            return "Invalid password";
	        }

	        Authentication authentication = authManager.authenticate(
	            new UsernamePasswordAuthenticationToken(user.getEmail(), user.getPassword())
	        );

	        if (authentication.isAuthenticated()) {
	            return jwtService.generateToken(user.getEmail(), dbUser.getRole());
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
