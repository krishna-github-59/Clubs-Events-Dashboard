package com.club.events_dashboard.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.club.events_dashboard.dto.ApiResponse;
import com.club.events_dashboard.entity.Role;
import com.club.events_dashboard.security.JwtUtil;
import com.club.events_dashboard.service.ClubMembershipService;

@RestController
@RequestMapping("/api/memberships")
public class ClubMembershipController {

    private final ClubMembershipService clubMembershipService;

    private final JwtUtil jwtUtil;

    public ClubMembershipController(ClubMembershipService clubMembershipService,JwtUtil jwtUtil) {
        this.clubMembershipService = clubMembershipService;
        this.jwtUtil = jwtUtil;
    }

    // Only superadmin or club admin
    @PostMapping("/add")
    public ResponseEntity<ApiResponse> addMemberToClub(
            @RequestHeader("Authorization") String authHeader,
            @RequestParam Long clubId,
            @RequestParam Long userId
    ) {
        String token = authHeader.substring(7);
        String requesterEmail = jwtUtil.extractUsername(token);
        Role requesterRole = jwtUtil.extractRole(token);

        return clubMembershipService.addMemberToClub(clubId, userId, requesterEmail, requesterRole);
    }

    // Only superadmin or club admin
    @DeleteMapping("/remove")
    public ResponseEntity<ApiResponse> removeMemberFromClub(
            @RequestHeader("Authorization") String authHeader,
            @RequestParam Long clubId,
            @RequestParam Long userId
    ) {
        String token = authHeader.substring(7);
        String requesterEmail = jwtUtil.extractUsername(token);
        Role requesterRole = jwtUtil.extractRole(token);

        return clubMembershipService.removeMemberFromClub(clubId, userId, requesterEmail, requesterRole);
    }


    @GetMapping("/club/{clubId}")
    public ResponseEntity<ApiResponse> getMembersByClub(@PathVariable Long clubId) {
        return clubMembershipService.getMembersByClub(clubId);
    }

    
    @GetMapping("/user/{userId}")
    public ResponseEntity<ApiResponse> getClubsByUser(@PathVariable Long userId) {
        return clubMembershipService.getClubsByUser(userId);
    }

}
