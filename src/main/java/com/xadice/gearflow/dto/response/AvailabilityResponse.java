package com.xadice.gearflow.dto.response;

import java.time.LocalDate;

public class AvailabilityResponse {

    private Long equipmentId;
    private String equipmentName;
    private LocalDate startDate;
    private LocalDate endDate;
    private Integer totalQuantity;
    private Long reservedQuantity;
    private Long availableQuantity;
    private boolean available;

    public AvailabilityResponse() {
    }

    public AvailabilityResponse(
            Long equipmentId,
            String equipmentName,
            LocalDate startDate,
            LocalDate endDate,
            Integer totalQuantity,
            Long reservedQuantity,
            Long availableQuantity,
            boolean available
    ) {
        this.equipmentId = equipmentId;
        this.equipmentName = equipmentName;
        this.startDate = startDate;
        this.endDate = endDate;
        this.totalQuantity = totalQuantity;
        this.reservedQuantity = reservedQuantity;
        this.availableQuantity = availableQuantity;
        this.available = available;
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

    public Integer getTotalQuantity() {
        return totalQuantity;
    }

    public Long getReservedQuantity() {
        return reservedQuantity;
    }

    public Long getAvailableQuantity() {
        return availableQuantity;
    }

    public boolean isAvailable() {
        return available;
    }
}