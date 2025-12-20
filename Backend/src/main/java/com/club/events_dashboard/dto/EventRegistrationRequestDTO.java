package com.club.events_dashboard.dto;

public class EventRegistrationRequestDTO {
    private Long eventId;
    private Long userId;  // nullable
    private Long guestId; // nullable

    // getters & setters
    public Long getEventId() { return eventId; }
    public void setEventId(Long eventId) { this.eventId = eventId; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public Long getGuestId() { return guestId; }
    public void setGuestId(Long guestId) { this.guestId = guestId; }
}

