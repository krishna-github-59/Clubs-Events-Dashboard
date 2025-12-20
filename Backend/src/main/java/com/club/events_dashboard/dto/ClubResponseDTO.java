package com.club.events_dashboard.dto;

import java.util.List;

import com.club.events_dashboard.entity.Club;

public class ClubResponseDTO {
    private Long id;
    private String name;
    private String description;
    private String adminEmail;
    private String createdBy;
    private int eventCount;

    public ClubResponseDTO() {}

    public ClubResponseDTO(Club club) {
        this.id = club.getId();
        this.name = club.getName();
        this.description = club.getDescription();
        this.createdBy = club.getCreatedBy();
        this.adminEmail = club.getAdminEmail();
        this.eventCount = club.getEvents() != null ? club.getEvents().size() : 0;
    }


    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getAdminEmail() { return adminEmail; }
    public void setAdminEmail(String adminEmail) { this.adminEmail = adminEmail; }

    public String getCreatedBy() { return createdBy; }
    public void setCreatedBy(String createdBy) { this.createdBy = createdBy; }

    public int getEventCount() { return eventCount; }
    public void setEvents(int eventCount) { this.eventCount = eventCount; }
}
