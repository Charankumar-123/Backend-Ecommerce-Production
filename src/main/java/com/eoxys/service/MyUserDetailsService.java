package com.eoxys.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.eoxys.entity.UserPrincipal;
import com.eoxys.entity.UsersEntity;
import com.eoxys.repository.UsersRepository;

public class MyUserDetailsService implements UserDetailsService {
	
	@Autowired
	private UsersRepository userRepository;

	@Override
	public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
	    System.out.println("Fetching user: " + email);
	    
	    UsersEntity user = userRepository.findByEmail(email)
	            .orElseThrow(() -> new UsernameNotFoundException("User not found: " + email));

	    System.out.println("User found: " + user.getEmail());
	    return new UserPrincipal(user);
	}



}
