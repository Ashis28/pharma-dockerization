package com.pharmacy.payment.controller;

import com.pharmacy.payment.dto.PaymentRequest;
import com.pharmacy.payment.dto.PaymentResponse;
import com.pharmacy.payment.service.PaymentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping("/process")
    public ResponseEntity<PaymentResponse> process(@RequestBody PaymentRequest req) {
        return ResponseEntity.ok(paymentService.processPayment(req));
    }

    @GetMapping("/order/{orderId}")
    public ResponseEntity<PaymentResponse> getByOrder(@PathVariable("orderId") Long orderId) {
        return ResponseEntity.ok(paymentService.getByOrderId(orderId));
    }
}
