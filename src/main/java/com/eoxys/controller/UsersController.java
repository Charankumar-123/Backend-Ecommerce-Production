package com.eoxys.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.eoxys.dto.LoginRequest;
import com.eoxys.dto.Response;
import com.eoxys.entity.UsersEntity;
import com.eoxys.service.UsersService;

@RequestMapping("users")
@RestController
public class UsersController {

//	private static final String ApiResponse = addusers;
	@Autowired
	private UsersService usersService;

	@PostMapping("/add")
	@CrossOrigin(origins = "http://localhost:3000")
	public ResponseEntity<Response> createUser(@RequestBody UsersEntity userEntity) {
		System.out.println("controller user data=>" + userEntity);

//		return ResponseEntity.ok(userjson);
		return new ResponseEntity<>(usersService.createUser(userEntity), HttpStatus.ACCEPTED);

	}
	
	@CrossOrigin(origins = "http://localhost:3000")
	@GetMapping("/getusers")
	public ResponseEntity<Response> gettingUser() {
        System.out.println("users =>"+usersService.gettingUser());
//		return (ResponseEntity<userjson>) ResponseEntity.ok(usersService.gettingUser());
		return new ResponseEntity<>(usersService.gettingUser(), HttpStatus.ACCEPTED);

	}
	
	
	@CrossOrigin(origins = "http://localhost:3000")
	@GetMapping({"/me", "/users/me"})
	public ResponseEntity<?> getCurrentUser(org.springframework.security.core.Authentication authentication) {
	    if (authentication == null || authentication.getName() == null) {
	        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not authenticated");
	    }
	    UsersEntity user = usersService.findByEmail(authentication.getName());
	    if (user == null) {
	        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
	    }

	    String effectiveName = (user.getUserName() != null && !user.getUserName().trim().isEmpty())
	            ? user.getUserName()
	            : (user.getEmail() != null ? user.getEmail().split("@")[0] : "User");

	    Map<String, Object> userInfo = new HashMap<>();
	    userInfo.put("userID", user.getUserID());
	    userInfo.put("userId", user.getUserID());
	    userInfo.put("userName", effectiveName);
	    userInfo.put("name", effectiveName);
	    userInfo.put("email", user.getEmail());
	    userInfo.put("mobile", user.getMobile());
	    userInfo.put("role", user.getRole());

	    return ResponseEntity.ok(userInfo);
	}



	

	
	@CrossOrigin(origins = "http://localhost:3000")
	@PostMapping("/login")
	public ResponseEntity<Response> validationuser(@RequestBody LoginRequest loginRequest) {
	    Response response = usersService.validationUser(loginRequest.getEmail(), loginRequest.getPassword());

	    return new ResponseEntity<>(response, response.isStatus() ? HttpStatus.OK : HttpStatus.UNAUTHORIZED);
	}


	@CrossOrigin(origins = "http://localhost:3000")
	@PostMapping("/signup")
	public ResponseEntity<Response> signUp(@RequestBody UsersEntity userEntity) {
		return new ResponseEntity<>(usersService.signUp(userEntity), HttpStatus.ACCEPTED);
	}

}
