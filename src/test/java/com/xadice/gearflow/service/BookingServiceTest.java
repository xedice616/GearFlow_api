package com.xadice.gearflow.service;

import com.xadice.gearflow.dto.request.CreateBookingRequest;
import com.xadice.gearflow.dto.response.BookingResponse;
import com.xadice.gearflow.exception.BookingConflictException;
import com.xadice.gearflow.exception.ResourceNotFoundException;
import com.xadice.gearflow.model.Booking;
import com.xadice.gearflow.model.BookingStatus;
import com.xadice.gearflow.model.Equipment;
import com.xadice.gearflow.model.EquipmentCategory;
import com.xadice.gearflow.model.Role;
import com.xadice.gearflow.model.User;
import com.xadice.gearflow.repository.BookingRepository;
import com.xadice.gearflow.repository.EquipmentRepository;
import com.xadice.gearflow.repository.UserRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;

import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BookingServiceTest {

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private EquipmentRepository equipmentRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private BookingService bookingService;

    private User user;
    private Equipment equipment;

    private LocalDate startDate;
    private LocalDate endDate;

    @BeforeEach
    void setUp() {

        startDate = LocalDate.now().plusDays(5);
        endDate = startDate.plusDays(3);

        user = new User();
        user.setFullName("Test User");
        user.setEmail("user@test.com");
        user.setPassword("encoded-password");
        user.setRole(Role.USER);
        user.setEnabled(true);

        equipment = new Equipment();
        equipment.setName("Sony Camera");
        equipment.setDescription("Professional camera");
        equipment.setCategory(EquipmentCategory.CAMERA);
        equipment.setDailyPrice(
                new BigDecimal("40.00")
        );
        equipment.setTotalQuantity(5);
        equipment.setActive(true);
        equipment.setImageUrl(
                "https://example.com/camera.jpg"
        );
    }

    @Test
    void createBooking_shouldCalculateTotalPriceCorrectly() {

        CreateBookingRequest request =
                createRequest(
                        1L,
                        startDate,
                        endDate,
                        2
                );

        when(
                userRepository.findByEmail(
                        "user@test.com"
                )
        ).thenReturn(
                Optional.of(user)
        );

        when(
                equipmentRepository.findByIdForUpdate(1L)
        ).thenReturn(
                Optional.of(equipment)
        );

        when(
                bookingRepository.getReservedQuantity(
                        eq(equipment),
                        anyList(),
                        eq(startDate),
                        eq(endDate)
                )
        ).thenReturn(0L);

        when(
                bookingRepository.save(any(Booking.class))
        ).thenAnswer(invocation -> {

            Booking booking =
                    invocation.getArgument(0);

            return booking;
        });

        BookingResponse response =
                bookingService.createBooking(
                        request,
                        "user@test.com"
                );

        assertEquals(
                new BigDecimal("240.00"),
                response.getTotalPrice()
        );

        assertEquals(
                BookingStatus.PENDING,
                response.getStatus()
        );

        assertEquals(
                2,
                response.getQuantity()
        );

        assertEquals(
                "Sony Camera",
                response.getEquipmentName()
        );

        verify(
                equipmentRepository
        ).findByIdForUpdate(1L);

        verify(
                bookingRepository
        ).save(any(Booking.class));
    }

    @Test
    void createBooking_shouldSaveCorrectBookingData() {

        LocalDate customStartDate = LocalDate.now().plusDays(10);
        LocalDate customEndDate = customStartDate.plusDays(4);
        CreateBookingRequest request = createRequest(1L, customStartDate, customEndDate, 3);

        when(
                userRepository.findByEmail(
                        "user@test.com"
                )
        ).thenReturn(
                Optional.of(user)
        );

        when(
                equipmentRepository.findByIdForUpdate(1L)
        ).thenReturn(
                Optional.of(equipment)
        );

        when(
                bookingRepository.getReservedQuantity(
                        eq(equipment),
                        anyList(),
                        eq(customStartDate),
                        eq(customEndDate)
                )
        ).thenReturn(0L);

        when(
                bookingRepository.save(any(Booking.class))
        ).thenAnswer(
                invocation -> invocation.getArgument(0)
        );

        bookingService.createBooking(
                request,
                "user@test.com"
        );

        ArgumentCaptor<Booking> bookingCaptor =
                ArgumentCaptor.forClass(
                        Booking.class
                );

        verify(
                bookingRepository
        ).save(
                bookingCaptor.capture()
        );

        Booking savedBooking =
                bookingCaptor.getValue();

        assertEquals(
                user,
                savedBooking.getUser()
        );

        assertEquals(
                equipment,
                savedBooking.getEquipment()
        );

        assertEquals(
                customStartDate,
                savedBooking.getStartDate()
        );

        assertEquals(
                customEndDate,
                savedBooking.getEndDate()
        );

        assertEquals(
                3,
                savedBooking.getQuantity()
        );

        assertEquals(
                BookingStatus.PENDING,
                savedBooking.getStatus()
        );

        assertEquals(
                new BigDecimal("480.00"),
                savedBooking.getTotalPrice()
        );

        verify(
                equipmentRepository
        ).findByIdForUpdate(1L);
    }

    @Test
    void createBooking_shouldThrowConflict_whenQuantityIsNotAvailable() {

        CreateBookingRequest request =
                createRequest(
                        1L,
                        startDate,
                        endDate,
                        3
                );

        when(
                userRepository.findByEmail(
                        "user@test.com"
                )
        ).thenReturn(
                Optional.of(user)
        );

        when(
                equipmentRepository.findByIdForUpdate(1L)
        ).thenReturn(
                Optional.of(equipment)
        );


        when(
                bookingRepository.getReservedQuantity(
                        eq(equipment),
                        anyList(),
                        eq(startDate),
                        eq(endDate)
                )
        ).thenReturn(3L);

        BookingConflictException exception =
                assertThrows(
                        BookingConflictException.class,
                        () ->
                                bookingService.createBooking(
                                        request,
                                        "user@test.com"
                                )
                );

        assertTrue(
                exception.getMessage()
                        .contains("2")
        );

        verify(
                equipmentRepository
        ).findByIdForUpdate(1L);

        verify(
                bookingRepository,
                never()
        ).save(any(Booking.class));
    }

    @Test
    void createBooking_shouldThrowNotFound_whenEquipmentDoesNotExist() {

        CreateBookingRequest request =
                createRequest(
                        999L,
                        startDate,
                        endDate,
                        1
                );

        when(
                userRepository.findByEmail(
                        "user@test.com"
                )
        ).thenReturn(
                Optional.of(user)
        );

        when(
                equipmentRepository.findByIdForUpdate(999L)
        ).thenReturn(
                Optional.empty()
        );

        ResourceNotFoundException exception =
                assertThrows(
                        ResourceNotFoundException.class,
                        () ->
                                bookingService.createBooking(
                                        request,
                                        "user@test.com"
                                )
                );

        assertTrue(
                exception.getMessage()
                        .contains("999")
        );

        verify(
                equipmentRepository
        ).findByIdForUpdate(999L);

        verify(
                bookingRepository,
                never()
        ).save(any(Booking.class));
    }

    @Test
    void createBooking_shouldThrowNotFound_whenEquipmentIsInactive() {

        equipment.setActive(false);

        CreateBookingRequest request = createRequest(1L, startDate, endDate, 1);

        when(
                userRepository.findByEmail(
                        "user@test.com"
                )
        ).thenReturn(Optional.of(user));
        when(
                equipmentRepository.findByIdForUpdate(1L)
        ).thenReturn(
                Optional.of(equipment)
        );

        ResourceNotFoundException exception =
                assertThrows(
                        ResourceNotFoundException.class,
                        () ->
                                bookingService.createBooking(
                                        request,
                                        "user@test.com"
                                )
                );

        assertTrue(
                exception.getMessage()
                        .contains("1")
        );

        verify(equipmentRepository).findByIdForUpdate(1L);

        verify(bookingRepository, never()).save(any(Booking.class));
    }

    private CreateBookingRequest createRequest(Long equipmentId, LocalDate startDate, LocalDate endDate, Integer quantity
    ) {

        CreateBookingRequest request = new CreateBookingRequest();

        request.setEquipmentId(equipmentId);

        request.setStartDate(startDate);

        request.setEndDate(endDate);

        request.setQuantity(quantity);

        return request;
    }
}