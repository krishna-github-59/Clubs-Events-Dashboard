package com.club.events_dashboard.service;

import com.club.events_dashboard.constants.Role;
import com.club.events_dashboard.dto.ApiResponse;
import com.club.events_dashboard.dto.EventRequestDTO;
import com.club.events_dashboard.dto.EventResponseDTO;
import com.club.events_dashboard.entity.Club;
import com.club.events_dashboard.entity.Event;
import com.club.events_dashboard.entity.EventRegistration;
import com.club.events_dashboard.entity.Media;
import com.club.events_dashboard.entity.User;
import com.club.events_dashboard.repository.ClubRepository;
import com.club.events_dashboard.repository.EventRegistrationRepository;
import com.club.events_dashboard.repository.EventRepository;
import com.club.events_dashboard.repository.MediaRepository;
import com.club.events_dashboard.repository.UserRepository;
import com.club.events_dashboard.specification.EventSpecification;

import jakarta.transaction.Transactional;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.ArrayList;
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
    private MediaRepository mediaRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EventRegistrationRepository eventRegistrationRepository;

    @Autowired
    private CloudinaryService cloudinaryService;


    private boolean canManageEvent(Club club, String requesterEmail, Role requesterRole) {
        if (requesterRole == Role.SUPER_ADMIN) return true;

        if (requesterRole != Role.CLUB_ADMIN) return false;

        User user = userRepository.findByEmail(requesterEmail)
                .orElse(null);

        if (user == null || user.getClub() == null) return false;

        return user.getClub().getId().equals(club.getId());
    }

    //helper function
    private void applyPermissions(EventResponseDTO dto, Event event, Role role) {
        LocalDate today = LocalDate.now();
        boolean isUpcoming = event.getDate().isAfter(today);
        boolean isClubAdmin = role == Role.CLUB_ADMIN;

        dto.setCanEdit(isClubAdmin && isUpcoming);
        dto.setCanDelete(isClubAdmin && isUpcoming);
        dto.setCanAddMedia(isClubAdmin && !isUpcoming);
        dto.setCanViewMedia(!isUpcoming);
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
        String imagePublicId = null;

        if (poster != null && !poster.isEmpty()) {
            try {
                var uploadResult = cloudinaryService.uploadFile(
                        poster,
                        "event_posters/" + club.getName().replaceAll("\\s+", "_")

                );
                imageUrl = uploadResult.getUrl();
                imagePublicId = uploadResult.getPublicId();
            } catch (Exception e) {
                return ResponseEntity.status(500)
                        .body(new ApiResponse(false, "Poster upload failed"));
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
        event.setImagePublicId(imagePublicId);


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

                    if (poster != null && !poster.isEmpty()) {
                        try {
                            // upload new poster
                            var uploadResult = cloudinaryService.uploadFile(
                                    poster,
                                    "event_posters/" + club.getName().replaceAll("\\s+", "_")
                            );

                            // delete old poster
                            if (existingEvent.getImagePublicId() != null) {
                                try {
                                    cloudinaryService.deleteFile(existingEvent.getImagePublicId());
                                } catch (Exception e) {
                                    return ResponseEntity.status(500)
                                    .body(new ApiResponse(false, "Failed to delete old poster"));
                                }
                            }

                            existingEvent.setImageUrl(uploadResult.getUrl());
                            existingEvent.setImagePublicId(uploadResult.getPublicId());

                        } catch (Exception e) {
                            return ResponseEntity.status(500)
                                    .body(new ApiResponse(false, "Poster update failed"));
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
    @Transactional
    public ResponseEntity<ApiResponse> deleteEvent(Long id, String requesterEmail, Role requesterRole) {
        return eventRepository.findById(id)
                .map(existingEvent -> {
                    Club club = existingEvent.getClub();

                    if (club == null) {
                        return ResponseEntity.status(400)
                                .body(new ApiResponse(false, "Event does not have a valid club"));
                    }

                    if (!canManageEvent(club, requesterEmail, requesterRole)) {
                        return ResponseEntity.status(403).body(new ApiResponse(false, "Access Denied"));
                    }

                    // String posterUrl = existingEvent.getImageUrl();

                    // Attempt to delete poster file (do not fail the whole operation if deletion fails)
                    // delete poster
                    if (existingEvent.getImagePublicId() != null && !existingEvent.getImagePublicId().isEmpty()) {
                        try {
                            cloudinaryService.deleteFile(existingEvent.getImagePublicId());
                        } catch (Exception ignored) {}
                    }


                    // 1. Fetch all media for event
                    List<Media> mediaList = mediaRepository.findByEventId(existingEvent.getId());
                    if (mediaList == null) mediaList = new ArrayList<>();

                    // 2. Delete from Cloudinary
                    for (Media m : mediaList) {
                        if (m.getPublicId() != null && !m.getPublicId().isEmpty()) {
                            try {
                                cloudinaryService.deleteFile(m.getPublicId());
                            } catch (Exception ignored) {}
                        }
                    }

                    // 3. Delete from DB
                    if (!mediaList.isEmpty()) {
                        mediaRepository.deleteAll(mediaList);
                    }

                    List<EventRegistration> registrations = eventRegistrationRepository.findByEventId(existingEvent.getId());
                    if (!registrations.isEmpty()) {
                        eventRegistrationRepository.deleteAll(registrations);
                    }
                    // 4. Delete Event
                    eventRepository.delete(existingEvent);

                    

                    return ResponseEntity.ok(new ApiResponse(true, "Event deleted successfully"));
                })
                .orElseGet(() -> ResponseEntity.status(404)
                        .body(new ApiResponse(false, "Event not found")));
    }

    // Get all events (public)
    public ResponseEntity<ApiResponse> getAllEvents() {
        List<EventResponseDTO> eventResponseDTOs = eventRepository.findAll()
                .stream()
                .map(e -> {
                    EventResponseDTO dto = new EventResponseDTO(
                        e.getId(),
                        e.getName(),
                        e.getDescription(),
                        e.getDate(),
                        e.getVenue(),
                        e.getClub().getId(),
                        e.getClub().getName(),
                        e.getEntryFee(),
                        e.getImageUrl(),
                        e.getStartTime(),
                        e.getEndTime()
                    );
                    dto.setCanEdit(false);
                    dto.setCanDelete(false);
                    dto.setCanAddMedia(false);
                    dto.setCanViewMedia(true);

                    return dto;
                })
                .toList();

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
                            e.getEntryFee(),
                            e.getImageUrl(),
                            e.getStartTime(),
                            e.getEndTime()
                    );
                    eventResponseDTO.setCanEdit(false);
                    eventResponseDTO.setCanDelete(false);
                    return ResponseEntity.ok(new ApiResponse(true, "Event fetched successfully", eventResponseDTO));
                })
                .orElseGet(() ->ResponseEntity.status(404).body(new ApiResponse(false, "Event not found", null)));
    }

    public ResponseEntity<ApiResponse> getMyClubEvents(
            String email, Role role) {

        if (role != Role.CLUB_ADMIN) {
            return ResponseEntity.status(403)
                    .body(new ApiResponse(false, "Access denied"));
        }

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        Club club = user.getClub();

        // Security check - club must exist
        if (club==null) {
            return ResponseEntity.status(403)
                    .body(new ApiResponse(false, "Club mismatch"));
        }

        List<EventResponseDTO> eventResponseDTOs = eventRepository
        .findByClubId(club.getId())
        .stream()
        .map(e -> {
            EventResponseDTO dto = new EventResponseDTO(
                    e.getId(),
                    e.getName(),
                    e.getDescription(),
                    e.getDate(),
                    e.getVenue(),
                    club.getId(),
                    club.getName(),
                    e.getEntryFee(),
                    e.getImageUrl(),
                    e.getStartTime(),
                    e.getEndTime()
            );

            dto.setCanEdit(true);
            dto.setCanDelete(true);

            return dto;
        })
        .toList();

        return ResponseEntity.ok(
                new ApiResponse(true, "Events fetched successfully for club admin", eventResponseDTOs)
        );

    }

    public ResponseEntity<ApiResponse> getEventsByClubId(Long clubId) {
        Club club = clubRepository.findById(clubId)
                .orElseThrow(() -> new RuntimeException("Club not found"));

        List<EventResponseDTO> eventResponseDTOs = eventRepository
                .findByClubId(clubId)
                .stream()
                .map(e -> {
                    EventResponseDTO dto = new EventResponseDTO(
                            e.getId(),
                            e.getName(),
                            e.getDescription(),
                            e.getDate(),
                            e.getVenue(),
                            club.getId(),
                            club.getName(),
                            e.getEntryFee(),
                            e.getImageUrl(),
                            e.getStartTime(),
                            e.getEndTime()
                    );

                    dto.setCanEdit(true);
                    dto.setCanDelete(true);

                    return dto;
                })
                .toList();

        return ResponseEntity.ok(
                new ApiResponse(true, "Events fetched successfully", eventResponseDTOs)
        );
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
        List<EventResponseDTO> upcoming = eventRepository.findAllByDateGreaterThanEqualOrderByDateAsc(today)
                .stream()
                .map(e -> {
                    EventResponseDTO dto = new EventResponseDTO(
                        e.getId(),
                        e.getName(),
                        e.getDescription(),
                        e.getDate(),
                        e.getVenue(),
                        e.getClub().getId(),
                        e.getClub().getName(),
                        e.getEntryFee(),
                        e.getImageUrl(),
                        e.getStartTime(),
                        e.getEndTime()
                    );
                    dto.setCanEdit(false);
                    dto.setCanDelete(false);
                    dto.setCanAddMedia(false);
                    dto.setCanViewMedia(false);

                    return dto;
                })
                .toList();

        return ResponseEntity.ok(new ApiResponse(true, "Upcoming events fetched", upcoming));
    }

    // -------- Past events (date < today) ----------
    public ResponseEntity<ApiResponse> getPastEvents() {
        LocalDate today = LocalDate.now();
        List<EventResponseDTO> past = eventRepository.findAllByDateBeforeOrderByDateDesc(today)
                .stream()
                .map(e -> {
                    EventResponseDTO dto = new EventResponseDTO(
                        e.getId(),
                        e.getName(),
                        e.getDescription(),
                        e.getDate(),
                        e.getVenue(),
                        e.getClub().getId(),
                        e.getClub().getName(),
                        e.getEntryFee(),
                        e.getImageUrl(),
                        e.getStartTime(),
                        e.getEndTime()
                    );
                    dto.setCanEdit(false);
                    dto.setCanDelete(false);
                    dto.setCanAddMedia(false);
                    dto.setCanViewMedia(true);

                    return dto;
                })
                .toList();

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

    public ResponseEntity<ApiResponse> getUpcomingEventsByClub(String email, Role role) {
        LocalDate today = LocalDate.now();
        User user = userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));
        Long clubId = user.getClub().getId();

        List<EventResponseDTO> events = eventRepository
                .findByClubIdAndDateAfterOrderByDateAsc(clubId, today)
                .stream()
                .map(e -> {
                    EventResponseDTO dto = new EventResponseDTO(
                        e.getId(),
                        e.getName(),
                        e.getDescription(),
                        e.getDate(),
                        e.getVenue(),
                        e.getClub().getId(),
                        e.getClub().getName(),
                        e.getEntryFee(),
                        e.getImageUrl(),
                        e.getStartTime(),
                        e.getEndTime()
                    );
                    dto.setCanEdit(true);
                    dto.setCanDelete(true);
                    dto.setCanAddMedia(false);
                    dto.setCanViewMedia(true);

                    return dto;
                })
                .toList();

        return ResponseEntity.ok(new ApiResponse(true, "Upcoming events fetched", events));
    }

    public ResponseEntity<ApiResponse> getPastEventsByClub(String email, Role role) {
        LocalDate today = LocalDate.now();
        User user = userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));
        Long clubId = user.getClub().getId();

        List<EventResponseDTO> events = eventRepository
                .findByClubIdAndDateBeforeOrderByDateDesc(clubId, today)
                .stream()
                .map(e -> {
                    EventResponseDTO dto = new EventResponseDTO(
                        e.getId(),
                        e.getName(),
                        e.getDescription(),
                        e.getDate(),
                        e.getVenue(),
                        e.getClub().getId(),
                        e.getClub().getName(),
                        e.getEntryFee(),
                        e.getImageUrl(),
                        e.getStartTime(),
                        e.getEndTime()
                    );
                    dto.setCanEdit(false);
                    dto.setCanDelete(false);
                    dto.setCanAddMedia(true);
                    dto.setCanViewMedia(true);

                    return dto;
                })
                .toList();

        return ResponseEntity.ok(new ApiResponse(true, "Past events fetched", events));
    }

}
