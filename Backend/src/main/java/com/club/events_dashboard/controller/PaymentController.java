package com.club.events_dashboard.controller;

import com.club.events_dashboard.dto.ApiResponse;
import com.club.events_dashboard.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    @Autowired
    private PaymentService paymentService;

    // 🎯 Create order for event
    @PostMapping("/create-order/{eventId}")
    public ResponseEntity<ApiResponse> createOrderForEvent(@PathVariable Long eventId, @RequestParam String userEmail) {
        return paymentService.createOrderForEvent(eventId, userEmail);
    }

    // ✅ Verify payment
    @PostMapping("/verify")
    public ResponseEntity<ApiResponse> verifyPaymentSignature(@RequestBody Map<String, String> paymentData) {
        return paymentService.verifyPaymentSignature(paymentData);
    }
}
