package com.club.events_dashboard.controller;

import com.club.events_dashboard.dto.ApiResponse;
import com.club.events_dashboard.dto.EventRequestDTO;
import com.club.events_dashboard.entity.Role;
import com.club.events_dashboard.security.JwtUtil;
import com.club.events_dashboard.service.EventService;

import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


@RestController
@RequestMapping("/api/events")
public class EventController {

    @Autowired
    private EventService eventService;

    @Autowired
    private JwtUtil jwtUtil;

    // Create event — only SuperAdmin or Club Admin
    @PostMapping(
        value = "/add",
        consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public ResponseEntity<ApiResponse> createEvent(
            @RequestHeader("Authorization") String authHeader,
            @RequestPart("event") EventRequestDTO eventRequest,
            @RequestPart(value = "poster", required = false) MultipartFile poster) {

        String token = authHeader.substring(7);
        String requesterEmail = jwtUtil.extractUsername(token);
        Role requesterRole = jwtUtil.extractRole(token);

        return eventService.createEvent(eventRequest, requesterEmail, requesterRole, poster);
    }

    // Update event — only SuperAdmin or Club Admin
    @PutMapping(
        value = "/update/{id}",
        consumes = { MediaType.MULTIPART_FORM_DATA_VALUE }
    )
    public ResponseEntity<ApiResponse> updateEvent(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable Long id,
            @RequestPart("event") EventRequestDTO eventRequest,
            @RequestPart(value = "poster", required = false) MultipartFile poster) {

        String token = authHeader.substring(7);
        String requesterEmail = jwtUtil.extractUsername(token);
        Role requesterRole = jwtUtil.extractRole(token);

        return eventService.updateEvent(id, eventRequest, requesterEmail, requesterRole, poster);
    }

    // Delete event — only SuperAdmin or Club Admin
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<ApiResponse> deleteEvent(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable Long id) {

        String token = authHeader.substring(7);
        String requesterEmail = jwtUtil.extractUsername(token);
        Role requesterRole = jwtUtil.extractRole(token);

        return eventService.deleteEvent(id, requesterEmail, requesterRole);
    }

    // Fetch all events (public)
    @GetMapping("/all")
    public ResponseEntity<ApiResponse> getAllEvents() {
        return eventService.getAllEvents();
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse> getEventById(@PathVariable Long id) {
        return eventService.getEventById(id);
    }

    // Filter Events (public)
    @GetMapping("/filter")
    public ResponseEntity<ApiResponse> getFilteredEvents(
            @RequestParam(required = false) Long clubId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(required = false) String name
    ) {
        return eventService.getFilteredEvents(clubId, name, startDate, endDate);
    }

    // Get upcoming events
    @GetMapping("/upcoming")
    public ResponseEntity<ApiResponse> getUpcomingEvents() {
        return eventService.getUpcomingEvents();
    }

    // Get past events
    @GetMapping("/past")
    public ResponseEntity<ApiResponse> getPastEvents() {
        return eventService.getPastEvents();
    }

    @GetMapping("/dashboard")
    public ResponseEntity<ApiResponse> getEventsDashboard() {
        return eventService.getEventsDashboard();
    }

    @GetMapping("/club/{clubId}/upcoming")
    public ResponseEntity<ApiResponse> getUpcomingEventsByClub(@PathVariable Long clubId) {
        return eventService.getUpcomingEventsByClub(clubId);
    }

    @GetMapping("/club/{clubId}/past")
    public ResponseEntity<ApiResponse> getPastEventsByClub(@PathVariable Long clubId) {
        return eventService.getPastEventsByClub(clubId);
    }

}
