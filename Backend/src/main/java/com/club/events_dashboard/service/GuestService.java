// package com.club.events_dashboard.service;

// import java.util.List;

// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.http.ResponseEntity;
// import org.springframework.stereotype.Service;
// import com.club.events_dashboard.dto.ApiResponse;
// import com.club.events_dashboard.dto.GuestDTO;
// import com.club.events_dashboard.entity.Role;
// import com.club.events_dashboard.repository.GuestRepository;

// @Service
// public class GuestService {

//     @Autowired
//     private GuestRepository guestRepository;

//     // GET GUEST BY ID
//     public ResponseEntity<ApiResponse> getGuestById(Long id) {
//         return guestRepository.findById(id)
//                 .map(guest -> {
//                     GuestDTO dto = new GuestDTO(
//                             guest.getId(),
//                             guest.getName(),
//                             guest.getEmail()
//                     );
//                     return ResponseEntity.ok(
//                             new ApiResponse(true, "Guest found", dto)
//                     );
//                 })
//                 .orElseGet(() -> ResponseEntity
//                         .status(404)
//                         .body(new ApiResponse(false, "Guest not found")));
//     }

//     // GET ALL GUESTS
//     public ResponseEntity<ApiResponse> getAllGuests() {
//         List<GuestDTO> guests = guestRepository.findAll()
//                 .stream()
//                 .map(g -> new GuestDTO(g.getId(), g.getName(), g.getEmail()))
//                 .toList();

//         return ResponseEntity.ok(
//                 new ApiResponse(true, "Guests fetched successfully", guests)
//         );
//     }

//     // UPDATE GUEST
//     public ResponseEntity<ApiResponse> updateGuest(Long id,Role requesterRole, GuestDTO guestDTO) {
//         // Only allow superadmin
//         if (requesterRole != Role.SUPER_ADMIN) {
//                 return ResponseEntity.status(403)
//                         .body(new ApiResponse(false, "Only superadmin can update guests"));
//         }
//         return guestRepository.findById(id)
//                 .map(guest -> {
//                     guest.setName(guestDTO.getName());
//                     guest.setEmail(guestDTO.getEmail());
//                     guestRepository.save(guest);

//                     return ResponseEntity.ok(
//                             new ApiResponse(true, "Guest updated successfully")
//                     );
//                 })
//                 .orElseGet(() -> ResponseEntity
//                         .status(404)
//                         .body(new ApiResponse(false, "Guest not found")));
//     }
// }

