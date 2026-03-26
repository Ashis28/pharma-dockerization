package com.pharmacy.order.client;

import com.pharmacy.order.dto.MedicineDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "catalog-service")
public interface CatalogClient {

    @GetMapping("/api/catalog/medicines/{id}")
    MedicineDto getMedicineById(@PathVariable("id") Long id);
}
