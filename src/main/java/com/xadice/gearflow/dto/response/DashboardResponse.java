package com.xadice.gearflow.dto.response;

import java.math.BigDecimal;

public class DashboardResponse {

    private long totalUsers;
    private long activeUsers;

    private long activeEquipment;

    private long totalBookings;
    private long pendingBookings;
    private long approvedBookings;
    private long completedBookings;
    private long cancelledBookings;
    private long rejectedBookings;

    private BigDecimal totalRevenue;

    public DashboardResponse() {
    }

    public DashboardResponse(
            long totalUsers,
            long activeUsers,
            long activeEquipment,
            long totalBookings,
            long pendingBookings,
            long approvedBookings,
            long completedBookings,
            long cancelledBookings,
            long rejectedBookings,
            BigDecimal totalRevenue
    ) {
        this.totalUsers = totalUsers;
        this.activeUsers = activeUsers;
        this.activeEquipment = activeEquipment;
        this.totalBookings = totalBookings;
        this.pendingBookings = pendingBookings;
        this.approvedBookings = approvedBookings;
        this.completedBookings = completedBookings;
        this.cancelledBookings = cancelledBookings;
        this.rejectedBookings = rejectedBookings;
        this.totalRevenue = totalRevenue;
    }

    public long getTotalUsers() {
        return totalUsers;
    }

    public long getActiveUsers() {
        return activeUsers;
    }

    public long getActiveEquipment() {
        return activeEquipment;
    }

    public long getTotalBookings() {
        return totalBookings;
    }

    public long getPendingBookings() {
        return pendingBookings;
    }

    public long getApprovedBookings() {
        return approvedBookings;
    }

    public long getCompletedBookings() {
        return completedBookings;
    }

    public long getCancelledBookings() {
        return cancelledBookings;
    }

    public long getRejectedBookings() {
        return rejectedBookings;
    }

    public BigDecimal getTotalRevenue() {
        return totalRevenue;
    }
}