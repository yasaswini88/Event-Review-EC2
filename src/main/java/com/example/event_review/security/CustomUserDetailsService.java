package com.example.event_review.security;

import com.example.event_review.Entity.User;
import com.example.event_review.Repo.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;

/**
 * Spring Security calls this to load user data by “username” (which we interpret as email).
 */
@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepo userRepo;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        // 1) Find the user by email
        User user = userRepo.findByEmail(email)
                .orElseThrow(() ->
                        new UsernameNotFoundException("User not found with email: " + email));

        // 2) Wrap in CustomUserDetails
        return new CustomUserDetails(user);
    }
}
