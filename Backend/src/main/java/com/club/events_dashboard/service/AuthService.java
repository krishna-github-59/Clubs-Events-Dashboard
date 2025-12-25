package com.club.events_dashboard.service;

import com.club.events_dashboard.dto.ApiResponse;
import com.club.events_dashboard.dto.JwtResponseDTO;
import com.club.events_dashboard.dto.LoginRequestDTO;
import com.club.events_dashboard.dto.RegisterRequestDTO;
import com.club.events_dashboard.dto.UserDTO;
import com.club.events_dashboard.entity.Role;
import com.club.events_dashboard.entity.User;
import com.club.events_dashboard.repository.UserRepository;
import com.club.events_dashboard.security.JwtUtil;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    // student
    public ResponseEntity<ApiResponse> registerStudent(RegisterRequestDTO request) {
        return register(request, Role.STUDENT);
    }
    // Club Admin
    public ResponseEntity<ApiResponse> registerClubAdmin(RegisterRequestDTO request) {
        return register(request, Role.CLUB_ADMIN);
    }


    // Generalized Register Method
    public ResponseEntity<ApiResponse> register(RegisterRequestDTO request, Role role) {
        if (!request.getEmail().endsWith("@iiitm.ac.in")) {
            return ResponseEntity.badRequest().body(new ApiResponse(false, "Email must be from iiitm.ac.in domain"));
        }

        Optional<User> existingUser = userRepository.findByEmail(request.getEmail());
        if (existingUser.isPresent()) {
            return ResponseEntity.badRequest().body(new ApiResponse(false, "Email already registered"));
        }

        User user = new User();
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(role);

        User savedUser = userRepository.save(user);
        UserDTO userDTO = new UserDTO(savedUser.getId(), savedUser.getName(), savedUser.getEmail());

        return ResponseEntity.ok(new ApiResponse(true, "User registered successfully", userDTO));
    }

    // Login user
    public ResponseEntity<ApiResponse> login(LoginRequestDTO request) {
        User user = userRepository.findByEmail(request.getEmail()).orElse(null);

        if (user == null || !passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            return ResponseEntity.status(401).body(new ApiResponse(false, "Invalid email or password"));
        }

        String token = jwtUtil.generateToken(user.getEmail());
        JwtResponseDTO jwtResponse = new JwtResponseDTO(token);

        return ResponseEntity.ok(new ApiResponse(true, "Login successful", jwtResponse));
    }
}
