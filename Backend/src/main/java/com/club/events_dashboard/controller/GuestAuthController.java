package com.club.events_dashboard.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.club.events_dashboard.dto.ApiResponse;
import com.club.events_dashboard.dto.GuestLoginDTO;
import com.club.events_dashboard.dto.GuestRegisterDTO;
import com.club.events_dashboard.service.GuestAuthService;

@RestController
@RequestMapping("/api/auth/guests")
public class GuestAuthController {

    private final GuestAuthService guestAuthService;

    public GuestAuthController(GuestAuthService guestAuthService) {
        this.guestAuthService = guestAuthService;
    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponse> guestRegister(@RequestBody GuestRegisterDTO request) {
        return guestAuthService.guestRegister(request);
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse> guestLogin(@RequestBody GuestLoginDTO request) {
        return guestAuthService.guestLogin(request);
    }
}
