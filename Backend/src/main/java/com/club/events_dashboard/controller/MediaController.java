package com.club.events_dashboard.controller;

import com.club.events_dashboard.constants.Role;
import com.club.events_dashboard.dto.ApiResponse;
import com.club.events_dashboard.security.JwtUtil;
import com.club.events_dashboard.service.MediaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


@RestController
@RequestMapping("/api/media")
public class MediaController {

    @Autowired
    private MediaService mediaService;

    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping("/upload/{eventId}")
    public ResponseEntity<ApiResponse> uploadMedia(
                    @RequestHeader("Authorization") String authHeader,
                    @PathVariable Long eventId,
                    @RequestParam("file") MultipartFile file){

        String token = authHeader.substring(7);
        String requesterEmail = jwtUtil.extractUsername(token);
        Role requesterRole = jwtUtil.extractRole(token);

        return mediaService.uploadMedia(file, eventId, requesterEmail, requesterRole);
    }

    @DeleteMapping("/delete/{mediaId}")
    public ResponseEntity<ApiResponse> deleteMedia(
                @RequestHeader("Authorization") String authHeader,
                @PathVariable Long mediaId){

        String token = authHeader.substring(7);
        String requesterEmail = jwtUtil.extractUsername(token);
        Role requesterRole = jwtUtil.extractRole(token);

        return mediaService.deleteMedia(mediaId, requesterEmail, requesterRole);
    }

    @GetMapping("/get/{eventId}")
    public ResponseEntity<ApiResponse> getMediaByEvent(@PathVariable Long eventId) {
        return mediaService.getMediaByEvent(eventId);
    }
}