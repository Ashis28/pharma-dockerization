package com.pharmacy.catalog.controller;

import com.pharmacy.catalog.dto.MedicineRequest;
import com.pharmacy.catalog.dto.MedicineResponse;
import com.pharmacy.catalog.service.MedicineService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/catalog/medicines")
public class MedicineController {

    private final MedicineService medicineService;

    public MedicineController(MedicineService medicineService) {
        this.medicineService = medicineService;
    }

    @GetMapping
    public ResponseEntity<List<MedicineResponse>> getAll() {
        return ResponseEntity.ok(medicineService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<MedicineResponse> getById(@PathVariable("id") Long id) {
        return ResponseEntity.ok(medicineService.getById(id));
    }

    @GetMapping("/search")
    public ResponseEntity<List<MedicineResponse>> search(@RequestParam String name) {
        return ResponseEntity.ok(medicineService.search(name));
    }

    @GetMapping("/low-stock-count")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Long> getLowStockCount() {
        return ResponseEntity.ok(medicineService.getLowStockCount());
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<MedicineResponse> create(@Valid @RequestBody MedicineRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(medicineService.create(req));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<MedicineResponse> update(@PathVariable Long id,
                                                    @Valid @RequestBody MedicineRequest req) {
        return ResponseEntity.ok(medicineService.update(id, req));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable("id") Long id) {
        medicineService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
