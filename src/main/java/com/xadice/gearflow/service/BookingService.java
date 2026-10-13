package com.xadice.gearflow.service;

import com.xadice.gearflow.dto.request.CreateBookingRequest;
import com.xadice.gearflow.dto.response.BookingResponse;

import com.xadice.gearflow.exception.BookingConflictException;
import com.xadice.gearflow.exception.InvalidOperationException;
import com.xadice.gearflow.exception.ResourceNotFoundException;

import com.xadice.gearflow.model.Booking;
import com.xadice.gearflow.model.BookingStatus;
import com.xadice.gearflow.model.Equipment;
import com.xadice.gearflow.model.User;

import com.xadice.gearflow.repository.BookingRepository;
import com.xadice.gearflow.repository.EquipmentRepository;
import com.xadice.gearflow.repository.UserRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

import java.util.List;

@Service
@Transactional
public class BookingService {

    private static final List<BookingStatus> RESERVED_STATUSES =
            List.of(
                    BookingStatus.PENDING,
                    BookingStatus.APPROVED
            );

    private final BookingRepository bookingRepository;
    private final EquipmentRepository equipmentRepository;
    private final UserRepository userRepository;

    public BookingService(
            BookingRepository bookingRepository,
            EquipmentRepository equipmentRepository,
            UserRepository userRepository
    ) {
        this.bookingRepository = bookingRepository;
        this.equipmentRepository = equipmentRepository;
        this.userRepository = userRepository;
    }

    public BookingResponse createBooking(
            CreateBookingRequest request,
            String authenticatedEmail
    ) {

        validateDates(
                request.getStartDate(),
                request.getEndDate()
        );

        User user =
                userRepository.findByEmail(authenticatedEmail)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "User not found"
                                )
                        );

        Equipment equipment =
                equipmentRepository.findByIdForUpdate(
                                request.getEquipmentId()
                        )
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Equipment not found with id: "
                                                + request.getEquipmentId()
                                )
                        );

        if (!equipment.isActive()) {
            throw new ResourceNotFoundException(
                    "Equipment not found with id: "
                            + request.getEquipmentId()
            );
        }

        long reservedQuantity =
                bookingRepository.getReservedQuantity(
                        equipment,
                        RESERVED_STATUSES,
                        request.getStartDate(),
                        request.getEndDate()
                );

        long availableQuantity =
                equipment.getTotalQuantity()
                        - reservedQuantity;

        if (request.getQuantity() > availableQuantity) {

            throw new BookingConflictException(
                    "Only "
                            + Math.max(availableQuantity, 0)
                            + " unit(s) are available for the selected dates"
            );
        }

        long rentalDays =
                ChronoUnit.DAYS.between(
                        request.getStartDate(),
                        request.getEndDate()
                );

        BigDecimal totalPrice =
                equipment.getDailyPrice()
                        .multiply(
                                BigDecimal.valueOf(rentalDays)
                        )
                        .multiply(
                                BigDecimal.valueOf(
                                        request.getQuantity()
                                )
                        );

        Booking booking = new Booking();

        booking.setUser(user);
        booking.setEquipment(equipment);
        booking.setStartDate(request.getStartDate());
        booking.setEndDate(request.getEndDate());
        booking.setQuantity(request.getQuantity());
        booking.setTotalPrice(totalPrice);
        booking.setStatus(BookingStatus.PENDING);

        Booking savedBooking =
                bookingRepository.save(booking);

        return mapToResponse(savedBooking);
    }

    @Transactional(readOnly = true)
    public List<BookingResponse> getMyBookings(
            String authenticatedEmail
    ) {

        User user =
                userRepository.findByEmail(authenticatedEmail)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "User not found"
                                )
                        );

        return bookingRepository
                .findAllByUserOrderByCreatedAtDesc(user)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public BookingResponse getMyBookingById(
            Long bookingId,
            String authenticatedEmail
    ) {

        Booking booking = findBooking(bookingId);

        verifyOwner(
                booking,
                authenticatedEmail
        );

        return mapToResponse(booking);
    }

    public BookingResponse cancelBooking(
            Long bookingId,
            String authenticatedEmail
    ) {

        Booking booking = findBooking(bookingId);

        verifyOwner(
                booking,
                authenticatedEmail
        );

        if (booking.getStatus() != BookingStatus.PENDING
                && booking.getStatus() != BookingStatus.APPROVED) {

            throw new InvalidOperationException(
                    "This booking cannot be cancelled"
            );
        }

        booking.setStatus(
                BookingStatus.CANCELLED
        );

        return mapToResponse(
                bookingRepository.save(booking)
        );
    }

    private Booking findBooking(
            Long bookingId
    ) {

        return bookingRepository.findById(bookingId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Booking not found with id: "
                                        + bookingId
                        )
                );
    }

    private void verifyOwner(
            Booking booking,
            String authenticatedEmail
    ) {

        if (!booking.getUser()
                .getEmail()
                .equalsIgnoreCase(authenticatedEmail)) {

            throw new ResourceNotFoundException(
                    "Booking not found with id: "
                            + booking.getId()
            );
        }
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

    private BookingResponse mapToResponse(
            Booking booking
    ) {

        Equipment equipment =
                booking.getEquipment();

        return new BookingResponse(
                booking.getId(),
                equipment.getId(),
                equipment.getName(),
                equipment.getImageUrl(),
                booking.getStartDate(),
                booking.getEndDate(),
                booking.getQuantity(),
                equipment.getDailyPrice(),
                booking.getTotalPrice(),
                booking.getStatus(),
                booking.getCreatedAt()
        );
    }
}