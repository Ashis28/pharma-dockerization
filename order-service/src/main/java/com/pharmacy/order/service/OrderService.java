package com.pharmacy.order.service;

import com.pharmacy.order.client.CatalogClient;
import com.pharmacy.order.client.PaymentClient;
import com.pharmacy.order.messaging.NotificationPublisher;
import com.pharmacy.order.dto.*;
import com.pharmacy.order.entity.Order;
import com.pharmacy.order.entity.OrderItem;
import com.pharmacy.order.exception.InvalidOrderStatusException;
import com.pharmacy.order.exception.OrderNotFoundException;
import com.pharmacy.order.repository.OrderRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class OrderService {

    private final OrderRepository orderRepo;
    private final CatalogClient catalogClient;
    private final PaymentClient paymentClient;
    private final NotificationPublisher notificationPublisher;

    public OrderService(OrderRepository orderRepo, CatalogClient catalogClient,
                        PaymentClient paymentClient, NotificationPublisher notificationPublisher) {
        this.orderRepo = orderRepo;
        this.catalogClient = catalogClient;
        this.paymentClient = paymentClient;
        this.notificationPublisher = notificationPublisher;
    }

    @Transactional
    public OrderResponse placeOrder(OrderRequest req, String customerEmail) {
        Order order = new Order();
        order.setCustomerId(req.getCustomerId());
        order.setCustomerEmail(customerEmail);
        order.setDeliveryAddress(req.getDeliveryAddress());

        BigDecimal total = BigDecimal.ZERO;
        for (OrderRequest.OrderItemRequest itemReq : req.getItems()) {
            MedicineDto medicine = catalogClient.getMedicineById(itemReq.getMedicineId());
            OrderItem item = new OrderItem();
            item.setOrder(order);
            item.setMedicineId(medicine.getId());
            item.setMedicineName(medicine.getName());
            item.setQuantity(itemReq.getQuantity());
            item.setUnitPrice(medicine.getPrice());
            order.getItems().add(item);
            total = total.add(medicine.getPrice().multiply(BigDecimal.valueOf(itemReq.getQuantity())));
        }
        order.setTotalAmount(total);
        Order saved = orderRepo.save(order);

        try {
            notificationPublisher.publish(new NotificationRequest(
                    customerEmail,
                    "Order Placed - #" + saved.getId(),
                    "Your order #" + saved.getId() + " has been placed. Total: ₹" + saved.getTotalAmount(),
                    "ORDER_PLACED"
            ));
        } catch (Exception ignored) { /* non-critical */ }

        return toResponse(saved);
    }

    public OrderResponse getById(Long id) {
        return toResponse(findOrThrow(id));
    }

    public List<OrderResponse> getByCustomer(Long customerId) {
        return orderRepo.findByCustomerId(customerId).stream()
                .map(this::toResponse).collect(Collectors.toList());
    }

    public List<OrderResponse> getAll() {
        return orderRepo.findAll().stream().map(this::toResponse).collect(Collectors.toList());
    }

    public OrderResponse updateStatus(Long id, String status) {
        Order order = findOrThrow(id);
        try {
            order.setStatus(Order.OrderStatus.valueOf(status.toUpperCase()));
        } catch (IllegalArgumentException e) {
            throw new InvalidOrderStatusException("Invalid order status: " + status +
                    ". Valid values: PENDING, PAID, PACKED, SHIPPED, DELIVERED, CANCELLED");
        }
        return toResponse(orderRepo.save(order));
    }

    public long getCount() {
        return orderRepo.count();
    }

    public BigDecimal getRevenueBetween(String from, String to) {
        LocalDateTime fromDt = LocalDate.parse(from).atStartOfDay();
        LocalDateTime toDt = LocalDate.parse(to).atTime(23, 59, 59);
        return orderRepo.sumRevenueBetween(fromDt, toDt);
    }

    private Order findOrThrow(Long id) {
        return orderRepo.findById(id).orElseThrow(() -> new OrderNotFoundException("Order not found with id: " + id));
    }

    private OrderResponse toResponse(Order o) {
        OrderResponse r = new OrderResponse();
        r.setId(o.getId());
        r.setCustomerId(o.getCustomerId());
        r.setCustomerEmail(o.getCustomerEmail());
        r.setStatus(o.getStatus().name());
        r.setTotalAmount(o.getTotalAmount());
        r.setDeliveryAddress(o.getDeliveryAddress());
        r.setCreatedAt(o.getCreatedAt());
        r.setItems(o.getItems().stream().map(i -> {
            OrderResponse.ItemDto d = new OrderResponse.ItemDto();
            d.setMedicineId(i.getMedicineId());
            d.setMedicineName(i.getMedicineName());
            d.setQuantity(i.getQuantity());
            d.setUnitPrice(i.getUnitPrice());
            return d;
        }).collect(Collectors.toList()));
        return r;
    }
}
