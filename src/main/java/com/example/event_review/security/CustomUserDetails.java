package com.example.event_review.security;

import com.example.event_review.Entity.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import java.util.Collection;
import java.util.Collections;

/**
 * Wraps our Entity.User into a Spring Security UserDetails object.
 * Spring Security needs user details at runtime, including authorities/roles.
 */
public class CustomUserDetails implements UserDetails {

    private final User user;

    public CustomUserDetails(User user) {
        this.user = user;
    }

    /**
     * We'll map the user's 'roleName' to a SimpleGrantedAuthority so that
     * Spring Security can do `hasAuthority("Admin")` or `hasAuthority("ROLE_Admin")` checks.
     * 
     * If your roleName is e.g. "Admin", you can choose to prepend "ROLE_" or just leave it.
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // Example: user.getRoles().getRoleName() = "Admin"
        // We can do:
        //   "ROLE_Admin" or just "Admin" depending on how you want to handle it
        return Collections.singletonList(
            new SimpleGrantedAuthority(user.getRoles().getRoleName())
        );
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    // We treat “email” as the username
    @Override
    public String getUsername() {
        return user.getEmail();
    }

    // Mark account as non-expired, non-locked, etc.
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
        // you could store a “status” or “enabled” column in DB if needed
        return true;
    }

    public User getUser() {
        return this.user;
    }
}
