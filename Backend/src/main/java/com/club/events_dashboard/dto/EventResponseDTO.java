package com.club.events_dashboard.dto;

import java.time.LocalDate;
import java.time.LocalTime;

public class EventResponseDTO {
    private Long id;
    private String name;
    private String description;
    private LocalDate date;
    private String venue;
    private Long clubId;
    private String clubName;
    private Double entryFee;
    
    private String imageUrl;      
    private LocalTime startTime;  
    private LocalTime endTime;    

    private boolean canEdit;
    private boolean canDelete;

    private boolean canAddMedia;
    private boolean canViewMedia;

    public EventResponseDTO() {}

    public EventResponseDTO(Long id, String name, String description, LocalDate date, String venue, Long clubId, String clubName, Double entryFee) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.date = date;
        this.venue = venue;
        this.clubId = clubId;
        this.clubName = clubName;
        this.entryFee = entryFee;
    }

    public EventResponseDTO(Long id, String name, String description, LocalDate date, String venue, Long clubId, String clubName, Double entryFee, String imageUrl, LocalTime startTime, LocalTime endTime) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.date = date;
        this.venue = venue;
        this.clubId = clubId;
        this.clubName = clubName;
        this.entryFee = entryFee;
        this.imageUrl = imageUrl;
        this.startTime = startTime;
        this.endTime = endTime;
    }

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

    public Long getClubId() { return clubId; }
    public void setClubId(Long clubId) { this.clubId = clubId; }

    public String getClubName() { return clubName; }
    public void setClubName(String clubName) { this.clubName = clubName; }

    public Double getEntryFee() { return entryFee; }
    public void setEntryFee(Double entryFee) { this.entryFee = entryFee; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public LocalTime getStartTime() { return startTime; }
    public void setStartTime(LocalTime startTime) { this.startTime = startTime; }

    public LocalTime getEndTime() { return endTime; }
    public void setEndTime(LocalTime endTime) { this.endTime = endTime; }

    public boolean getCanEdit() { return canEdit; }
    public void setCanEdit(boolean canEdit) { this.canEdit = canEdit; }

    public boolean getCanDelete() { return canDelete; }
    public void setCanDelete(boolean canDelete) { this.canDelete = canDelete; }

    public boolean getCanAddMedia() { return canAddMedia; }
    public void setCanAddMedia(boolean canAddMedia) { this.canAddMedia = canAddMedia; }

    public boolean getCanViewMedia() { return canViewMedia; }
    public void setCanViewMedia(boolean canViewMedia) { this.canViewMedia = canViewMedia; }
}
