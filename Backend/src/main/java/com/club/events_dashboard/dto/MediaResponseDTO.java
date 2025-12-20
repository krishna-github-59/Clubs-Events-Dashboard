package com.club.events_dashboard.dto;

import java.time.LocalDate;

import com.club.events_dashboard.entity.Media;

public class MediaResponseDTO {
    private Long id;
    private String url;
    private String publicId;
    private String uploadedBy;
    private LocalDate uploadedDate;
    private Long eventId;

    public MediaResponseDTO() {}

    public MediaResponseDTO(Media media) {
        this.id = media.getId();
        this.url = media.getUrl();
        this.publicId = media.getPublicId();
        this.uploadedBy = media.getUploadedBy();
        this.uploadedDate = media.getUploadedDate();
        this.eventId = media.getEvent() != null ? media.getEvent().getId() : null;
    }

    // Getters
    public Long getId() { return id; }
    public String getUrl() { return url; }
    public String getPublicId() { return publicId; }
    public String getUploadedBy() { return uploadedBy; }
    public LocalDate getUploadedDate() { return uploadedDate; }
    public Long getEventId() { return eventId; }
}
