package com.pharmacy.catalog.controller;

import com.pharmacy.catalog.dto.PrescriptionResponse;
import com.pharmacy.catalog.service.PrescriptionService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/catalog/prescriptions")
public class PrescriptionController {

    private final PrescriptionService prescriptionService;

    public PrescriptionController(PrescriptionService prescriptionService) {
        this.prescriptionService = prescriptionService;
    }

    @PostMapping("/upload")
    public ResponseEntity<PrescriptionResponse> upload(
            @RequestHeader("X-Auth-User") String email,
            @RequestBody Map<String, String> body) {
        Long customerId = Long.parseLong(body.get("customerId"));
        String imageUrl = body.get("imageUrl");
        return ResponseEntity.ok(prescriptionService.upload(customerId, email, imageUrl));
    }

    @GetMapping("/my")
    public ResponseEntity<List<PrescriptionResponse>> myPrescriptions(@RequestParam Long customerId) {
        return ResponseEntity.ok(prescriptionService.getByCustomer(customerId));
    }

    @GetMapping("/pending")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<PrescriptionResponse>> getPending() {
        return ResponseEntity.ok(prescriptionService.getPending());
    }

    @PutMapping("/{id}/approve")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PrescriptionResponse> approve(@PathVariable("id") Long id) {
        return ResponseEntity.ok(prescriptionService.approve(id));
    }

    @PutMapping("/{id}/reject")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PrescriptionResponse> reject(@PathVariable("id") Long id,
                                                        @RequestBody Map<String, String> body) {
        return ResponseEntity.ok(prescriptionService.reject(id, body.get("reason")));
    }
}
