package com.pharmacy.admin.service;

import com.pharmacy.admin.client.CatalogClient;
import com.pharmacy.admin.client.OrderClient;
import com.pharmacy.admin.dto.*;
import com.pharmacy.admin.entity.AuditLog;
import com.pharmacy.admin.repository.AuditLogRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Service
public class AdminService {

    private final CatalogClient catalogClient;
    private final OrderClient orderClient;
    private final AuditLogRepository auditLogRepo;

    public AdminService(CatalogClient catalogClient, OrderClient orderClient,
                        AuditLogRepository auditLogRepo) {
        this.catalogClient = catalogClient;
        this.orderClient = orderClient;
        this.auditLogRepo = auditLogRepo;
    }

    public DashboardResponse getDashboard() {
        long totalOrders = orderClient.getTotalOrderCount();
        long pendingRx = catalogClient.getPendingPrescriptions().size();
        long lowStock = catalogClient.getLowStockCount();

        String today = LocalDate.now().toString();
        String monthStart = LocalDate.now().withDayOfMonth(1).toString();
        Map<String, Object> revenueMap = orderClient.getRevenue(monthStart, today);
        BigDecimal revenue = revenueMap.get("revenue") instanceof BigDecimal bd
                ? bd : new BigDecimal(revenueMap.get("revenue").toString());

        return DashboardResponse.builder()
                .totalOrders(totalOrders)
                .pendingPrescriptions(pendingRx)
                .lowStockCount(lowStock)
                .monthlyRevenue(revenue)
                .build();
    }

    public MedicineResponse addMedicine(MedicineRequest req) {
        MedicineResponse r = catalogClient.addMedicine(req);
        audit("ADD_MEDICINE", "Medicine", String.valueOf(r.getId()), "Added: " + req.getName());
        return r;
    }

    public MedicineResponse updateMedicine(Long id, MedicineRequest req) {
        MedicineResponse r = catalogClient.updateMedicine(id, req);
        audit("UPDATE_MEDICINE", "Medicine", String.valueOf(id), "Updated: " + req.getName());
        return r;
    }

    public void deleteMedicine(Long id) {
        catalogClient.deleteMedicine(id);
        audit("DELETE_MEDICINE", "Medicine", String.valueOf(id), "Deleted medicine id=" + id);
    }

    public List<PrescriptionResponse> getPendingPrescriptions() {
        return catalogClient.getPendingPrescriptions();
    }

    public PrescriptionResponse approvePrescription(Long id) {
        PrescriptionResponse r = catalogClient.approvePrescription(id);
        audit("APPROVE_PRESCRIPTION", "Prescription", String.valueOf(id), "Approved");
        return r;
    }

    public PrescriptionResponse rejectPrescription(Long id, String reason) {
        PrescriptionResponse r = catalogClient.rejectPrescription(id, Map.of("reason", reason));
        audit("REJECT_PRESCRIPTION", "Prescription", String.valueOf(id), "Rejected: " + reason);
        return r;
    }

    public List<OrderResponse> getAllOrders() {
        return orderClient.getAllOrders();
    }

    public OrderResponse updateOrderStatus(Long id, String status) {
        OrderResponse r = orderClient.updateOrderStatus(id, Map.of("status", status));
        audit("UPDATE_ORDER_STATUS", "Order", String.valueOf(id), "Status -> " + status);
        return r;
    }

    private void audit(String action, String entity, String targetId, String details) {
        String admin = SecurityContextHolder.getContext().getAuthentication().getName();
        auditLogRepo.save(AuditLog.builder()
                .adminEmail(admin).action(action)
                .targetEntity(entity).targetId(targetId).details(details)
                .build());
    }
}
