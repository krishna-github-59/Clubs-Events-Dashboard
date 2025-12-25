package com.club.events_dashboard.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.club.events_dashboard.dto.ApiResponse;
import com.club.events_dashboard.dto.EventRequestDTO;
import com.club.events_dashboard.dto.EventResponseDTO;
import com.club.events_dashboard.entity.Club;
import com.club.events_dashboard.entity.Event;
import com.club.events_dashboard.entity.Media;
import com.club.events_dashboard.entity.Role;
import com.club.events_dashboard.repository.ClubRepository;
import com.club.events_dashboard.repository.EventRepository;
import com.club.events_dashboard.repository.MediaRepository;
import com.club.events_dashboard.specification.EventSpecification;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class EventService {

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private ClubRepository clubRepository;

    @Autowired
    private FileService fileService;

    @Autowired
    private MediaRepository mediaRepository;

    @Autowired
    private Cloudinary cloudinary;

    // Helper — check if requested user can manage this event
    private boolean canManageEvent(Club club, String requesterEmail, Role requesterRole) {
        return (requesterRole == Role.SUPER_ADMIN)
                || (club != null && club.getAdminEmail().equalsIgnoreCase(requesterEmail));
    }

    // Create event
    public ResponseEntity<ApiResponse> createEvent(
        EventRequestDTO request,
        String requesterEmail,
        Role requesterRole,
        MultipartFile poster) {

        // 1. Validate Club
        Club club = clubRepository.findById(request.getClubId()).orElse(null);
        if (club == null) {
            return ResponseEntity.status(404).body(new ApiResponse(false, "Club not found"));
        }

        // 2. Check Access
        if (!canManageEvent(club, requesterEmail, requesterRole)) {
            return ResponseEntity.status(403).body(new ApiResponse(false, "Access denied"));
        }

        // 3. Upload Poster Image (optional)
        String imageUrl = null;

        if (poster != null && !poster.isEmpty()) {
            try {
                imageUrl = fileService.uploadFile(poster); // returns "/uploads/file.jpg"
            } catch (Exception e) {
                return ResponseEntity.status(500)
                        .body(new ApiResponse(false, "Image upload failed: " + e.getMessage()));
            }
        }

        // 4. Create Event object with new fields
        Event event = new Event();
        event.setName(request.getName());
        event.setDescription(request.getDescription());
        event.setDate(request.getDate());
        event.setVenue(request.getVenue());
        event.setClub(club);
        event.setEntryFee(request.getEntryFee());
        event.setStartTime(request.getStartTime());
        event.setEndTime(request.getEndTime());
        event.setImageUrl(imageUrl);

        // 5. Save Event
        Event savedEvent = eventRepository.save(event);

        // 6. Prepare Response DTO
        EventResponseDTO responseDTO = new EventResponseDTO(
                savedEvent.getId(),
                savedEvent.getName(),
                savedEvent.getDescription(),
                savedEvent.getDate(),
                savedEvent.getVenue(),
                savedEvent.getClub().getId(),
                savedEvent.getClub().getName(),
                savedEvent.getEntryFee(),
                savedEvent.getImageUrl(),
                savedEvent.getStartTime(),
                savedEvent.getEndTime()
        );

        return ResponseEntity.ok(new ApiResponse(true, "Event created successfully", responseDTO));
    }


    // Update event
    public ResponseEntity<ApiResponse> updateEvent(
            Long id,
            EventRequestDTO request,
            String requesterEmail,
            Role requesterRole,
            MultipartFile poster) {

        return eventRepository.findById(id)
                .map(existingEvent -> {

                    Club club = existingEvent.getClub();
                    if (!canManageEvent(club, requesterEmail, requesterRole)) {
                        return ResponseEntity.status(403).body(new ApiResponse(false, "Access denied"));
                    }

                    existingEvent.setName(request.getName());
                    existingEvent.setDescription(request.getDescription());
                    existingEvent.setDate(request.getDate());
                    existingEvent.setVenue(request.getVenue());
                    existingEvent.setEntryFee(request.getEntryFee());
                    existingEvent.setStartTime(request.getStartTime());
                    existingEvent.setEndTime(request.getEndTime());

                    // If a new poster is provided, upload it and delete the old one
                     // If a new poster is provided, upload it and delete the old one
                    if (poster != null && !poster.isEmpty()) {
                        try {
                            // upload new
                            String newUrl = fileService.uploadFile(poster);

                            // attempt to delete old poster (if present and not equal)
                            String oldUrl = existingEvent.getImageUrl();
                            if (oldUrl != null && !oldUrl.trim().isEmpty() && !oldUrl.equals(newUrl)) {
                                try {
                                    fileService.deleteFile(oldUrl);
                                } catch (Exception ex) {
                                    // Log and continue; deleting old poster failing should not block update
                                    // (Replace with your logger if available)
                                    System.err.println("Failed to delete old poster: " + ex.getMessage());
                                }
                            }

                            existingEvent.setImageUrl(newUrl);
                        } catch (Exception e) {
                            return ResponseEntity.status(500).body(new ApiResponse(false, "Image upload failed: " + e.getMessage()));
                        }
                    }


                    Event updated = eventRepository.save(existingEvent);
                    EventResponseDTO responseDTO = new EventResponseDTO(
                        updated.getId(),
                        updated.getName(),
                        updated.getDescription(),
                        updated.getDate(),
                        updated.getVenue(),
                        updated.getClub().getId(),
                        updated.getClub().getName(),
                        updated.getEntryFee(),
                        updated.getImageUrl(),
                        updated.getStartTime(),
                        updated.getEndTime()
                    );

                    return ResponseEntity.ok(new ApiResponse(true, "Event updated successfully", responseDTO));
                })
                .orElseGet(() -> ResponseEntity.status(404).body(new ApiResponse(false, "Event not found")));
    }
    // Delete event
    public ResponseEntity<ApiResponse> deleteEvent(Long id, String requesterEmail, Role requesterRole) {
        return eventRepository.findById(id)
                .map(existingEvent -> {
                    Club club = existingEvent.getClub();

                    if (!canManageEvent(club, requesterEmail, requesterRole)) {
                        return ResponseEntity.status(403).body(new ApiResponse(false, "Access Denied"));
                    }

                    String posterUrl = existingEvent.getImageUrl();

                    // Attempt to delete poster file (do not fail the whole operation if deletion fails)
                    if (posterUrl != null && !posterUrl.trim().isEmpty()) {
                        try {
                            fileService.deleteFile(posterUrl);
                        } catch (Exception ex) {
                            // log and continue
                            System.err.println("Failed to delete poster during event deletion: " + ex.getMessage());
                        }
                    }

                    try {
                        // 1. Fetch all media for event
                        List<Media> mediaList = mediaRepository.findByEventId(existingEvent.getId());

                        // 2. Delete from Cloudinary
                        for (Media m : mediaList) {
                            try {
                                cloudinary.uploader().destroy(m.getPublicId(), ObjectUtils.emptyMap());
                            } catch (Exception ignored) {
                                // continue deleting everything else
                            }
                        }

                        // 3. Delete from DB
                        mediaRepository.deleteAll(mediaList);

                        // 4. Delete Event
                        eventRepository.delete(existingEvent);

                    } catch (Exception e) {
                        return ResponseEntity.status(500)
                                .body(new ApiResponse(false, "Error deleting event media: " + e.getMessage()));
                    }

                    return ResponseEntity.ok(new ApiResponse(true, "Event deleted successfully"));
                })
                .orElseGet(() -> ResponseEntity.status(404)
                        .body(new ApiResponse(false, "Event not found")));
    }

    // Get all events (public)
    public ResponseEntity<ApiResponse> getAllEvents() {
        List<EventResponseDTO> eventResponseDTOs = eventRepository.findAll().stream()
                .map(e -> new EventResponseDTO(
                        e.getId(),
                        e.getName(),
                        e.getDescription(),
                        e.getDate(),
                        e.getVenue(),
                        e.getClub().getId(),
                        e.getClub().getName(),
                        e.getEntryFee()
                    ))
                .collect(Collectors.toList());

        return ResponseEntity.ok(new ApiResponse(true, "All events fetched successfully", eventResponseDTOs));
    }

    public ResponseEntity<ApiResponse> getEventById(Long id) {
        return eventRepository.findById(id)
                .map(e -> {
                    EventResponseDTO eventResponseDTO = new EventResponseDTO(
                            e.getId(),
                            e.getName(),
                            e.getDescription(),
                            e.getDate(),
                            e.getVenue(),
                            e.getClub().getId(),
                            e.getClub().getName(),
                            e.getEntryFee()
                    );
                    return ResponseEntity.ok(new ApiResponse(true, "Event fetched successfully", eventResponseDTO));
                })
                .orElseGet(() ->ResponseEntity.status(404).body(new ApiResponse(false, "Event not found", null)));
    }


    // filter events (public)
    public ResponseEntity<ApiResponse> getFilteredEvents(Long clubId, String name, LocalDate startDate, LocalDate endDate) {
        Specification<Event> spec = Specification
                .where(EventSpecification.hasClubId(clubId))
                .and(EventSpecification.hasNameLike(name))
                .and(EventSpecification.hasStartDate(startDate))
                .and(EventSpecification.hasEndDate(endDate));

        List<Event> events = eventRepository.findAll(spec);

        if (events.isEmpty()) {
            return ResponseEntity.ok(new ApiResponse(false, "No events found matching the criteria"));
        }

        return ResponseEntity.ok(new ApiResponse(true, "Events fetched successfully", events));
    }

    // -------- Upcoming events (date >= today) ----------
    public ResponseEntity<ApiResponse> getUpcomingEvents() {
        LocalDate today = LocalDate.now();
        List<EventResponseDTO> upcoming = eventRepository.findAllByDateGreaterThanEqualOrderByDateAsc(today).stream()
                .map(e -> new EventResponseDTO(
                        e.getId(),
                        e.getName(),
                        e.getDescription(),
                        e.getDate(),
                        e.getVenue(),
                        e.getClub().getId(),
                        e.getClub().getName(),
                        e.getEntryFee()
                    ))
                .collect(Collectors.toList());

        return ResponseEntity.ok(new ApiResponse(true, "Upcoming events fetched", upcoming));
    }

    // -------- Past events (date < today) ----------
    public ResponseEntity<ApiResponse> getPastEvents() {
        LocalDate today = LocalDate.now();
        List<EventResponseDTO> past = eventRepository.findAllByDateBeforeOrderByDateDesc(today).stream()
                .map(e -> new EventResponseDTO(
                        e.getId(),
                        e.getName(),
                        e.getDescription(),
                        e.getDate(),
                        e.getVenue(),
                        e.getClub().getId(),
                        e.getClub().getName(),
                        e.getEntryFee()
                    ))
                .collect(Collectors.toList());

        return ResponseEntity.ok(new ApiResponse(true, "Past events fetched", past));
    }

    // get past and upcoming events
    public ResponseEntity<ApiResponse> getEventsDashboard() {

        // Call existing methods
        ResponseEntity<ApiResponse> upResp = getUpcomingEvents();
        ResponseEntity<ApiResponse> pastResp = getPastEvents();

        Object upcoming = upResp.getBody() != null ? upResp.getBody().getData() : null;
        Object past = pastResp.getBody() != null ? pastResp.getBody().getData() : null;

        // Build combined response
        Map<String, Object> result = new HashMap<>();
        result.put("upcoming", upcoming);
        result.put("past", past);

        return ResponseEntity.ok(new ApiResponse(true, "Dashboard events fetched successfully", result));
    }

    public ResponseEntity<ApiResponse> getUpcomingEventsByClub(Long clubId) {
        LocalDate today = LocalDate.now();

        List<EventResponseDTO> events = eventRepository
                .findByClubIdAndDateAfterOrderByDateAsc(clubId, today)
                .stream()
                .map(e -> new EventResponseDTO(
                        e.getId(),
                        e.getName(),
                        e.getDescription(),
                        e.getDate(),
                        e.getVenue(),
                        e.getClub().getId(),
                        e.getClub().getName(),
                        e.getEntryFee()
                    ))
                .toList();

        return ResponseEntity.ok(new ApiResponse(true, "Upcoming events fetched", events));
    }

    public ResponseEntity<ApiResponse> getPastEventsByClub(Long clubId) {
        LocalDate today = LocalDate.now();

        List<EventResponseDTO> events = eventRepository
                .findByClubIdAndDateBeforeOrderByDateDesc(clubId, today)
                .stream()
                .map(e -> new EventResponseDTO(
                        e.getId(),
                        e.getName(),
                        e.getDescription(),
                        e.getDate(),
                        e.getVenue(),
                        e.getClub().getId(),
                        e.getClub().getName(),
                        e.getEntryFee()
                    ))
                .toList();

        return ResponseEntity.ok(new ApiResponse(true, "Past events fetched", events));
    }

}
