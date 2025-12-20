package com.club.events_dashboard.entity;

import jakarta.persistence.*;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "media")
public class Media {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String url;
    private String publicId;
    private String uploadedBy;
    private LocalDate uploadedDate = LocalDate.now();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_id", nullable = false)
    @JsonIgnore // prevents proxy serialization and recursion
    private Event event;

    // Constructors
    public Media() {}

    public Media(String url, String publicId, String uploadedBy, Event event) {
        this.url = url;
        this.publicId = publicId;
        this.uploadedBy = uploadedBy;
        this.event = event;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public String getUrl() { return url; }
    public void setUrl(String url) { this.url = url; }

    public String getPublicId() { return publicId; }
    public void setPublicId(String publicId) { this.publicId = publicId; }

    public String getUploadedBy() { return uploadedBy; }
    public void setUploadedBy(String uploadedBy) { this.uploadedBy = uploadedBy; }

    public LocalDate getUploadedDate() { return uploadedDate; }
    public void setUploadedDate(LocalDate uploadedDate) { this.uploadedDate = uploadedDate; }

    public Event getEvent() { return event; }
    public void setEvent(Event event) { this.event = event; }
}
