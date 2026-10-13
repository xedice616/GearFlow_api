package com.xadice.gearflow.controller;

import com.xadice.gearflow.dto.request.EquipmentRequest;

import com.xadice.gearflow.dto.response.AvailabilityResponse;
import com.xadice.gearflow.dto.response.EquipmentResponse;
import com.xadice.gearflow.dto.response.PageResponse;

import com.xadice.gearflow.model.EquipmentCategory;

import com.xadice.gearflow.service.AvailabilityService;
import com.xadice.gearflow.service.EquipmentService;

import jakarta.validation.Valid;

import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.security.access.prepost.PreAuthorize;

import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@RestController
@RequestMapping("/api/v1/equipment")
public class EquipmentController {

    private final EquipmentService equipmentService;
    private final AvailabilityService availabilityService;
    public EquipmentController(
            EquipmentService equipmentService,
            AvailabilityService availabilityService
    ) {
        this.equipmentService = equipmentService;
        this.availabilityService = availabilityService;
    }

    @GetMapping
    public ResponseEntity<PageResponse<EquipmentResponse>> getAllEquipment(

            @RequestParam(required = false)
            String search,

            @RequestParam(required = false)
            EquipmentCategory category,

            @RequestParam(required = false)
            BigDecimal minPrice,

            @RequestParam(required = false)
            BigDecimal maxPrice,

            @PageableDefault(
                    size = 12,
                    sort = "createdAt"
            )
            Pageable pageable
    ) {

        return ResponseEntity.ok(
                equipmentService.getAllEquipment(
                        search,
                        category,
                        minPrice,
                        maxPrice,
                        pageable
                )
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<EquipmentResponse> getEquipmentById(
            @PathVariable Long id
    ) {

        return ResponseEntity.ok(
                equipmentService.getEquipmentById(id)
        );
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<EquipmentResponse> createEquipment(
            @Valid @RequestBody EquipmentRequest request
    ) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                        equipmentService.createEquipment(request)
                );
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<EquipmentResponse> updateEquipment(
            @PathVariable Long id,
            @Valid @RequestBody EquipmentRequest request
    ) {

        return ResponseEntity.ok(
                equipmentService.updateEquipment(
                        id,
                        request
                )
        );
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteEquipment(
            @PathVariable Long id
    ) {

        equipmentService.deleteEquipment(id);

        return ResponseEntity
                .noContent()
                .build();
    }


    @GetMapping("/{id}/availability")
    public ResponseEntity<AvailabilityResponse> checkAvailability(
            @PathVariable Long id,
            @RequestParam LocalDate startDate,
            @RequestParam LocalDate endDate
    ) {

        return ResponseEntity.ok(
                availabilityService.checkAvailability(
                        id,
                        startDate,
                        endDate
                )
        );
    }
}