package com.example.event_review.Controller;

import com.example.event_review.DTO.*;
import com.example.event_review.Entity.User;
import com.example.event_review.Repo.UserRepo;
import com.example.event_review.Service.UserService;
import com.example.event_review.security.jwt.JwtUtils;

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

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private UserRepo userRepo;

    // New endpoint to decode token and fetch user details
    @GetMapping("/decode-token")
    public ResponseEntity<?> decodeToken(@RequestHeader("Authorization") String authorizationHeader) {
        try {
            String token = authorizationHeader.substring(7); // remove "Bearer "
            if (!jwtUtils.validateJwtToken(token)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid Token");
            }
    
            // Extract email from JWT
            String email = jwtUtils.getUserNameFromJwtToken(token);
    
            // Find user in DB
            Optional<User> userOpt = userRepo.findByEmail(email);
            if (userOpt.isPresent()) {
                User user = userOpt.get();
    
                // Return user details, including numeric roleId
                Map<String, Object> userDetails = new HashMap<>();
                userDetails.put("userId", user.getUserId());
                userDetails.put("email", user.getEmail());
                userDetails.put("roleId", user.getRoles().getRoleId());  // <--- numeric
                userDetails.put("firstName", user.getFirstName());
                userDetails.put("lastName", user.getLastName());
                // etc.
    
                return ResponseEntity.ok(userDetails);
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error decoding token");
        }
    }
    

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

   

    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> login(@RequestBody LoginRequest loginRequest) {
       Optional<User> userOpt = userService.loginUser(loginRequest.getEmail(), loginRequest.getPassword());
    
       if (userOpt.isPresent()) {
           User user = userOpt.get();
    
           // generate token with custom claims for userId & role
           String token = jwtUtils.generateJwtToken(user);
    
           Map<String, String> response = new HashMap<>();
           response.put("token", token);
    
           return ResponseEntity.ok(response);
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

 

//     @PostMapping("/google-login")
// public ResponseEntity<UserDTO> googleLogin(@RequestBody GoogleLoginRequest request) {
//     try {
//         return userService.handleGoogleLogin(request.getCredential())
//                 .map(userDTO -> new ResponseEntity<>(userDTO, HttpStatus.OK))
//                 .orElse(new ResponseEntity<>(HttpStatus.UNAUTHORIZED)); // No user found, return 401
//     } catch (Exception e) {
//         return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR); // Internal error
//     }
// }
@PostMapping("/google-login")
public ResponseEntity<Map<String, String>> googleLogin(@RequestBody GoogleLoginRequest request) {
    try {
        Optional<User> userOpt = userService.handleGoogleLoginAsUser(request.getCredential());
        if (userOpt.isPresent()) {
            User user = userOpt.get();

            // generate the JWT with role & userId
            String token = jwtUtils.generateJwtToken(user);

            // return just the token
            Map<String, String> response = new HashMap<>();
            response.put("token", token);
            return ResponseEntity.ok(response);
        } else {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
    } catch (Exception e) {
        return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
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