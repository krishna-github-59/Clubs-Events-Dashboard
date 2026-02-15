// package com.club.events_dashboard.controller;

// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.http.ResponseEntity;
// import org.springframework.web.bind.annotation.*;

// import com.club.events_dashboard.dto.ApiResponse;
// import com.club.events_dashboard.dto.GuestDTO;
// import com.club.events_dashboard.entity.Role;
// import com.club.events_dashboard.security.JwtUtil;
// import com.club.events_dashboard.service.GuestService;

// @RestController
// @RequestMapping("/api/guests")
// public class GuestController {
//     @Autowired
//     private GuestService guestService;

//     @Autowired
//     private JwtUtil jwtUtil;

//     // GET GUEST BY ID
//     @GetMapping("/{id}")
//     public ResponseEntity<ApiResponse> getGuestById(@PathVariable Long id) {
//         return guestService.getGuestById(id);
//     }

//     // GET ALL GUESTS
//     @GetMapping("/all")
//     public ResponseEntity<ApiResponse> getAllGuests() {
//         return guestService.getAllGuests();
//     }

//     // UPDATE GUEST
//     @PutMapping("/update/{id}")
//     public ResponseEntity<ApiResponse> updateGuest(
//             @PathVariable Long id,
//             @RequestHeader("Authorization") String authHeader,
//             @RequestBody GuestDTO guestDTO
//     ) {
//         // Extract JWT token (without "Bearer ")
//         String token = authHeader.substring(7);

//         // Extract Role (user who is requesting to update the guest)
//         Role requesterRole = jwtUtil.extractRole(token);

//         return guestService.updateGuest(id, requesterRole, guestDTO);
//     }
// }
