package com.club.events_dashboard.service;

import com.club.events_dashboard.entity.*;
import com.club.events_dashboard.dto.ApiResponse;
import com.club.events_dashboard.repository.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class ClubMembershipService {

    @Autowired
    private ClubMembershipRepository clubMembershipRepository;

    @Autowired
    private ClubRepository clubRepository;

    @Autowired
    private UserRepository userRepository;

    private boolean canManageMembership(Club club, String requesterEmail, Role requesterRole) {
        return (requesterRole == Role.SUPER_ADMIN) || (club != null && club.getAdminEmail().equalsIgnoreCase(requesterEmail));
    }


    public ResponseEntity<ApiResponse> addMemberToClub(Long clubId, Long userId, String requesterEmail, Role requesterRole) {
        Optional<Club> clubOpt = clubRepository.findById(clubId);
        Optional<User> userOpt = userRepository.findById(userId);

        if (clubOpt.isEmpty() || userOpt.isEmpty()) {
            return ResponseEntity.badRequest().body(new ApiResponse(false, "Club or User not found"));
        }

        Club club = clubOpt.get();
        User user = userOpt.get();

        if (!canManageMembership(club, requesterEmail, requesterRole)) {
            return ResponseEntity.status(403).body(new ApiResponse(false, "Access denied"));
        }

        boolean alreadyMember = clubMembershipRepository.existsByClubAndUser(club, user);
        if (alreadyMember) {
            return ResponseEntity.badRequest().body(new ApiResponse(false, "User already a member of this club"));
        }

        ClubMembership membership = new ClubMembership();
        membership.setClub(club);
        membership.setUser(user);
        membership.setJoinedDate(LocalDate.now());
        clubMembershipRepository.save(membership);

        return ResponseEntity.ok(new ApiResponse(true, "Member added successfully", membership));
    }


    public ResponseEntity<ApiResponse> removeMemberFromClub(Long clubId, Long userId, String requesterEmail, Role requesterRole) {
        Optional<Club> clubOpt = clubRepository.findById(clubId);
        Optional<User> userOpt = userRepository.findById(userId);

        if (clubOpt.isEmpty() || userOpt.isEmpty()) {
            return ResponseEntity.badRequest().body(new ApiResponse(false, "Club or User not found"));
        }

        Club club = clubOpt.get();
        User user = userOpt.get();

        if (!canManageMembership(club, requesterEmail, requesterRole)) {
            return ResponseEntity.status(403).body(new ApiResponse(false, "Access denied"));
        }

        Optional<ClubMembership> membership = clubMembershipRepository.findByUserIdAndClubId(club.getId(), user.getId());
        if (membership.isEmpty()) {
            return ResponseEntity.badRequest().body(new ApiResponse(false, "User is not a member of this club"));
        }

        clubMembershipRepository.delete(membership.get());
        return ResponseEntity.ok(new ApiResponse(true, "Member removed successfully"));
    }


    public ResponseEntity<ApiResponse> getMembersByClub(Long clubId) {
        List<ClubMembership> members = clubMembershipRepository.findByClubId(clubId);

        if (members.isEmpty()) {
            return ResponseEntity.ok(new ApiResponse(false, "No members found for this club"));
        }

        return ResponseEntity.ok(new ApiResponse(true, "Members fetched successfully", members));
    }


    public ResponseEntity<ApiResponse> getClubsByUser(Long userId) {
        List<ClubMembership> clubs = clubMembershipRepository.findByUserId(userId);

        if (clubs.isEmpty()) {
            return ResponseEntity.ok(new ApiResponse(false, "User is not a member of any club"));
        }

        return ResponseEntity.ok(new ApiResponse(true, "Clubs fetched successfully", clubs));
    }
}
