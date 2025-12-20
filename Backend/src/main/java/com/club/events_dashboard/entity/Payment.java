package com.club.events_dashboard.entity;
import java.time.LocalDate;

import jakarta.persistence.*;


@Entity
@Table(name = "payments")
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String razorpayOrderId;
    private String razorpayPaymentId;
    private String razorpaySignature;

    private Double amount;
    
    @Enumerated(EnumType.STRING)
    private PaymentStatus status;

    private String userEmail;

    @ManyToOne
    @JoinColumn(name = "event_id")
    private Event event;

    private LocalDate createdDate = LocalDate.now();


    // Constructors
    public Payment() {}


    // Getters & Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getRazorpayOrderId() { return razorpayOrderId; }
    public void setRazorpayOrderId(String razorpayOrderId) { this.razorpayOrderId = razorpayOrderId; }

    public String getRazorpayPaymentId() { return razorpayPaymentId; }
    public void setRazorpayPaymentId(String razorpayPaymentId) { this.razorpayPaymentId = razorpayPaymentId; }

    public String getRazorpaySignature() { return razorpaySignature; }
    public void setRazorpaySignature(String razorpaySignature) { this.razorpaySignature = razorpaySignature; }

    public Double getAmount() { return amount; }
    public void setAmount(Double amount) { this.amount = amount; }

    public PaymentStatus getStatus() { return status; }
    public void setStatus(PaymentStatus status) { this.status = status; }

    public String getUserEmail() { return userEmail; }
    public void setUserEmail(String userEmail) { this.userEmail = userEmail; }

    public Event getEvent() { return event; }
    public void setEvent(Event event) { this.event = event; }

    public LocalDate getCreatedDate() { return createdDate; }
    public void setCreatedDate(LocalDate createdDate) { this.createdDate = createdDate; }
}