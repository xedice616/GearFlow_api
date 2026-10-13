package com.xadice.gearflow.service;

import com.xadice.gearflow.dto.response.DashboardResponse;

import com.xadice.gearflow.model.BookingStatus;

import com.xadice.gearflow.repository.BookingRepository;
import com.xadice.gearflow.repository.EquipmentRepository;
import com.xadice.gearflow.repository.UserRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@Transactional(readOnly = true)
public class DashboardService {

    private final UserRepository userRepository;
    private final EquipmentRepository equipmentRepository;
    private final BookingRepository bookingRepository;

    public DashboardService(
            UserRepository userRepository,
            EquipmentRepository equipmentRepository,
            BookingRepository bookingRepository
    ) {
        this.userRepository = userRepository;
        this.equipmentRepository = equipmentRepository;
        this.bookingRepository = bookingRepository;
    }

    public DashboardResponse getDashboardStatistics() {

        long totalUsers =
                userRepository.count();

        long activeUsers =
                userRepository.countByEnabledTrue();

        long activeEquipment =
                equipmentRepository.countByActiveTrue();

        long totalBookings =
                bookingRepository.count();

        long pendingBookings =
                bookingRepository.countByStatus(
                        BookingStatus.PENDING
                );

        long approvedBookings =
                bookingRepository.countByStatus(
                        BookingStatus.APPROVED
                );

        long completedBookings =
                bookingRepository.countByStatus(
                        BookingStatus.COMPLETED
                );

        long cancelledBookings =
                bookingRepository.countByStatus(
                        BookingStatus.CANCELLED
                );

        long rejectedBookings =
                bookingRepository.countByStatus(
                        BookingStatus.REJECTED
                );

        BigDecimal totalRevenue =
                bookingRepository.getTotalRevenueByStatus(
                        BookingStatus.COMPLETED
                );

        if (totalRevenue == null) {
            totalRevenue = BigDecimal.ZERO;
        }

        return new DashboardResponse(
                totalUsers,
                activeUsers,
                activeEquipment,
                totalBookings,
                pendingBookings,
                approvedBookings,
                completedBookings,
                cancelledBookings,
                rejectedBookings,
                totalRevenue
        );
    }
}