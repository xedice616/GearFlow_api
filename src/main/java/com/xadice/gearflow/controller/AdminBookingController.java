package com.xadice.gearflow.controller;

import com.xadice.gearflow.dto.response.AdminBookingResponse;
import com.xadice.gearflow.dto.response.PageResponse;

import com.xadice.gearflow.model.BookingStatus;

import com.xadice.gearflow.service.AdminBookingService;

import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;

import org.springframework.http.ResponseEntity;

import org.springframework.security.access.prepost.PreAuthorize;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/admin/bookings")
@PreAuthorize("hasRole('ADMIN')")
public class AdminBookingController {

    private final AdminBookingService adminBookingService;

    public AdminBookingController(
            AdminBookingService adminBookingService
    ) {
        this.adminBookingService =
                adminBookingService;
    }

    @GetMapping
    public ResponseEntity<PageResponse<AdminBookingResponse>>
    getAllBookings(

            @RequestParam(required = false)
            BookingStatus status,

            @PageableDefault(
                    size = 10,
                    sort = "createdAt"
            )
            Pageable pageable
    ) {

        return ResponseEntity.ok(
                adminBookingService.getAllBookings(
                        status,
                        pageable
                )
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<AdminBookingResponse>
    getBookingById(
            @PathVariable Long id
    ) {

        return ResponseEntity.ok(
                adminBookingService
                        .getBookingById(id)
        );
    }

    @PatchMapping("/{id}/approve")
    public ResponseEntity<AdminBookingResponse>
    approveBooking(
            @PathVariable Long id
    ) {

        return ResponseEntity.ok(
                adminBookingService
                        .approveBooking(id)
        );
    }

    @PatchMapping("/{id}/reject")
    public ResponseEntity<AdminBookingResponse>
    rejectBooking(
            @PathVariable Long id
    ) {

        return ResponseEntity.ok(
                adminBookingService
                        .rejectBooking(id)
        );
    }

    @PatchMapping("/{id}/complete")
    public ResponseEntity<AdminBookingResponse>
    completeBooking(
            @PathVariable Long id
    ) {

        return ResponseEntity.ok(
                adminBookingService
                        .completeBooking(id)
        );
    }
}