package com.pharmacy.admin.controller;

import com.pharmacy.admin.dto.*;
import com.pharmacy.admin.service.AdminService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final AdminService adminService;

    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    @GetMapping("/dashboard")
    public ResponseEntity<DashboardResponse> getDashboard() {
        return ResponseEntity.ok(adminService.getDashboard());
    }

    @PostMapping("/medicines")
    public ResponseEntity<MedicineResponse> addMedicine(@Valid @RequestBody MedicineRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(adminService.addMedicine(req));
    }

    @PutMapping("/medicines/{id}")
    public ResponseEntity<MedicineResponse> updateMedicine(@PathVariable Long id,
                                                            @Valid @RequestBody MedicineRequest req) {
        return ResponseEntity.ok(adminService.updateMedicine(id, req));
    }

    @DeleteMapping("/medicines/{id}")
    public ResponseEntity<Void> deleteMedicine(@PathVariable Long id) {
        adminService.deleteMedicine(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/prescriptions/pending")
    public ResponseEntity<List<PrescriptionResponse>> getPendingPrescriptions() {
        return ResponseEntity.ok(adminService.getPendingPrescriptions());
    }

    @PutMapping("/prescriptions/{id}/approve")
    public ResponseEntity<PrescriptionResponse> approvePrescription(@PathVariable Long id) {
        return ResponseEntity.ok(adminService.approvePrescription(id));
    }

    @PutMapping("/prescriptions/{id}/reject")
    public ResponseEntity<PrescriptionResponse> rejectPrescription(@PathVariable Long id,
                                                                     @RequestBody Map<String, String> body) {
        return ResponseEntity.ok(adminService.rejectPrescription(id, body.get("reason")));
    }

    @GetMapping("/orders")
    public ResponseEntity<List<OrderResponse>> getAllOrders() {
        return ResponseEntity.ok(adminService.getAllOrders());
    }

    @PutMapping("/orders/{id}/status")
    public ResponseEntity<OrderResponse> updateOrderStatus(@PathVariable Long id,
                                                            @RequestBody Map<String, String> body) {
        return ResponseEntity.ok(adminService.updateOrderStatus(id, body.get("status")));
    }
}
