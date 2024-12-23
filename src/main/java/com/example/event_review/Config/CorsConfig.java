package com.example.event_review.Config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

@Configuration
public class CorsConfig {

    @Bean
    public CorsFilter corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(true);
         
        config.addAllowedOrigin("http://localhost:3000");
        // config.addAllowedOrigin("http://54.175.8.123:3000");
        config.addAllowedOrigin("http://35.173.220.149:3000");

        config.addAllowedOrigin("http://35.153.179.66:8080"); 
        config.addAllowedOriginPattern("*");

        config.addAllowedHeader("*");
        config.addAllowedMethod("*");
        source.registerCorsConfiguration("/**", config);
        return new CorsFilter(source);
    }
}