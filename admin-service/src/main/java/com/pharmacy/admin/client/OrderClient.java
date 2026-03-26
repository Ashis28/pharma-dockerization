package com.pharmacy.admin.client;

import com.pharmacy.admin.dto.OrderResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@FeignClient(name = "order-service")
public interface OrderClient {

    @GetMapping("/api/orders")
    List<OrderResponse> getAllOrders();

    @PutMapping("/api/orders/{id}/status")
    OrderResponse updateOrderStatus(@PathVariable("id") Long id, @RequestBody Map<String, String> body);

    @GetMapping("/api/orders/count")
    long getTotalOrderCount();

    @GetMapping("/api/orders/revenue")
    Map<String, Object> getRevenue(@RequestParam("from") String from, @RequestParam("to") String to);
}
