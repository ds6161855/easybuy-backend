package com.easybuy.controller;

import java.util.Map;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Hex;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.easybuy.entity.Payment;
import com.easybuy.enums.PaymentStatus;
import com.easybuy.security.RazorpayService;
import com.easybuy.service.PaymentService;

@RestController
@RequestMapping("/api/payments")
@CrossOrigin(origins = "https://easybuy-backend-xadk.onrender.com")
public class PaymentController {

    private final RazorpayService razorpayService;
    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService,
                             RazorpayService razorpayService) {
        this.paymentService = paymentService;
        this.razorpayService = razorpayService;
    }

    // CREATE RAZORPAY ORDER
    @PostMapping("/razorpay/order")
    public ResponseEntity<?> createRazorpayOrder(@RequestBody Map<String, Object> data) {

        try {
        	Long userId = 0L;

        	if (data.containsKey("userId") && data.get("userId") != null) {
        	    userId = Long.valueOf(data.get("userId").toString());
        	}

            if (!data.containsKey("amount") || data.get("amount") == null) {
                return ResponseEntity.badRequest().body("Amount required");
            }

            int amount = ((Number) data.get("amount")).intValue() * 100; // Razorpay paisa

            // Create Razorpay order
            Map<String, Object> response = razorpayService.createOrder(amount);

            // Build Payment entity
            Payment payment = Payment.builder()
                    .orderId((String) response.get("id"))
                    .amount(amount / 100.0)
                    .userId(userId)
                    .status(PaymentStatus.PENDING)
                    .build();

            // ✅ Save payment along with user snapshot (handled in PaymentService)
            paymentService.savePayment(payment);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    // VERIFY PAYMENT
    @PostMapping("/verify")
    public ResponseEntity<String> verifyPayment(@RequestBody Map<String, String> data) {

        try {
            String orderId = data.get("razorpay_order_id");
            String paymentId = data.get("razorpay_payment_id");
            String signature = data.get("razorpay_signature");

            if (orderId == null || paymentId == null || signature == null) {
                return ResponseEntity.badRequest().body("Invalid payment data");
            }

            String payload = orderId + "|" + paymentId;

            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(razorpayService.getRazorpaySecret().getBytes(), "HmacSHA256"));

            String generatedSignature = Hex.encodeHexString(mac.doFinal(payload.getBytes()));

            boolean isValid = generatedSignature.equals(signature);

            if (isValid) {
                // ✅ Mark payment SUCCESS
                paymentService.updatePaymentStatus(orderId, paymentId, PaymentStatus.SUCCESS);
                paymentService.updateOrderStatusAfterPayment(orderId);
                return ResponseEntity.ok("Payment verified");
            }

            // ✅ Mark payment FAILED
            paymentService.updatePaymentStatus(orderId, paymentId, PaymentStatus.FAILED);
            return ResponseEntity.badRequest().body("Verification failed");

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }
}
