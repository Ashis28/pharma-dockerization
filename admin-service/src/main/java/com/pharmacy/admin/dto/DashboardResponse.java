package com.pharmacy.admin.dto;

import java.math.BigDecimal;

public class DashboardResponse {
    private long totalOrders;
    private long pendingPrescriptions;
    private long lowStockCount;
    private BigDecimal monthlyRevenue;

    public DashboardResponse() {}

    public DashboardResponse(long totalOrders, long pendingPrescriptions,
                              long lowStockCount, BigDecimal monthlyRevenue) {
        this.totalOrders = totalOrders;
        this.pendingPrescriptions = pendingPrescriptions;
        this.lowStockCount = lowStockCount;
        this.monthlyRevenue = monthlyRevenue;
    }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private long totalOrders;
        private long pendingPrescriptions;
        private long lowStockCount;
        private BigDecimal monthlyRevenue;

        public Builder totalOrders(long totalOrders) { this.totalOrders = totalOrders; return this; }
        public Builder pendingPrescriptions(long pendingPrescriptions) { this.pendingPrescriptions = pendingPrescriptions; return this; }
        public Builder lowStockCount(long lowStockCount) { this.lowStockCount = lowStockCount; return this; }
        public Builder monthlyRevenue(BigDecimal monthlyRevenue) { this.monthlyRevenue = monthlyRevenue; return this; }
        public DashboardResponse build() {
            return new DashboardResponse(totalOrders, pendingPrescriptions, lowStockCount, monthlyRevenue);
        }
    }

    public long getTotalOrders() { return totalOrders; }
    public void setTotalOrders(long totalOrders) { this.totalOrders = totalOrders; }
    public long getPendingPrescriptions() { return pendingPrescriptions; }
    public void setPendingPrescriptions(long pendingPrescriptions) { this.pendingPrescriptions = pendingPrescriptions; }
    public long getLowStockCount() { return lowStockCount; }
    public void setLowStockCount(long lowStockCount) { this.lowStockCount = lowStockCount; }
    public BigDecimal getMonthlyRevenue() { return monthlyRevenue; }
    public void setMonthlyRevenue(BigDecimal monthlyRevenue) { this.monthlyRevenue = monthlyRevenue; }
}
