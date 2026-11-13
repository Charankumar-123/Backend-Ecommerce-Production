package com.eoxys.entity;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.eoxys.utils.UserRole;

public class UserPrincipal implements UserDetails {
    
    private final UsersEntity user;
    
    public UserPrincipal(UsersEntity user) {
        this.user = user;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        UserRole userRole = UserRole.fromString(user.getRole());
        List<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority(userRole.toAuthority()));
        authorities.add(new SimpleGrantedAuthority(userRole.name()));
        
        // Backward compatibility for existing check matchers
        if (userRole == UserRole.CUSTOMER) {
            authorities.add(new SimpleGrantedAuthority("ROLE_USER"));
            authorities.add(new SimpleGrantedAuthority("USER"));
        }
        return authorities;
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getEmail();
    }
    
    public String getEmail() {
        return user.getEmail();
    }
    
    public String getRole() {
        return UserRole.fromString(user.getRole()).name();
    }
    
    public Long getMobile() {
        return user.getMobile();
    }
    
    public Long getUserId() {
        return user.getUserID();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
