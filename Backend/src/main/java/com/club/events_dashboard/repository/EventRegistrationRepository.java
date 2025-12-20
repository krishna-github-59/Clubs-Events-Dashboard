package com.club.events_dashboard.repository;

import com.club.events_dashboard.entity.EventRegistration;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface EventRegistrationRepository extends JpaRepository<EventRegistration, Long> {

    boolean existsByEventIdAndUserId(Long eventId, Long userId);

    boolean existsByEventIdAndGuestId(Long eventId, Long guestId);

    List<EventRegistration> findByUserId(Long userId);

    List<EventRegistration> findByGuestId(Long guestId);
}
