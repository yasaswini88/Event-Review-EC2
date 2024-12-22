package com.example.event_review.Controller;

import com.example.event_review.DTO.*;
import com.example.event_review.Entity.User;
import com.example.event_review.Service.UserService;
// import com.example.event_review.Service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestController
@CrossOrigin
@RequestMapping("/api")
public class UserController {
    @Autowired
    private UserService userService;

    // @Autowired
    // private EmailService emailService;

    // Basic CRUD Operations
    @GetMapping("/users")
    public List<UserDTO> getAllUsers() {
        return userService.getAllUsers();
    }

    @GetMapping("/users/{id}")
    public ResponseEntity<UserDTO> getUserById(@PathVariable Long id) {
        Optional<UserDTO> user = userService.getUserById(id);
        return user.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/users")
    public ResponseEntity<?> addUser(@RequestBody User user) {
        try {
            User newUser = userService.addUser(user);
            return new ResponseEntity<>(newUser, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/users/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    // User Role Management
    @PutMapping("/users/{userId}/roles/{roleId}")
    public ResponseEntity<User> updateUserRole(@PathVariable Long userId, @PathVariable Long roleId,
            @RequestBody User user) {
        return userService.updateUserRole(userId, roleId, user)
                .map(updatedUser -> new ResponseEntity<>(updatedUser, HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    // Authentication
    // @PostMapping("/login")
    // public ResponseEntity<User> login(@RequestBody LoginRequest loginRequest) {
    //     return userService.loginUser(loginRequest.getEmail(), loginRequest.getPassword())
    //             .map(user -> new ResponseEntity<>(user, HttpStatus.OK))
    //             .orElse(new ResponseEntity<>(HttpStatus.UNAUTHORIZED));
    // }

    @PostMapping("/login")
    public ResponseEntity<UserDTO> login(@RequestBody LoginRequest loginRequest) {
        Optional<User> userOpt = userService.loginUser(loginRequest.getEmail(), loginRequest.getPassword());
        
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            // Now we can call the public convertToDTO method on userService
            UserDTO userDTO = userService.convertToDTO(user);
            return new ResponseEntity<>(userDTO, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
    }
    


    // Password Reset Flow
    @PostMapping("/forgot-password")
    public ResponseEntity<String> forgotPassword(@RequestBody Map<String, String> payload) {
        try {
            userService.initiateForgotPassword(payload.get("email"));
            return new ResponseEntity<>("Passcode sent to your email.", HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>("Email not found.", HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping("/verify-code")
    public ResponseEntity<String> verifyCode(@RequestBody CodeVerificationRequest request) {
        if (userService.verifyCode(request)) {
            return new ResponseEntity<>("Code verified. Proceed to reset password.", HttpStatus.OK);
        }
        return new ResponseEntity<>("Invalid or expired code.", HttpStatus.BAD_REQUEST);
    }

    @PostMapping("/reset-password")
    public ResponseEntity<Map<String, String>> resetPassword(@RequestBody PasswordResetRequest request) {
        try {
            userService.resetPassword(request);
            Map<String, String> response = new HashMap<>();
            response.put("message", "Password reset successfully.");
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            Map<String, String> response = new HashMap<>();
            response.put("message", e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        } catch (RuntimeException e) {
            Map<String, String> response = new HashMap<>();
            response.put("message", "User not found.");
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }
    }

    // @PostMapping("/google-login")
    // public ResponseEntity<User> googleLogin(@RequestBody GoogleLoginRequest
    // request) {
    // try {
    // return userService.handleGoogleLogin(request.getCredential())
    // .map(user -> new ResponseEntity<>(user, HttpStatus.OK))
    // .orElse(new ResponseEntity<>(HttpStatus.UNAUTHORIZED));
    // } catch (Exception e) {
    // return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
    // }
    // }

    // @PostMapping("/google-login")
    // public ResponseEntity<User> googleLogin(@RequestBody GoogleLoginRequest request) {
    //     try {
    //         return userService.handleGoogleLogin(request.getCredential())
    //                 .map(user -> new ResponseEntity<>(user, HttpStatus.OK))
    //                 .orElse(new ResponseEntity<>(HttpStatus.UNAUTHORIZED)); // No user found, return 401
    //     } catch (Exception e) {
    //         return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR); // Internal error
    //     }
    // }

    @PostMapping("/google-login")
public ResponseEntity<UserDTO> googleLogin(@RequestBody GoogleLoginRequest request) {
    try {
        return userService.handleGoogleLogin(request.getCredential())
                .map(userDTO -> new ResponseEntity<>(userDTO, HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.UNAUTHORIZED)); // No user found, return 401
    } catch (Exception e) {
        return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR); // Internal error
    }
}


    // User Profile Update
    @PutMapping("/users/{userId}")
    public ResponseEntity<User> updateUserDetails(@PathVariable Long userId, @RequestBody User updatedUser) {
        return userService.updateUserDetails(userId, updatedUser)
                .map(user -> new ResponseEntity<>(user, HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }
}