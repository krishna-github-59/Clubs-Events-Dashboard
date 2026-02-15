package com.club.events_dashboard.controller;

import com.club.events_dashboard.constants.Role;
import com.club.events_dashboard.dto.ApiResponse;
import com.club.events_dashboard.dto.ClubRequestDTO;
import com.club.events_dashboard.security.JwtUtil;
import com.club.events_dashboard.service.ClubService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/clubs")
public class ClubController {

    @Autowired
    private ClubService clubService;

    @Autowired
    private JwtUtil jwtUtil;
    
    // Add club (only SuperAdmin allowed)
    @PostMapping("/add")
    public ResponseEntity<ApiResponse> createClub(
            @RequestHeader("Authorization") String authHeader,
            @RequestBody ClubRequestDTO clubRequest) {

        // Extract JWT token (without "Bearer ")
        String token = authHeader.substring(7);

        // Extract email & Role (user who is requesting to create the club)
        String requesterEmail = jwtUtil.extractUsername(token);
        Role requesterRole = jwtUtil.extractRole(token);

        return clubService.createClub(clubRequest, requesterEmail, requesterRole);
    }

    @GetMapping("/all")
    public ResponseEntity<ApiResponse> getAllClubs() {
        return clubService.getAllClubs();
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse> getClubById(@PathVariable Long id) {
        return clubService.getClubById(id);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<ApiResponse> updateClub(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable Long id,
            @RequestBody ClubRequestDTO clubRequest) {

        String token = authHeader.substring(7);
        Role requesterRole = jwtUtil.extractRole(token);

        return clubService.updateClub(id, clubRequest, requesterRole);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<ApiResponse> deleteClub(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable Long id) {

        String token = authHeader.substring(7);
        Role requesterRole = jwtUtil.extractRole(token);

        return clubService.deleteClub(id, requesterRole);
    }
}