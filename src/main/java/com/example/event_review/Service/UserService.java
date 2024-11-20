package com.example.event_review.Service;

import com.example.event_review.DTO.UserDTO;
import com.example.event_review.Entity.User;
import com.example.event_review.Repo.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserService {

    @Autowired
    private UserRepo userRepo;

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
        return userRepo.save(user);
    }

    public void deleteUser(Long userId) {
        userRepo.deleteById(userId);
    }

    // Utility to convert User entity to UserDTO
    private UserDTO convertToDTO(User user) {
        return new UserDTO(
                user.getUserId(),
                user.getFirstName(),
                user.getLastName(),
                user.getGender(),
                user.getEmail(),
                user.getPhoneNumber(),
                user.getRoles().getRoleName()
        );
    }
}
