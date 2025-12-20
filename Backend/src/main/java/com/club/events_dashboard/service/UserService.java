package com.club.events_dashboard.service;

import com.club.events_dashboard.dto.ApiResponse;
import com.club.events_dashboard.entity.User;
import com.club.events_dashboard.repository.UserRepository;

import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, BCryptPasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public ResponseEntity<ApiResponse> getAllUsers() {
        List<User> users = userRepository.findAll();
        return ResponseEntity.ok(new ApiResponse(true, "Fetched all users", users));
    }

    public ResponseEntity<ApiResponse> getUserById(Long id) {
        return userRepository.findById(id)
                .map(user -> ResponseEntity.ok(new ApiResponse(true, "User found", user)))
                .orElseGet(() -> ResponseEntity.status(404).body(new ApiResponse(false, "User not found")));
    }

    public ResponseEntity<ApiResponse> createUser(User user) {
        // Encode password and save new user
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        User savedUser = userRepository.save(user);
        return ResponseEntity.ok(new ApiResponse(true, "User created successfully", savedUser));
    }


    public ResponseEntity<ApiResponse> updateUser(Long id, User updatedUser) {
        return userRepository.findById(id)
                .map(user -> {
                    user.setName(updatedUser.getName());
                    user.setEmail(updatedUser.getEmail());
                    user.setRole(updatedUser.getRole());

                    // Only encode if password actually changed
                    if (!updatedUser.getPassword().equals(user.getPassword())) {
                        user.setPassword(passwordEncoder.encode(updatedUser.getPassword()));
                    }

                    User savedUser = userRepository.save(user);
                    return ResponseEntity.ok(new ApiResponse(true, "User updated successfully", savedUser));
                })
                .orElseGet(() -> ResponseEntity.status(404).body(new ApiResponse(false, "User not found")));
    }

    public ResponseEntity<ApiResponse> deleteUser(Long id) {
        return userRepository.findById(id)
                .map(user -> {
                    userRepository.delete(user);
                    return ResponseEntity.ok(new ApiResponse(true, "User deleted successfully"));
                })
                .orElseGet(() -> ResponseEntity.status(404).body(new ApiResponse(false, "User not found")));
    }
}
