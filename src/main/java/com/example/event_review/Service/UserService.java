package com.example.event_review.Service;

import com.example.event_review.DTO.*;
import com.example.event_review.Entity.User;
import com.example.event_review.Entity.VerificationCode;
import com.example.event_review.Entity.Roles;
import com.example.event_review.Repo.UserRepo;
import com.example.event_review.Repo.RolesRepo;
import com.example.event_review.Repo.VerificationCodeRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.*;
import java.util.stream.Collectors;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;

@Service
public class UserService {
    @Autowired
    private UserRepo userRepo;
    
    @Autowired
    private RolesRepo rolesRepo;
    
    @Autowired
    private VerificationCodeRepo verificationCodeRepo;
    
    @Autowired
    private EmailService emailService;

    // Basic CRUD Operations

    @Autowired
private GoogleIdTokenVerifier googleIdTokenVerifier;

// public Optional<User> handleGoogleLogin(String credential) {
//     try {
//         // Verify the Google token
//         GoogleIdToken idToken = googleIdTokenVerifier.verify(credential);
        
//         if (idToken != null) {
//             // Extract email from the token payload
//             GoogleIdToken.Payload payload = idToken.getPayload();
//             String email = payload.getEmail();
            
//             // Check if the user exists in the database
//             return userRepo.findByEmail(email); // Return the user if found
//         }
//         return Optional.empty(); // Return empty if token verification fails
//     } catch (Exception e) {
//         throw new RuntimeException("Error processing Google login", e);
//     }
// }


public Optional<UserDTO> handleGoogleLogin(String credential) {
    try {
        // 1. Verify the Google token
        GoogleIdToken idToken = googleIdTokenVerifier.verify(credential);
        if (idToken == null) {
            // Token verification failed
            return Optional.empty();
        }

        // 2. Extract email and optionally other claims from the token payload
        GoogleIdToken.Payload payload = idToken.getPayload();
        String email = payload.getEmail();
        String givenName = (String) payload.get("given_name");    // optional
        String familyName = (String) payload.get("family_name");  // optional

        // 3. Check if user already exists in the DB
        Optional<User> existingUserOpt = userRepo.findByEmail(email);
        User user;
        if (existingUserOpt.isPresent()) {
            // 3a. If user already exists, reuse it
            user = existingUserOpt.get();
        } else {
            // 3b. If user doesn't exist, create a new one
            Roles facultyRole = rolesRepo.findById(2L)
                .orElseThrow(() -> new RuntimeException("Faculty role with ID=2 not found!"));

            // Create new user
            user = new User();
            user.setEmail(email);
            user.setFirstName(givenName);
            user.setLastName(familyName);
            user.setPassword("GOOGLE_SSO"); // or null, if you prefer
            user.setRoles(facultyRole);
            
            // Save user (automatically assigns userId)
            user = userRepo.save(user);
        }

        // 4. Convert the user entity to a UserDTO
        UserDTO userDTO = convertToDTO(user);

        // 5. Return the newly created or existing user as a DTO
        return Optional.of(userDTO);

    } catch (Exception e) {
        // If there's an error verifying or processing the token
        throw new RuntimeException("Error processing Google login", e);
    }
}



