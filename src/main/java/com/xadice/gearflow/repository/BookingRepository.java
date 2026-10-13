package com.xadice.gearflow.repository;

import com.xadice.gearflow.model.Booking;
import com.xadice.gearflow.model.BookingStatus;
import com.xadice.gearflow.model.Equipment;
import com.xadice.gearflow.model.User;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public interface BookingRepository
        extends JpaRepository<Booking, Long> {

    List<Booking> findAllByUserOrderByCreatedAtDesc(
            User user
    );

    Page<Booking> findAllByStatus(
            BookingStatus status,
            Pageable pageable
    );

    long countByStatus(
            BookingStatus status
    );

    @Query("""
            SELECT COALESCE(SUM(b.quantity), 0)
            FROM Booking b
            WHERE b.equipment = :equipment
              AND b.status IN :statuses
              AND b.startDate < :endDate
              AND b.endDate > :startDate
            """)
    Long getReservedQuantity(
            @Param("equipment") Equipment equipment,
            @Param("statuses") List<BookingStatus> statuses,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );

    @Query("""
            SELECT COALESCE(SUM(b.totalPrice), 0)
            FROM Booking b
            WHERE b.status = :status
            """)
    BigDecimal getTotalRevenueByStatus(
            @Param("status") BookingStatus status
    );
}