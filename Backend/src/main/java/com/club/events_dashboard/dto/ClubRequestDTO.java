package com.club.events_dashboard.dto;

public class ClubRequestDTO {
    private String name;
    private String description;
    private String adminEmail;

    public ClubRequestDTO() {}

    public ClubRequestDTO(String name, String description, String adminEmail) {
        this.name = name;
        this.description = description;
        this.adminEmail = adminEmail;
    }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getAdminEmail() { return adminEmail; }
    public void setAdminEmail(String adminEmail) { this.adminEmail = adminEmail; }
}
