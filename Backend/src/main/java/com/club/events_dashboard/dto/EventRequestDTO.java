package com.club.events_dashboard.dto;

import java.time.LocalDate;
import java.time.LocalTime;

public class EventRequestDTO {
    private String name;
    private String description;
    private LocalDate date;
    private String venue;
    private Long clubId;
    private Double entryFee;
    private LocalTime startTime;
    private LocalTime endTime;  

    public EventRequestDTO() {}

    public EventRequestDTO(String name, String description, LocalDate date, String venue, Long clubId, Double entryFee, LocalTime startTime, LocalTime endTime) {
        this.name = name;
        this.description = description;
        this.date = date;
        this.venue = venue;
        this.clubId = clubId;
        this.entryFee = entryFee;
        this.startTime = startTime;
        this.endTime = endTime;
    }

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

    public Double getEntryFee() { return entryFee; }
    public void setEntryFee(Double entryFee) { this.entryFee = entryFee; }

    public LocalTime getStartTime() { return startTime; }
    public void setStartTime(LocalTime startTime) { this.startTime = startTime; }

    public LocalTime getEndTime() { return endTime; }
    public void setEndTime(LocalTime endTime) { this.endTime = endTime; }
}
