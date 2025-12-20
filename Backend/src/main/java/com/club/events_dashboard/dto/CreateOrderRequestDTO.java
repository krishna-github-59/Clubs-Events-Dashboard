package com.club.events_dashboard.dto;

public class CreateOrderRequestDTO {
    private Long eventId;
    private Long amountRupees; // required if eventId is null or event has no fee


    public CreateOrderRequestDTO() {}
    
    public Long getEventId() { return eventId; }
    public void setEventId(Long eventId) { this.eventId = eventId; }
    public Long getAmountRupees() { return amountRupees; }
    public void setAmountRupees(Long amountRupees) { this.amountRupees = amountRupees; }
}
