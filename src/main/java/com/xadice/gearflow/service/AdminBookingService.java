package com.xadice.gearflow.service;

import com.xadice.gearflow.dto.response.AdminBookingResponse;
import com.xadice.gearflow.dto.response.PageResponse;
import com.xadice.gearflow.exception.InvalidOperationException;
import com.xadice.gearflow.exception.ResourceNotFoundException;
import com.xadice.gearflow.model.Booking;
import com.xadice.gearflow.model.BookingStatus;
import com.xadice.gearflow.model.Equipment;
import com.xadice.gearflow.model.User;
import com.xadice.gearflow.repository.BookingRepository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
@Transactional
public class AdminBookingService {

    private final BookingRepository bookingRepository;

    public AdminBookingService(
            BookingRepository bookingRepository
    ) {
        this.bookingRepository = bookingRepository;
    }

    @Transactional(readOnly = true)
    public PageResponse<AdminBookingResponse> getAllBookings(
            BookingStatus status,
            Pageable pageable
    ) {

        Page<Booking> bookingPage;

        if (status == null) {
            bookingPage = bookingRepository.findAll(pageable);
        } else {
            bookingPage = bookingRepository.findAllByStatus(
                    status,
                    pageable
            );
        }

        Page<AdminBookingResponse> responsePage =
                bookingPage.map(this::mapToResponse);

        return PageResponse.from(responsePage);
    }

    @Transactional(readOnly = true)
    public AdminBookingResponse getBookingById(
            Long bookingId
    ) {

        Booking booking = findBooking(bookingId);

        return mapToResponse(booking);
    }

    public AdminBookingResponse approveBooking(
            Long bookingId
    ) {

        Booking booking = findBooking(bookingId);

        if (booking.getStatus() != BookingStatus.PENDING) {
            throw new InvalidOperationException(
                    "Only pending bookings can be approved"
            );
        }

        booking.setStatus(BookingStatus.APPROVED);

        Booking savedBooking =
                bookingRepository.save(booking);

        return mapToResponse(savedBooking);
    }

    public AdminBookingResponse rejectBooking(
            Long bookingId
    ) {

        Booking booking = findBooking(bookingId);

        if (booking.getStatus() != BookingStatus.PENDING) {
            throw new InvalidOperationException(
                    "Only pending bookings can be rejected"
            );
        }

        booking.setStatus(BookingStatus.REJECTED);

        Booking savedBooking =
                bookingRepository.save(booking);

        return mapToResponse(savedBooking);
    }

    public AdminBookingResponse completeBooking(
            Long bookingId
    ) {

        Booking booking = findBooking(bookingId);

        // Yalnız APPROVED booking tamamlanmış ola bilər
        if (booking.getStatus() != BookingStatus.APPROVED) {
            throw new InvalidOperationException(
                    "Only approved bookings can be completed"
            );
        }

        // Booking-in müddəti hələ bitməyibsə COMPLETED etmək olmaz
        if (booking.getEndDate().isAfter(LocalDate.now())) {
            throw new InvalidOperationException(
                    "Booking cannot be completed before its end date"
            );
        }

        booking.setStatus(BookingStatus.COMPLETED);

        Booking savedBooking =
                bookingRepository.save(booking);

        return mapToResponse(savedBooking);
    }

    private Booking findBooking(
            Long bookingId
    ) {

        return bookingRepository
                .findById(bookingId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Booking not found with id: "
                                        + bookingId
                        )
                );
    }

    private AdminBookingResponse mapToResponse(
            Booking booking
    ) {

        User user = booking.getUser();
        Equipment equipment = booking.getEquipment();

        return new AdminBookingResponse(
                booking.getId(),

                user.getId(),
                user.getFullName(),
                user.getEmail(),

                equipment.getId(),
                equipment.getName(),

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