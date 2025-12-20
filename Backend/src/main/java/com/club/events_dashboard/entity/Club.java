package com.club.events_dashboard.entity;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;

@Entity
@Table(name = "clubs")
public class Club {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Club name cannot be blank")
    private String name;

    private String description;

    @Column(name = "created_by")
    private String createdBy; // email of SuperAdmin

    @Column(name = "admin_email", nullable = false)
    private String adminEmail; // email of admin

    @OneToMany(mappedBy = "club", cascade = CascadeType.ALL, orphanRemoval = true)
    // @JsonManagedReference
    @JsonIgnore
    private List<Event> events;

    public Club() {}

    public Club(String name, String description, String createdBy, String adminEmail) {
        this.name = name;
        this.description = description;
        this.createdBy = createdBy;
        this.adminEmail = adminEmail;
    }

    // Getters and setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getCreatedBy() { return createdBy; }
    public void setCreatedBy(String createdBy) { this.createdBy = createdBy; }

    public String getAdminEmail(){ return adminEmail;}
    public void setAdminEmail(String adminEmail){ this.adminEmail = adminEmail;}

    public List<Event> getEvents() { return events; }
    public void setEvents(List<Event> events) { this.events = events; }
}
