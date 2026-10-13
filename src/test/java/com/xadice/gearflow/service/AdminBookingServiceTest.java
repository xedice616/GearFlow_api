package com.xadice.gearflow.service;

import com.xadice.gearflow.dto.response.AdminBookingResponse;
import com.xadice.gearflow.exception.InvalidOperationException;
import com.xadice.gearflow.model.Booking;
import com.xadice.gearflow.model.BookingStatus;
import com.xadice.gearflow.model.Equipment;
import com.xadice.gearflow.model.EquipmentCategory;
import com.xadice.gearflow.model.Role;
import com.xadice.gearflow.model.User;
import com.xadice.gearflow.repository.BookingRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AdminBookingServiceTest {

    @Mock
    private BookingRepository bookingRepository;

    @InjectMocks
    private AdminBookingService adminBookingService;

    private Booking booking;

    @BeforeEach
    void setUp() {

        User user = new User();
        user.setFullName("Test User");
        user.setEmail("user@test.com");
        user.setPassword("encoded-password");
        user.setRole(Role.USER);
        user.setEnabled(true);

        Equipment equipment = new Equipment();
        equipment.setName("Sony Alpha A7 IV");
        equipment.setDescription("Professional mirrorless camera");
        equipment.setCategory(EquipmentCategory.CAMERA);
        equipment.setDailyPrice(new BigDecimal("40.00"));
        equipment.setTotalQuantity(5);
        equipment.setActive(true);

        booking = new Booking();

        booking.setUser(user);
        booking.setEquipment(equipment);

        booking.setStartDate(
                LocalDate.now().minusDays(5)
        );

        booking.setEndDate(
                LocalDate.now().minusDays(2)
        );

        booking.setQuantity(2);

        booking.setTotalPrice(
                new BigDecimal("240.00")
        );

        booking.setStatus(
                BookingStatus.PENDING
        );
    }

    @Test
    void approveBooking_shouldChangePendingToApproved() {

        booking.setStatus(BookingStatus.PENDING);

        when(bookingRepository.findById(1L))
                .thenReturn(Optional.of(booking));

        when(bookingRepository.save(any(Booking.class)))
                .thenAnswer(invocation ->
                        invocation.getArgument(0)
                );

        AdminBookingResponse response =
                adminBookingService.approveBooking(1L);

        assertEquals(
                BookingStatus.APPROVED,
                response.getStatus()
        );

        assertEquals(
                BookingStatus.APPROVED,
                booking.getStatus()
        );

        verify(bookingRepository)
                .save(booking);
    }

    @Test
    void approveBooking_shouldRejectInvalidStatusTransition() {

        booking.setStatus(
                BookingStatus.APPROVED
        );

        when(bookingRepository.findById(1L))
                .thenReturn(Optional.of(booking));

        InvalidOperationException exception =
                assertThrows(
                        InvalidOperationException.class,
                        () ->
                                adminBookingService
                                        .approveBooking(1L)
                );

        assertEquals(
                "Only pending bookings can be approved",
                exception.getMessage()
        );

        verify(
                bookingRepository,
                never()
        ).save(any(Booking.class));
    }

    @Test
    void rejectBooking_shouldChangePendingToRejected() {

        booking.setStatus(
                BookingStatus.PENDING
        );

        when(bookingRepository.findById(1L))
                .thenReturn(Optional.of(booking));

        when(bookingRepository.save(any(Booking.class)))
                .thenAnswer(invocation ->
                        invocation.getArgument(0)
                );

        AdminBookingResponse response =
                adminBookingService.rejectBooking(1L);

        assertEquals(
                BookingStatus.REJECTED,
                response.getStatus()
        );

        assertEquals(
                BookingStatus.REJECTED,
                booking.getStatus()
        );

        verify(bookingRepository)
                .save(booking);
    }

    @Test
    void rejectBooking_shouldRejectInvalidStatusTransition() {

        booking.setStatus(
                BookingStatus.CANCELLED
        );

        when(bookingRepository.findById(1L))
                .thenReturn(Optional.of(booking));

        InvalidOperationException exception =
                assertThrows(
                        InvalidOperationException.class,
                        () ->
                                adminBookingService
                                        .rejectBooking(1L)
                );

        assertEquals(
                "Only pending bookings can be rejected",
                exception.getMessage()
        );

        verify(
                bookingRepository,
                never()
        ).save(any(Booking.class));
    }

    @Test
    void completeBooking_shouldChangeApprovedToCompleted() {

        booking.setStatus(
                BookingStatus.APPROVED
        );

        booking.setEndDate(
                LocalDate.now().minusDays(1)
        );

        when(bookingRepository.findById(1L))
                .thenReturn(Optional.of(booking));

        when(bookingRepository.save(any(Booking.class)))
                .thenAnswer(invocation ->
                        invocation.getArgument(0)
                );

        AdminBookingResponse response =
                adminBookingService.completeBooking(1L);

        assertEquals(
                BookingStatus.COMPLETED,
                response.getStatus()
        );

        assertEquals(
                BookingStatus.COMPLETED,
                booking.getStatus()
        );

        verify(bookingRepository)
                .save(booking);
    }

    @Test
    void completeBooking_shouldRejectNonApprovedBooking() {

        booking.setStatus(
                BookingStatus.PENDING
        );

        when(bookingRepository.findById(1L))
                .thenReturn(Optional.of(booking));

        InvalidOperationException exception =
                assertThrows(
                        InvalidOperationException.class,
                        () ->
                                adminBookingService
                                        .completeBooking(1L)
                );

        assertEquals(
                "Only approved bookings can be completed",
                exception.getMessage()
        );

        verify(
                bookingRepository,
                never()
        ).save(any(Booking.class));
    }

    @Test
    void completeBooking_shouldRejectBookingBeforeEndDate() {

        booking.setStatus(
                BookingStatus.APPROVED
        );

        booking.setStartDate(
                LocalDate.now().plusDays(5)
        );

        booking.setEndDate(
                LocalDate.now().plusDays(8)
        );

        when(bookingRepository.findById(1L))
                .thenReturn(Optional.of(booking));

        InvalidOperationException exception =
                assertThrows(
                        InvalidOperationException.class,
                        () ->
                                adminBookingService
                                        .completeBooking(1L)
                );

        assertEquals(
                "Booking cannot be completed before its end date",
                exception.getMessage()
        );

        verify(
                bookingRepository,
                never()
        ).save(any(Booking.class));
    }
}