package com.club.events_dashboard.repository;

import com.club.events_dashboard.entity.Media;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface MediaRepository extends JpaRepository<Media, Long> {
    List<Media> findByEventId(Long eventId);
}
