package com.eoxys.entity;

import java.util.Collection;
import java.util.Collections;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

public class UserPrincipal implements UserDetails {
    
    private final UsersEntity user; // Remove @Autowired, use constructor injection
    
    public UserPrincipal(UsersEntity user) {
        this.user = user;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // Return the user's role from UsersEntity instead of hardcoding "USER"
        return Collections.singleton(new SimpleGrantedAuthority("ROLE_" + user.getRole()));
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getEmail(); // Return email instead of userName
    }
    
    public String getEmail() {
        return user.getEmail();
    }
    
    public String getRole() {
        return user.getRole();
    }
    
    public Long getMobile() {
        return user.getMobile();
    }
    
    public Long getUserId() {
        return user.getUserID();
    }

    // Required UserDetails methods (default implementations)
    @Override
    public boolean isAccountNonExpired() {
        return true; // Customize if needed
    }

    @Override
    public boolean isAccountNonLocked() {
        return true; // Customize if needed
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true; // Customize if needed
    }

    @Override
    public boolean isEnabled() {
        return true; // Customize if needed
    }
}