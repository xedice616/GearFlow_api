package com.xadice.gearflow.service;

import com.xadice.gearflow.dto.response.AvailabilityResponse;

import com.xadice.gearflow.exception.InvalidOperationException;
import com.xadice.gearflow.exception.ResourceNotFoundException;

import com.xadice.gearflow.model.BookingStatus;
import com.xadice.gearflow.model.Equipment;

import com.xadice.gearflow.repository.BookingRepository;
import com.xadice.gearflow.repository.EquipmentRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class AvailabilityService {

    private static final List<BookingStatus> RESERVED_STATUSES =
            List.of(
                    BookingStatus.PENDING,
                    BookingStatus.APPROVED
            );

    private final EquipmentRepository equipmentRepository;
    private final BookingRepository bookingRepository;

    public AvailabilityService(
            EquipmentRepository equipmentRepository,
            BookingRepository bookingRepository
    ) {
        this.equipmentRepository = equipmentRepository;
        this.bookingRepository = bookingRepository;
    }

    public AvailabilityResponse checkAvailability(
            Long equipmentId,
            LocalDate startDate,
            LocalDate endDate
    ) {

        validateDates(startDate, endDate);

        Equipment equipment =
                equipmentRepository.findById(equipmentId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Equipment not found with id: "
                                                + equipmentId
                                )
                        );

        if (!equipment.isActive()) {
            throw new ResourceNotFoundException(
                    "Equipment not found with id: "
                            + equipmentId
            );
        }

        long reservedQuantity =
                bookingRepository.getReservedQuantity(
                        equipment,
                        RESERVED_STATUSES,
                        startDate,
                        endDate
                );

        long availableQuantity =
                Math.max(
                        0,
                        equipment.getTotalQuantity()
                                - reservedQuantity
                );

        return new AvailabilityResponse(
                equipment.getId(),
                equipment.getName(),
                startDate,
                endDate,
                equipment.getTotalQuantity(),
                reservedQuantity,
                availableQuantity,
                availableQuantity > 0
        );
    }

    private void validateDates(
            LocalDate startDate,
            LocalDate endDate
    ) {

        if (startDate == null || endDate == null) {
            throw new InvalidOperationException(
                    "Start date and end date are required"
            );
        }

        if (startDate.isBefore(LocalDate.now())) {
            throw new InvalidOperationException(
                    "Start date cannot be in the past"
            );
        }

        if (!endDate.isAfter(startDate)) {
            throw new InvalidOperationException(
                    "End date must be after start date"
            );
        }
    }
}