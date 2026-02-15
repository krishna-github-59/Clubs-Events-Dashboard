package com.club.events_dashboard.repository;

import com.club.events_dashboard.constants.Role;
import com.club.events_dashboard.entity.User;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    Optional<Role> findByRole(Role role);
    boolean existsByRole(Role role);
    boolean existsByEmail(String email);
}
