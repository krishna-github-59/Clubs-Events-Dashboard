package com.club.events_dashboard.service;

import com.club.events_dashboard.constants.Role;
import com.club.events_dashboard.dto.ApiResponse;
import com.club.events_dashboard.dto.ClubRequestDTO;
import com.club.events_dashboard.dto.ClubResponseDTO;
import com.club.events_dashboard.entity.Club;
import com.club.events_dashboard.entity.User;
import com.club.events_dashboard.repository.ClubRepository;
import com.club.events_dashboard.repository.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ClubService {

    @Autowired
    private ClubRepository clubRepository;

    @Autowired
    private UserRepository userRepository;

    //Create Club (SuperAdmin Only)
    public ResponseEntity<ApiResponse> createClub(ClubRequestDTO clubRequest, String requesterEmail, Role requesterRole) {
        if (requesterRole != Role.SUPER_ADMIN) {
            return ResponseEntity.status(403).body(new ApiResponse(false, "Only Super Admins can create clubs"));
        }

        if (clubRepository.existsByName(clubRequest.getName())) {
            return ResponseEntity.status(404).body(new ApiResponse(false, "Club already exists"));
        }

        // 🔍 Validate that adminEmail belongs to a valid CLUB_ADMIN user
        Optional<User> adminUser = userRepository.findByEmail(clubRequest.getAdminEmail());
        if (adminUser.isEmpty()) {
            return ResponseEntity.status(400)
                    .body(new ApiResponse(false, "Admin email does not belong to any user"));
        }

        if (adminUser.get().getRole() != Role.CLUB_ADMIN) {
            return ResponseEntity.status(400)
                    .body(new ApiResponse(false, "Provided adminEmail doesn't belong to an admin"));
        }

        String createdBy = requesterEmail;
        Club club = new Club(
            clubRequest.getName(),
            clubRequest.getDescription(),
            createdBy,
            clubRequest.getAdminEmail()
        );

        adminUser.get().setClub(club);

        Club savedClub = clubRepository.save(club);

        ClubResponseDTO responseDTO = new ClubResponseDTO(savedClub);
        userRepository.save(adminUser.get());
        return ResponseEntity.ok(new ApiResponse(true, "Club created successfully", responseDTO));
    }

    // Update Club (SuperAdmin Only)
    public ResponseEntity<ApiResponse> updateClub(Long id, ClubRequestDTO clubRequest, Role requesterRole) {
        if (requesterRole != Role.SUPER_ADMIN) {
            return ResponseEntity.status(403).body(new ApiResponse(false, "Only Super Admins can update clubs"));
        }

        // Validate adminEmail before updating
        Optional<User> adminUser = userRepository.findByEmail(clubRequest.getAdminEmail());
        if (adminUser.isEmpty()) {
            return ResponseEntity.status(400)
                    .body(new ApiResponse(false, "Admin email does not belong to any user"));
        }

        if (adminUser.get().getRole() != Role.CLUB_ADMIN) {
            return ResponseEntity.status(400)
                    .body(new ApiResponse(false, "Provided adminEmail doesn't belong to an admin"));
        }

        return clubRepository.findById(id)
                .map(existingClub -> {
                    existingClub.setName(clubRequest.getName());
                    existingClub.setDescription(clubRequest.getDescription());
                    existingClub.setAdminEmail(clubRequest.getAdminEmail());

                    Club updatedClub = clubRepository.save(existingClub);
                    ClubResponseDTO responseDTO = new ClubResponseDTO(updatedClub);

                    return ResponseEntity.ok(new ApiResponse(true, "Club updated successfully", responseDTO));
                })
                .orElseGet(() -> ResponseEntity.status(404)
                        .body(new ApiResponse(false, "Club not found")));
    }

    // Delete club (SuperAdmin only)
    public ResponseEntity<ApiResponse> deleteClub(Long id, Role requesterRole) {
        if (requesterRole != Role.SUPER_ADMIN) {
            return ResponseEntity.status(403).body(new ApiResponse(false, "Only Super Admins can delete clubs"));
        }

        return clubRepository.findById(id)
                .map(existingClub -> {
                    clubRepository.delete(existingClub);
                    return ResponseEntity.ok(new ApiResponse(true, "Club deleted successfully"));
                })
                .orElseGet(() -> ResponseEntity.status(404)
                        .body(new ApiResponse(false, "Club not found")));
    }

     // Get all clubs
    public ResponseEntity<ApiResponse> getAllClubs() {
        List<ClubResponseDTO> clubs = clubRepository.findAll()
                .stream()
                .map(club -> new ClubResponseDTO(club))
                .collect(Collectors.toList());

        return ResponseEntity.ok(new ApiResponse(true, "Fetched all clubs", clubs));
    }

    // Get club by id
    public ResponseEntity<ApiResponse> getClubById(Long id) {
        return clubRepository.findById(id)
                .map(club -> {
                    ClubResponseDTO dto = new ClubResponseDTO(club);
                    return ResponseEntity.ok(new ApiResponse(true, "Club found", dto));
                })
                .orElseGet(() -> ResponseEntity.status(404)
                        .body(new ApiResponse(false, "Club not found")));
    }
}