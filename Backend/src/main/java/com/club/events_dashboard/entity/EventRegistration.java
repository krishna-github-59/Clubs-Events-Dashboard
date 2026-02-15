package com.club.events_dashboard.entity;

import java.util.Date;
import jakarta.persistence.*;

@Entity
@Table(
    name = "event_registrations",
    uniqueConstraints = @UniqueConstraint(columnNames = {"event_id", "user_id"
    // , "guest_id"
        })
)
public class EventRegistration {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "event_id", nullable = false)
    private Event event;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = true)
    private User user;     // student/admin

    // @ManyToOne
    // @JoinColumn(name = "guest_id", nullable = true)
    // private Guest guest;

    private Date registeredDate = new Date();

    // getters & setters
    public Long getId(){return id;}
    public void setId(Long id){this.id = id;}

    public Event getEvent(){return event;}
    public void setEvent(Event event){this.event = event;}

    public User getUser(){return user;}
    public void setUser(User user){this.user = user;}

    // public Guest getGuest(){return guest;}
    // public void setGuest(Guest guest){this.guest = guest;}

    public Date getRegisteredDate(){return registeredDate;}
    public void setRegisteredDate(Date registeredDate){this.registeredDate = registeredDate;}
}
