package com.club.events_dashboard.repository;

import com.club.events_dashboard.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;


public interface PaymentRepository extends JpaRepository<Payment, Long> {
    Optional<Payment> findByRazorpayOrderId(String orderId);
}