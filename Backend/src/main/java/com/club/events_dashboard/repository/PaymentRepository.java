package com.club.events_dashboard.repository;

import com.club.events_dashboard.constants.PaymentStatus;
import com.club.events_dashboard.entity.Payment;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {
    Optional<Payment> findByRazorpayOrderId(String orderId);
    Boolean existsByEventIdAndUserEmailAndStatus(Long eventId, String userEmail, PaymentStatus status);
}