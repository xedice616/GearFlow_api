package com.xadice.gearflow.dto.response;

import com.xadice.gearflow.model.BookingStatus;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class AdminBookingResponse {

    private Long id;

    private Long userId;
    private String userName;
    private String userEmail;

    private Long equipmentId;
    private String equipmentName;

    private LocalDate startDate;
    private LocalDate endDate;

    private Integer quantity;

    private BigDecimal dailyPrice;
    private BigDecimal totalPrice;

    private BookingStatus status;

    private LocalDateTime createdAt;

    public AdminBookingResponse() {
    }

    public AdminBookingResponse(
            Long id,
            Long userId,
            String userName,
            String userEmail,
            Long equipmentId,
            String equipmentName,
            LocalDate startDate,
            LocalDate endDate,
            Integer quantity,
            BigDecimal dailyPrice,
            BigDecimal totalPrice,
            BookingStatus status,
            LocalDateTime createdAt
    ) {
        this.id = id;
        this.userId = userId;
        this.userName = userName;
        this.userEmail = userEmail;
        this.equipmentId = equipmentId;
        this.equipmentName = equipmentName;
        this.startDate = startDate;
        this.endDate = endDate;
        this.quantity = quantity;
        this.dailyPrice = dailyPrice;
        this.totalPrice = totalPrice;
        this.status = status;
        this.createdAt = createdAt;
    }

    public Long getId() {
        return id;
    }

    public Long getUserId() {
        return userId;
    }

    public String getUserName() {
        return userName;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public Long getEquipmentId() {
        return equipmentId;
    }

    public String getEquipmentName() {
        return equipmentName;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public BigDecimal getDailyPrice() {
        return dailyPrice;
    }

    public BigDecimal getTotalPrice() {
        return totalPrice;
    }

    public BookingStatus getStatus() {
        return status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}