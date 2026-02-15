package com.club.events_dashboard.controller;

import com.club.events_dashboard.constants.Role;
import com.club.events_dashboard.dto.ApiResponse;
import com.club.events_dashboard.dto.EventRequestDTO;
import com.club.events_dashboard.security.JwtUtil;
import com.club.events_dashboard.service.EventService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


@RestController
@RequestMapping("/api/events")
public class EventController {

    @Autowired
    private EventService eventService;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private ObjectMapper objectMapper;


    // Create event — only SuperAdmin or Club Admin
    @PostMapping(
        value = "/add",
        consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public ResponseEntity<ApiResponse> createEvent(
            @RequestHeader("Authorization") String authHeader,
            @RequestPart("event") String eventJson,
            @RequestPart(value = "poster", required = false) MultipartFile poster) {

        try {
            String token = authHeader.substring(7);
            String requesterEmail = jwtUtil.extractUsername(token);
            Role requesterRole = jwtUtil.extractRole(token);

            EventRequestDTO eventRequest =
                    objectMapper.readValue(eventJson, EventRequestDTO.class);

            return eventService.createEvent(eventRequest, requesterEmail, requesterRole, poster);
        } catch (JsonProcessingException e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse(false, "Invalid event JSON format"));
        }
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

        System.out.println("JWT Email: " + requesterEmail);
        System.out.println("JWT Role: " + requesterRole);


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

    @GetMapping("/my-club")
    @PreAuthorize("hasRole('CLUB_ADMIN')")
    public ResponseEntity<ApiResponse> getMyClubEvents(
            @RequestHeader("Authorization") String authHeader) {

        String token = authHeader.substring(7);

        String email = jwtUtil.extractUsername(token);
        Role role = jwtUtil.extractRole(token);

        return eventService.getMyClubEvents(email, role);
    }

    @GetMapping("/club/{clubId}")
    // @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<ApiResponse> getEventsByClubId(
            @PathVariable Long clubId) {

        return eventService.getEventsByClubId(clubId);
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

    @GetMapping("/my-club/upcoming")
    public ResponseEntity<ApiResponse> getUpcomingEventsByClub(
        @RequestHeader("Authorization") String authHeader) {

            String token = authHeader.substring(7);
            String email = jwtUtil.extractUsername(token);
            Role role = jwtUtil.extractRole(token);

        return eventService.getUpcomingEventsByClub(email, role);
    }

    @GetMapping("/my-club/past")
    public ResponseEntity<ApiResponse> getPastEventsByClub(
        @RequestHeader("Authorization") String authHeader) {

            String token = authHeader.substring(7);
            String email = jwtUtil.extractUsername(token);
            Role role = jwtUtil.extractRole(token);

        return eventService.getPastEventsByClub(email, role);
    }

}
