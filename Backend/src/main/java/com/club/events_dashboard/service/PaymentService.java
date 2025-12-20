package com.club.events_dashboard.service;

import com.club.events_dashboard.dto.ApiResponse;
import com.club.events_dashboard.entity.Event;
import com.club.events_dashboard.entity.Payment;
import com.club.events_dashboard.entity.PaymentStatus;
import com.club.events_dashboard.repository.EventRepository;
import com.club.events_dashboard.repository.PaymentRepository;
import com.razorpay.Order;
import com.razorpay.RazorpayClient;
import com.razorpay.Utils;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;

@Service
public class PaymentService {

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private PaymentRepository paymentRepository;

    private final String keyId = System.getenv("RAZORPAY_KEY_ID");
    private final String keySecret = System.getenv("RAZORPAY_KEY_SECRET");

    // Create Razorpay order for event
    public ResponseEntity<ApiResponse> createOrderForEvent(Long eventId, String userEmail) {
        try {
            Optional<Event> eventOpt = eventRepository.findById(eventId);

            if (eventOpt.isEmpty()) {
                return ResponseEntity.badRequest().body(new ApiResponse(false, "Event not found", null));
            }

            Event event = eventOpt.get();

            if (event.getEntryFee() <= 0) {
                return ResponseEntity.ok(new ApiResponse(true, "This event is free. No payment required.", null));
            }

            RazorpayClient razorpay = new RazorpayClient(keyId, keySecret);

            JSONObject orderRequest = new JSONObject();
            orderRequest.put("amount", (int) (event.getEntryFee() * 100)); // amount in paise
            orderRequest.put("currency", "INR");
            orderRequest.put("payment_capture", 1);

            Order order = razorpay.orders.create(orderRequest);

            Payment payment = new Payment();
            payment.setEvent(event);
            payment.setRazorpayOrderId(order.get("id"));
            payment.setAmount(event.getEntryFee());
            payment.setUserEmail(userEmail);
            payment.setStatus(PaymentStatus.PENDING);

            paymentRepository.save(payment);
            return ResponseEntity.ok(
                new ApiResponse(true, "Order created successfully", order.toString())
            );

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body(new ApiResponse(false, "Failed to create Razorpay order: " + e.getMessage(), null));
        }
    }

    // Verify Razorpay payment signature
    public ResponseEntity<ApiResponse> verifyPaymentSignature(Map<String, String> paymentData) {
        try {
            String orderId = paymentData.get("razorpay_order_id");
            Optional<Payment> paymentOpt = paymentRepository.findByRazorpayOrderId(orderId);

            if (paymentOpt == null) {
                return ResponseEntity.badRequest().body(new ApiResponse(false, "Payment record not found for order ID", null));
            }

            Payment payment = paymentOpt.get();

            JSONObject attributes = new JSONObject();
            attributes.put("razorpay_order_id", paymentData.get("razorpay_order_id"));
            attributes.put("razorpay_payment_id", paymentData.get("razorpay_payment_id"));
            attributes.put("razorpay_signature", paymentData.get("razorpay_signature"));

            boolean isValid = Utils.verifyPaymentSignature(attributes, keySecret);

            if (isValid) {
                payment.setRazorpayPaymentId(paymentData.get("razorpay_payment_id"));
                payment.setRazorpaySignature(paymentData.get("razorpay_signature"));
                payment.setStatus(PaymentStatus.SUCCESS);
                paymentRepository.save(payment);

                return ResponseEntity.ok().body(
                    new ApiResponse(true, "Payment verified and saved successfully", payment)
                );
            } else {
                payment.setStatus(PaymentStatus.FAILED);
                paymentRepository.save(payment);

                return ResponseEntity.badRequest().body(new ApiResponse(false, "Invalid payment signature", null));
            }

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body(new ApiResponse(false, "Payment verification failed: " + e.getMessage(), null));
        }
    }
}
