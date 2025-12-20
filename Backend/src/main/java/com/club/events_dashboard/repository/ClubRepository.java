package com.club.events_dashboard.repository;

import com.club.events_dashboard.entity.Club;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ClubRepository extends JpaRepository<Club, Long> {
    boolean existsByName(String name);
}
