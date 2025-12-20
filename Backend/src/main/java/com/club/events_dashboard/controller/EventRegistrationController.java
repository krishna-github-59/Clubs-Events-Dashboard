package com.club.events_dashboard.controller;

import com.club.events_dashboard.dto.ApiResponse;
import com.club.events_dashboard.dto.EventRegistrationRequestDTO;
import com.club.events_dashboard.service.EventRegistrationService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/events/register")
public class EventRegistrationController {

    @Autowired
    private EventRegistrationService registrationService;

    @PostMapping
    public ResponseEntity<ApiResponse> register(@RequestBody EventRegistrationRequestDTO req) {
        return registrationService.registerForEvent(req);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<ApiResponse> getUserRegistrations(@PathVariable Long userId) {
        return registrationService.getRegisteredEventsForUser(userId);
    }

    @GetMapping("/guest/{guestId}")
    public ResponseEntity<ApiResponse> getGuestRegistrations(@PathVariable Long guestId) {
        return registrationService.getRegisteredEventsForGuest(guestId);
    }
}

