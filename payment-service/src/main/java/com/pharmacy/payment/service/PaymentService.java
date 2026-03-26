package com.pharmacy.payment.service;

import com.pharmacy.payment.dto.PaymentRequest;
import com.pharmacy.payment.dto.PaymentResponse;
import com.pharmacy.payment.entity.Payment;
import com.pharmacy.payment.exception.PaymentNotFoundException;
import com.pharmacy.payment.repository.PaymentRepository;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class PaymentService {

    private final PaymentRepository paymentRepo;

    public PaymentService(PaymentRepository paymentRepo) {
        this.paymentRepo = paymentRepo;
    }

    public PaymentResponse processPayment(PaymentRequest req) {
        Payment payment = new Payment();
        payment.setOrderId(req.getOrderId());
        payment.setCustomerId(req.getCustomerId());
        payment.setAmount(req.getAmount());
        payment.setPaymentMethod(req.getPaymentMethod());
        payment.setStatus(Payment.PaymentStatus.SUCCESS);
        payment.setTransactionId("TXN-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        Payment saved = paymentRepo.save(payment);
        return toResponse(saved);
    }

    public PaymentResponse getByOrderId(Long orderId) {
        return paymentRepo.findByOrderId(orderId).stream()
                .findFirst().map(this::toResponse)
                .orElseThrow(() -> new PaymentNotFoundException("Payment not found for order: " + orderId));
    }

    private PaymentResponse toResponse(Payment p) {
        PaymentResponse r = new PaymentResponse();
        r.setPaymentId(p.getId());
        r.setOrderId(p.getOrderId());
        r.setAmount(p.getAmount());
        r.setStatus(p.getStatus().name());
        r.setTransactionId(p.getTransactionId());
        r.setCreatedAt(p.getCreatedAt());
        return r;
    }
}
