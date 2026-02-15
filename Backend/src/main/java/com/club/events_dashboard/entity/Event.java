package com.club.events_dashboard.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalTime;


@Entity
@Table(name = "events")
public class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Event name cannot be empty")
    private String name;

    @NotBlank(message = "Description cannot be empty")
    private String description;

    @NotNull(message = "Event date cannot be null")
    @FutureOrPresent
    private LocalDate date;

    @NotBlank(message = "Venue cannot be empty")
    private String venue;

    @Column(length = 500)
    private String imageUrl;

    private String imagePublicId;

    @NotNull
    private LocalTime startTime;

    @NotNull
    private LocalTime endTime;  

    // @ManyToOne(fetch = FetchType.LAZY)
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "club_id", nullable = false)
    // @JsonIgnore
    private Club club;

    @Column(nullable = false)
    private double entryFee = 0.0;

    public Event() {}

    public Event(String name, String description, LocalDate date, String venue, Club club) {
        this.name = name;
        this.description = description;
        this.date = date;
        this.venue = venue;
        this.club = club;
    }

    // Getters and Setters
    public Long getId() { return id; }

    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }

    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }

    public void setDescription(String description) { this.description = description; }

    public LocalDate getDate() { return date; }

    public void setDate(LocalDate date) { this.date = date; }

    public String getVenue() { return venue; }

    public void setVenue(String venue) { this.venue = venue; }

    public String getImageUrl() { return imageUrl; }

    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public String getImagePublicId() { return imagePublicId; }

    public void setImagePublicId(String imagePublicId) { this.imagePublicId = imagePublicId; }

    public LocalTime getStartTime() { return startTime; }

    public void setStartTime(LocalTime startTime) { this.startTime = startTime; }

    public LocalTime getEndTime() { return endTime; }

    public void setEndTime(LocalTime endTime) { this.endTime = endTime; }

    public Club getClub() { return club; }
    
    public void setClub(Club club) { this.club = club; }

    public double getEntryFee(){ return entryFee;}

    public void setEntryFee(double entryFee){this.entryFee = entryFee;}
}
