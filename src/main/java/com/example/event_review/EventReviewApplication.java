package com.example.event_review;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class EventReviewApplication {

	public static void main(String[] args) {
		SpringApplication.run(EventReviewApplication.class, args);
	}

}
