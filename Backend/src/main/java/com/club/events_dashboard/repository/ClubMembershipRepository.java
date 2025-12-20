package com.club.events_dashboard.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.club.events_dashboard.entity.Club;
import com.club.events_dashboard.entity.User;
import com.club.events_dashboard.entity.ClubMembership;
import java.util.List;
import java.util.Optional;

public interface ClubMembershipRepository extends JpaRepository<ClubMembership, Long> {
    List<ClubMembership> findByClubId(Long clubId);
    List<ClubMembership> findByUserId(Long userId);
    Optional<ClubMembership> findByUserIdAndClubId(Long userId, Long clubId);
    boolean existsByClubAndUser(Club club, User user);
    Optional<ClubMembership> findByUserEmailAndClubId(String email, Long clubId);
}
