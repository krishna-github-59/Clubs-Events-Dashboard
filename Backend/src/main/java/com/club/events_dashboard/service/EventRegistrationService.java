package com.club.events_dashboard.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import com.club.events_dashboard.constants.PaymentStatus;
import com.club.events_dashboard.dto.ApiResponse;
import com.club.events_dashboard.dto.EventRegistrationRequestDTO;
import com.club.events_dashboard.entity.Event;
import com.club.events_dashboard.entity.EventRegistration;
// import com.club.events_dashboard.entity.Guest;
import com.club.events_dashboard.entity.User;
import com.club.events_dashboard.repository.EventRegistrationRepository;
import com.club.events_dashboard.repository.EventRepository;
// import com.club.events_dashboard.repository.GuestRepository;
import com.club.events_dashboard.repository.PaymentRepository;
import com.club.events_dashboard.repository.UserRepository;
import com.club.events_dashboard.security.JwtUtil;

import jakarta.transaction.Transactional;

@Service
public class EventRegistrationService {

    @Autowired
    private EventRepository eventRepository;
    @Autowired
    private UserRepository userRepository;
//     @Autowired
//     private GuestRepository guestRepository;
    @Autowired
    private EventRegistrationRepository registrationRepository;
    @Autowired
    private PaymentRepository paymentRepository;
    @Autowired
    private JwtUtil jwtUtil;

    @Transactional
    public ResponseEntity<ApiResponse> registerForEvent(
        EventRegistrationRequestDTO request,
        String authHeader // pass Authorization header from controller
    ) {
        Event event = eventRepository.findById(request.getEventId())
                .orElseThrow(() ->
                        new IllegalArgumentException("Event not found"));

        String email="";
        User user = null;

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
                // Logged-in user: extract email from JWT
                String token = authHeader.substring(7);
                email = jwtUtil.extractUsername(token);

                user = userRepository.findByEmail(email)
                        .orElseThrow(() -> new RuntimeException("User not found"));

                boolean alreadyRegistered = registrationRepository
                .existsByEventIdAndUserId(event.getId(), user.getId());

                if (alreadyRegistered) {
                return ResponseEntity.badRequest()
                        .body(new ApiResponse(false, "User already registered for this event"));
                }
        } 
        // else {
        //         // Guest user
        //         email = request.getGuestEmail();

        //         if (email == null || email.isBlank()) {
        //         throw new IllegalArgumentException("Guest email is required");
        //         }

        //         boolean alreadyRegistered = registrationRepository
        //         .existsByEventIdAndGuestEmail(event.getId(), email);
        //         if (alreadyRegistered) {
        //         return ResponseEntity.badRequest()
        //                 .body(new ApiResponse(false, "Guest already registered for this event"));
        //         }
        // }

        // Payment check
        if (event.getEntryFee() > 0) {
                boolean hasPaid = paymentRepository
                        .existsByEventIdAndUserEmailAndStatus(
                                event.getId(),
                                email,
                                PaymentStatus.SUCCESS
                        );

                if (!hasPaid) {
                return ResponseEntity.status(402)
                        .body(new ApiResponse(false, "PAYMENT_REQUIRED"));
                }
        }

        // Create registration
        return createRegistration(event, request, user, email);
    }

    private ResponseEntity<ApiResponse> createRegistration(
        Event event,
        EventRegistrationRequestDTO request,
        User user,
        String email
    ) {
        EventRegistration registration = new EventRegistration();
        registration.setEvent(event);

        if (user != null) {
                registration.setUser(user);
        } 
        // else {
        //         Guest guest = guestRepository.findByEmail(email)
        //                 .orElseGet(() -> {
        //                 Guest g = new Guest();
        //                 g.setName(request.getGuestName());
        //                 g.setEmail(request.getGuestEmail());
        //                 return guestRepository.save(g);
        //                 });
        //         registration.setGuest(guest);
        // }

        registrationRepository.save(registration);

        return ResponseEntity.ok(
                new ApiResponse(true, "Registered successfully")
        );
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


//     public ResponseEntity<ApiResponse> getRegisteredEventsForGuest(Long guestId) {
//         List<Event> events = registrationRepository.findByGuestId(guestId)
//                 .stream()
//                 .map(EventRegistration::getEvent)
//                 .toList();

//         return ResponseEntity.ok(
//                 new ApiResponse(true, "Registered events for guest fetched successfully", events)
//         );
//     }

}