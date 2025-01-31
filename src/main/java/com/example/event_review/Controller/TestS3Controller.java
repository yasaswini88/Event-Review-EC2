package com.example.event_review.Controller;

// import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
// import software.amazon.awssdk.services.s3.S3Client;

@RestController
public class TestS3Controller {

    // @Autowired
    // private S3Client s3Client; 

    @GetMapping("/test-s3")
    public String testS3() {
        return "S3 Client is working!";
    }
}
