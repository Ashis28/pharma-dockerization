package com.pharmacy.admin.client;

import com.pharmacy.admin.dto.MedicineRequest;
import com.pharmacy.admin.dto.MedicineResponse;
import com.pharmacy.admin.dto.PrescriptionResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@FeignClient(name = "catalog-service")
public interface CatalogClient {

    @PostMapping("/api/catalog/medicines")
    MedicineResponse addMedicine(@RequestBody MedicineRequest request);

    @PutMapping("/api/catalog/medicines/{id}")
    MedicineResponse updateMedicine(@PathVariable("id") Long id, @RequestBody MedicineRequest request);

    @DeleteMapping("/api/catalog/medicines/{id}")
    void deleteMedicine(@PathVariable("id") Long id);

    @GetMapping("/api/catalog/prescriptions/pending")
    List<PrescriptionResponse> getPendingPrescriptions();

    @PutMapping("/api/catalog/prescriptions/{id}/approve")
    PrescriptionResponse approvePrescription(@PathVariable("id") Long id);

    @PutMapping("/api/catalog/prescriptions/{id}/reject")
    PrescriptionResponse rejectPrescription(@PathVariable("id") Long id, @RequestBody Map<String, String> body);

    @GetMapping("/api/catalog/medicines/low-stock-count")
    long getLowStockCount();
}
