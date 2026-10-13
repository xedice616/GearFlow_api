package com.xadice.gearflow.dto.response;

import com.xadice.gearflow.model.EquipmentCategory;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class EquipmentResponse {

    private Long id;
    private String name;
    private String description;
    private EquipmentCategory category;
    private BigDecimal dailyPrice;
    private Integer totalQuantity;
    private boolean active;
    private String imageUrl;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public EquipmentResponse() {
    }

    public EquipmentResponse(
            Long id,
            String name,
            String description,
            EquipmentCategory category,
            BigDecimal dailyPrice,
            Integer totalQuantity,
            boolean active,
            String imageUrl,
            LocalDateTime createdAt,
            LocalDateTime updatedAt
    ) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.category = category;
        this.dailyPrice = dailyPrice;
        this.totalQuantity = totalQuantity;
        this.active = active;
        this.imageUrl = imageUrl;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public EquipmentCategory getCategory() {
        return category;
    }

    public BigDecimal getDailyPrice() {
        return dailyPrice;
    }

    public Integer getTotalQuantity() {
        return totalQuantity;
    }

    public boolean isActive() {
        return active;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
}