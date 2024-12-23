package com.example.event_review.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    // 1) We will need an AuthenticationManager bean for the login process
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    // 2) Define our SecurityFilterChain
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable()) // Disable CSRF (for token-based auth)
            .authorizeHttpRequests(auth -> auth
                // Permit anyone to hit login, signup, google-login, etc.
                .requestMatchers("/api/login", "/api/google-login", "/api/forgot-password", "/api/verify-code", "/api/reset-password", "/api/users").permitAll()
                // everything else requires authentication
                .anyRequest().authenticated()
            )
            // formLogin can be disabled because we will rely on JSON login
            .httpBasic(Customizer.withDefaults()); // Basic auth to test quickly (we'll adjust later if needed)

        return http.build();
    }
}
