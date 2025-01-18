
package com.example.event_review.Config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Bean;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.regions.Region;


@Configuration
public class S3Config {

    @Bean
    public S3Client s3Client() {
        return S3Client.builder()
                       // The region can be set here if desired; if omitted, 
                       // the SDK picks up from environment or instance metadata
                       .region(Region.US_EAST_1)
                       .build();
    }
}


