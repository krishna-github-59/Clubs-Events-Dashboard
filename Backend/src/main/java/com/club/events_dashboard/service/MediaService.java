package com.club.events_dashboard.service;

import com.club.events_dashboard.dto.ApiResponse;
import com.club.events_dashboard.dto.MediaResponseDTO;
import com.club.events_dashboard.entity.Club;
import com.club.events_dashboard.entity.Event;
import com.club.events_dashboard.entity.Role;
import com.club.events_dashboard.entity.User;
import com.club.events_dashboard.entity.Media;
import com.club.events_dashboard.repository.ClubMembershipRepository;
import com.club.events_dashboard.repository.EventRepository;
import com.club.events_dashboard.repository.MediaRepository;
import com.club.events_dashboard.repository.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class MediaService {

    @Autowired
    private Cloudinary cloudinary;

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private ClubMembershipRepository clubMembershipRepository;

    @Autowired 
    private MediaRepository mediaRepository;

    @Autowired
    private UserRepository userRepository;

    // Helper Function
    private boolean canManageMedia(Long eventId, String requesterEmail, Role requesterRole) {
        if (requesterRole == Role.SUPERADMIN) {
            return true;
        }

        // Get the event and related club
        Event event = eventRepository.findById(eventId).orElse(null);
        if (event == null) return false;

        Club club = event.getClub();
        if (club == null) return false;

        // Club Admin of that club
        if (requesterRole == Role.CLUB_ADMIN && club.getAdminEmail().equalsIgnoreCase(requesterEmail)) {
            return true;
        }

        // Club Member
        User user = userRepository.findByEmail(requesterEmail).orElse(null);
        if (user != null) {
            return clubMembershipRepository.findByUserIdAndClubId(user.getId(), club.getId()).isPresent();
        }

        return false;
    }

    // Upload Media
    public ResponseEntity<ApiResponse> uploadMedia(MultipartFile file, Long eventId, String requesterEmail, Role requesterRole){
        Optional<Event> optionalEvent = eventRepository.findById(eventId);
        if (optionalEvent.isEmpty()) {
            return ResponseEntity.badRequest().body(new ApiResponse(false, "Event not found"));
        }

        Event event = optionalEvent.get();
        Club club = event.getClub();

        boolean isPastEvent = event.getDate().isBefore(java.time.LocalDate.now());
        if(!isPastEvent){
            return ResponseEntity.badRequest().body(new ApiResponse(false, "Media can only be added to past events"));
        }


        if (!canManageMedia(eventId, requesterEmail, requesterRole)) {
            return ResponseEntity.badRequest().body(new ApiResponse(false, "You are not allowed to upload media for this event"));
        }

        // Upload to Cloudinary
        try {
            Map uploadResult = cloudinary.uploader().upload(
                                    file.getBytes(),
                                    ObjectUtils.asMap("folder", "club_events/" + club.getName())
                                );

            String url = (String) uploadResult.get("url");
            String publicId = (String) uploadResult.get("public_id");

            Media media = new Media(url, publicId, requesterEmail, event);
            mediaRepository.save(media);

            return ResponseEntity.ok(new ApiResponse(true, "Media uploaded successfully", media));

        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse(false, "Failed to upload media: " + e.getMessage()));
        }
    }

    // Fetch Media For an Event
    public ResponseEntity<ApiResponse> getMediaByEvent(Long eventId) {
        List<Media> mediaList = mediaRepository.findByEventId(eventId);

        if (mediaList.isEmpty()) {
            return ResponseEntity.status(404).body(new ApiResponse(false, "No media found for this event"));
        }

        List<MediaResponseDTO> mediaDTOs = mediaList.stream()
            .map(MediaResponseDTO::new)
            .toList();

        return ResponseEntity.ok(new ApiResponse(true, "Media fetched successfully", mediaDTOs));
    }

    // Delete Media
    public ResponseEntity<ApiResponse> deleteMedia(Long mediaId, String requesterEmail, Role requesterRole) {
        Optional<Media> mediaOpt = mediaRepository.findById(mediaId);
        if (mediaOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse(false, "Media not found"));
        }

        Media media = mediaOpt.get();
        Event event = eventRepository.findById(media.getEvent().getId()).orElse(null);
        if (event == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponse(false, "Event not found for this media"));
        }

        if (!canManageMedia(event.getId(), requesterEmail, requesterRole)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ApiResponse(false, "You are not authorized to delete this media"));
        }

        try {
            // Delete from Cloudinary
            cloudinary.uploader().destroy(media.getPublicId(), ObjectUtils.emptyMap());

            // Delete from DB
            mediaRepository.delete(media);

            return ResponseEntity.ok(new ApiResponse(true, "Media deleted successfully"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse(false, "Error deleting media: " + e.getMessage()));
        }
    }

}
