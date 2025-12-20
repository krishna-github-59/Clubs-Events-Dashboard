package com.club.events_dashboard.repository;

import com.club.events_dashboard.entity.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;


@Repository
public interface EventRepository extends JpaRepository<Event, Long>, JpaSpecificationExecutor<Event> {
    List<Event> findByClubId(Long id);
    boolean existsByName(String name);
    int countEventsByClubId(Long clubId);
    
    List<Event> findAllByDateGreaterThanEqualOrderByDateAsc(LocalDate date);
    List<Event> findAllByDateBeforeOrderByDateDesc(LocalDate date);

    List<Event> findByClubIdAndDateAfterOrderByDateAsc(Long clubId, LocalDate date);
    List<Event> findByClubIdAndDateBeforeOrderByDateDesc(Long clubId, LocalDate date);
}
