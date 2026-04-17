package com.easybuy.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.easybuy.entity.EasybuyUser;
import com.easybuy.entity.Payment;

import com.easybuy.enums.PaymentStatus;
import com.easybuy.repository.PaymentRepository;

@Service
@Transactional
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final OrderService orderService;

    public PaymentService(PaymentRepository paymentRepository,
                          OrderService orderService) {
        this.paymentRepository = paymentRepository;
        this.orderService = orderService;
    }

    // Save payment with user snapshot
    public Payment savePayment(Payment payment) {

        if (payment.getStatus() == null) {
            payment.setStatus(PaymentStatus.PENDING);
        }

        // ✅ Get user details from OrderService
        EasybuyUser user = orderService.getUserById(payment.getUserId());
        if (user != null) {
            payment.setName(user.getName());
            payment.setMobile(user.getMobile());
            payment.setAddress(user.getAddress());
        }

        return paymentRepository.save(payment);
    }

    // Update after Razorpay verification
    public void updatePaymentStatus(String orderId,
                                    String paymentId,
                                    PaymentStatus status) {

        Payment payment = paymentRepository
                .findByOrderId(orderId)
                .orElseThrow(() ->
                        new RuntimeException("Payment not found"));

        payment.setPaymentId(paymentId);
        payment.setStatus(status);

        paymentRepository.save(payment);
    }

    public List<Payment> getAllPayments() {
        return paymentRepository.findAll();
    }

    public List<Payment> getPaymentsByUser(Long userId) {
        return paymentRepository.findByUserId(userId);
    }

    public long successCount() {
        return paymentRepository.countByStatus(PaymentStatus.SUCCESS);
    }

    public long failedCount() {
        return paymentRepository.countByStatus(PaymentStatus.FAILED);
    }

    public Double totalRevenue() {
        return paymentRepository.totalRevenue();
    }

    public void updateOrderStatusAfterPayment(String orderId) {
        paymentRepository.findByOrderId(orderId).ifPresent(payment -> {
            orderService.markOrderPaid(payment.getUserId());
        });
    }
}