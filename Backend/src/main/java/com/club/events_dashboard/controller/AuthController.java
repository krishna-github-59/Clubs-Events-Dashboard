package com.club.events_dashboard.controller;

import com.club.events_dashboard.dto.ApiResponse;
import com.club.events_dashboard.dto.LoginRequestDTO;
import com.club.events_dashboard.dto.RegisterRequestDTO;
import com.club.events_dashboard.service.AuthService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthService authService;
    
    // Super Admin Bootstrap Registration
    @PostMapping("/register/super-admin")
    public ResponseEntity<ApiResponse> registerSuperAdmin(
            @RequestBody RegisterRequestDTO request) {
        return authService.registerSuperAdmin(request);
    }

    // Register students
    @PostMapping("/register/student")
    public ResponseEntity<ApiResponse> register(@RequestBody RegisterRequestDTO request) {
        return authService.registerStudent(request);
    }

    // Register club admin
    @PostMapping("/register/club-admin")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<ApiResponse> registerClubAdmin(@RequestBody RegisterRequestDTO request) {
        return authService.registerClubAdmin(request);
    }


    // Login Endpoint
    @PostMapping("/login")
    public ResponseEntity<ApiResponse> login(@RequestBody LoginRequestDTO request) {
        return authService.login(request);
    }
}
