package com.example.event_review.security;

import com.example.event_review.security.jwt.AuthTokenFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Basic config that:
 * 1) Disables CSRF
 * 2) Permits certain endpoints
 * 3) Requires authentication for everything else
 * 4) Adds our AuthTokenFilter to the chain
 */
@Configuration
public class SecurityConfig {

    private final AuthTokenFilter authTokenFilter;

    public SecurityConfig(AuthTokenFilter authTokenFilter) {
        this.authTokenFilter = authTokenFilter;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())

            .authorizeHttpRequests(auth -> auth
                // Permit login, google-login, signup, forgot, etc.
                .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                .requestMatchers(
                        "/api/login",
                        "/api/google-login",
                        "/api/forgot-password",
                        "/api/verify-code",
                        "/api/reset-password",
                        // Let’s say we also allow user creation
                        "/api/users"
                ).permitAll()

                // everything else must be authenticated
                .anyRequest().authenticated()
            )
            // If you previously used .httpBasic(), remove it if you only want JWT
            // .httpBasic(withDefaults())
            .addFilterBefore(authTokenFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
