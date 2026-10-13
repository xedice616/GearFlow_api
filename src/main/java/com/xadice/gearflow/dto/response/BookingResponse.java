package com.xadice.gearflow.dto.response;

import com.xadice.gearflow.model.BookingStatus;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class BookingResponse {

    private Long id;

    private Long equipmentId;
    private String equipmentName;
    private String equipmentImageUrl;

    private LocalDate startDate;
    private LocalDate endDate;

    private Integer quantity;

    private BigDecimal dailyPrice;
    private BigDecimal totalPrice;

    private BookingStatus status;

    private LocalDateTime createdAt;

    public BookingResponse() {
    }

    public BookingResponse(
            Long id,
            Long equipmentId,
            String equipmentName,
            String equipmentImageUrl,
            LocalDate startDate,
            LocalDate endDate,
            Integer quantity,
            BigDecimal dailyPrice,
            BigDecimal totalPrice,
            BookingStatus status,
            LocalDateTime createdAt
    ) {
        this.id = id;
        this.equipmentId = equipmentId;
        this.equipmentName = equipmentName;
        this.equipmentImageUrl = equipmentImageUrl;
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

    public Long getEquipmentId() {
        return equipmentId;
    }

    public String getEquipmentName() {
        return equipmentName;
    }

    public String getEquipmentImageUrl() {
        return equipmentImageUrl;
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