    public List<UserDTO> getAllUsers() {
        return userRepo.findAll()
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public Optional<UserDTO> getUserById(Long userId) {
        return userRepo.findById(userId)
                .map(this::convertToDTO);
    }

   public User addUser(User user) {
    if (user.getRoles() != null && user.getRoles().getRoleId() != null) {
        Roles roles = rolesRepo.findById(user.getRoles().getRoleId())
                .orElseThrow(() -> new RuntimeException("Role not found with ID: " + user.getRoles().getRoleId()));
        user.setRoles(roles);
    } else {
        throw new IllegalArgumentException("Role must be provided in the request");
    }
    return userRepo.save(user);
}



    public void deleteUser(Long userId) {
        userRepo.deleteById(userId);
    }

    // User Role Management
    public Optional<User> updateUserRole(Long userId, Long roleId, User updatedUser) {
        Optional<User> userOpt = userRepo.findByUserId(userId);
        Optional<Roles> roleOpt = rolesRepo.findByRoleId(roleId);
        
        if (userOpt.isPresent() && roleOpt.isPresent()) {
            User user = userOpt.get();
            // user.setPosition(updatedUser.getPosition());
            if (updatedUser.getPassword() != null && !updatedUser.getPassword().isEmpty()) {
                user.setPassword(updatedUser.getPassword());
                // user.setConfirmPassword(updatedUser.getConfirmPassword());
            }
            user.setRoles(roleOpt.get());
            return Optional.of(userRepo.save(user));
        }
        return Optional.empty();
    }

    // Authentication
    public Optional<User> loginUser(User loginUser) {
        Optional<User> user = userRepo.findByEmail(loginUser.getEmail());
        if (user.isPresent() && user.get().getPassword().equals(loginUser.getPassword())) {
            return user;
        }
        return Optional.empty();
    }

    // Password Reset Flow
    public void initiateForgotPassword(String email) {
        User user = userRepo.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        String code = generateRandomCode();
        Date expirationTime = generateExpirationTime();

        VerificationCode verificationCode = new VerificationCode();
        verificationCode.setUser(user);
        verificationCode.setEmail(email);
        verificationCode.setCode(code);
        verificationCode.setExpirationTime(expirationTime);
        verificationCodeRepo.save(verificationCode);

        emailService.sendSimpleEmail(
                email,
                "Password Reset Verification Code",
                "Your verification code is: " + code
        );
    }

    public boolean verifyCode(CodeVerificationRequest request) {
        Optional<VerificationCode> codeOpt = verificationCodeRepo
                .findByCodeAndEmail(request.getCode(), request.getEmail());
        return codeOpt.isPresent() && codeOpt.get().getExpirationTime().after(new Date());
    }

    public Optional<User> loginUser(String email, String password) {
        Optional<User> user = userRepo.findByEmail(email);
        if (user.isPresent() && user.get().getPassword().equals(password)) {
            return user;
        }
        return Optional.empty();
    }

    @Transactional
    public void resetPassword(PasswordResetRequest request) {
        if (request.getNewPassword() == null || request.getConfirmPassword() == null ||
                request.getNewPassword().isEmpty() || request.getConfirmPassword().isEmpty()) {
            throw new IllegalArgumentException("New password and confirmation cannot be empty.");
        }

        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            throw new IllegalArgumentException("Passwords do not match.");
        }

        User user = userRepo.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setPassword(request.getNewPassword());
        
        userRepo.save(user);
        verificationCodeRepo.deleteByEmail(request.getEmail());
    }

    // User Profile Update
    public Optional<User> updateUserDetails(Long userId, User updatedUser) {
        return userRepo.findByUserId(userId)
                .map(user -> {
                    // Update basic fields
                    user.setFirstName(updatedUser.getFirstName());
                    user.setLastName(updatedUser.getLastName());
                    user.setEmail(updatedUser.getEmail());
                    user.setPhoneNumber(updatedUser.getPhoneNumber());
                    user.setGender(updatedUser.getGender());
    
                    // Update password if provided
                    if (updatedUser.getPassword() != null && !updatedUser.getPassword().isEmpty()) {
                        user.setPassword(updatedUser.getPassword());
                    }
    
                    // NEW: Update role if provided
                    if (updatedUser.getRoles() != null && updatedUser.getRoles().getRoleId() != null) {
                        Long newRoleId = updatedUser.getRoles().getRoleId();
                        Roles newRole = rolesRepo.findById(newRoleId)
                                .orElseThrow(() -> new RuntimeException("Role not found with ID: " + newRoleId));
                        user.setRoles(newRole);
                    }
    
                    return userRepo.save(user);
                });
    }
    

    // Utility Methods
    private String generateRandomCode() {
        return String.valueOf((int)(Math.random() * 9000) + 1000);
    }

    private Date generateExpirationTime() {
        return new Date(System.currentTimeMillis() + (10 * 60 * 1000)); // 10 minutes
    }

    public UserDTO convertToDTO(User user) {
        // 1. Create a RolesDTO from user.getRoles()
        RolesDTO rolesDTO = new RolesDTO();
        rolesDTO.setRoleId(user.getRoles().getRoleId());
        rolesDTO.setRoleName(user.getRoles().getRoleName());
    
        // 2. Create a new UserDTO
        UserDTO userDTO = new UserDTO();
        userDTO.setUserId(user.getUserId());
        userDTO.setFirstName(user.getFirstName());
        userDTO.setLastName(user.getLastName());
        userDTO.setGender(user.getGender());
        userDTO.setEmail(user.getEmail());
        userDTO.setPhoneNumber(user.getPhoneNumber());
    
        // 3. Set roles on the UserDTO
        userDTO.setRoles(rolesDTO);
    
        return userDTO;
    }
    
}