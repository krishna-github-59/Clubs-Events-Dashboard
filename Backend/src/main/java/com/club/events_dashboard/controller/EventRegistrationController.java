package com.club.events_dashboard.controller;

import com.club.events_dashboard.dto.ApiResponse;
import com.club.events_dashboard.dto.EventRegistrationRequestDTO;
import com.club.events_dashboard.service.EventRegistrationService;
import com.club.events_dashboard.security.JwtUtil;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/events/register")
public class EventRegistrationController {

    @Autowired
    private EventRegistrationService eventRegistrationService;
    @Autowired
    private JwtUtil jwtUtil;

    // @PostMapping
    // public ResponseEntity<ApiResponse> register(@RequestBody EventRegistrationRequestDTO req) {
    //     return eventRegistrationService.registerForEvent(req);
    // }
    @PostMapping
    public ResponseEntity<ApiResponse> register(
            @RequestBody EventRegistrationRequestDTO req,
            @RequestHeader(value = "Authorization", required = false) String authHeader
    ) {
        return eventRegistrationService.registerForEvent(req, authHeader);
    }


    @GetMapping("/user/{userId}")
    public ResponseEntity<ApiResponse> getUserRegistrations(@PathVariable Long userId) {
        return eventRegistrationService.getRegisteredEventsForUser(userId);
    }

    // @GetMapping("/guest/{guestId}")
    // public ResponseEntity<ApiResponse> getGuestRegistrations(@PathVariable Long guestId) {
    //     return eventRegistrationService.getRegisteredEventsForGuest(guestId);
    // }
}

