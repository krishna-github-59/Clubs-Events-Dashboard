package com.club.events_dashboard.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.club.events_dashboard.dto.ApiResponse;
import com.club.events_dashboard.dto.EventRegistrationRequestDTO;
import com.club.events_dashboard.entity.Event;
import com.club.events_dashboard.entity.EventRegistration;
import com.club.events_dashboard.entity.Guest;
import com.club.events_dashboard.entity.User;
import com.club.events_dashboard.repository.EventRegistrationRepository;
import com.club.events_dashboard.repository.EventRepository;
import com.club.events_dashboard.repository.GuestRepository;
import com.club.events_dashboard.repository.UserRepository;

@Service
public class EventRegistrationService {

    @Autowired
    private EventRepository eventRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private GuestRepository guestRepository;
    @Autowired
    private EventRegistrationRepository registrationRepository;

    
    public ResponseEntity<ApiResponse> registerForEvent(EventRegistrationRequestDTO request) {
        Optional<Event> eventOpt = eventRepository.findById(request.getEventId());
        if (eventOpt.isEmpty()) {
            return ResponseEntity.badRequest().body(new ApiResponse(false, "Event not found"));
        }

        Event event = eventOpt.get();

        // Student registration
        if (request.getUserId() != null) {
            if (registrationRepository.existsByEventIdAndUserId(request.getEventId(), request.getUserId())) {
                return ResponseEntity.badRequest().body(new ApiResponse(false, "Already registered"));
            }

            User user = userRepository.findById(request.getUserId())
                    .orElse(null);
            if (user == null)
                return ResponseEntity.badRequest().body(new ApiResponse(false, "User not found"));

            EventRegistration reg = new EventRegistration();
            reg.setEvent(event);
            reg.setUser(user);
            registrationRepository.save(reg);

            return ResponseEntity.ok(new ApiResponse(true, "Registered successfully"));
        }

        // Guest registration
        if (request.getGuestId() != null) {
            if (registrationRepository.existsByEventIdAndGuestId(request.getEventId(), request.getGuestId())) {
                return ResponseEntity.badRequest().body(new ApiResponse(false, "Already registered"));
            }

            Guest guest = guestRepository.findById(request.getGuestId())
                    .orElse(null);
            if (guest == null)
                return ResponseEntity.badRequest().body(new ApiResponse(false, "Guest not found"));

            EventRegistration reg = new EventRegistration();
            reg.setEvent(event);
            reg.setGuest(guest);
            registrationRepository.save(reg);

            return ResponseEntity.ok(new ApiResponse(true, "Registered successfully"));
        }

        return ResponseEntity.badRequest().body(new ApiResponse(false, "Invalid request"));
    }


    public ResponseEntity<ApiResponse> getRegisteredEventsForUser(Long userId) {
        List<Event> events = registrationRepository.findByUserId(userId)
                .stream()
                .map(EventRegistration::getEvent)
                .toList();

        return ResponseEntity.ok(
                new ApiResponse(true, "Registered events for user fetched successfully", events)
        );
    }


    public ResponseEntity<ApiResponse> getRegisteredEventsForGuest(Long guestId) {
        List<Event> events = registrationRepository.findByGuestId(guestId)
                .stream()
                .map(EventRegistration::getEvent)
                .toList();

        return ResponseEntity.ok(
                new ApiResponse(true, "Registered events for guest fetched successfully", events)
        );
    }

}

