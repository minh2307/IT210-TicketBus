package com.example.it210ticketbus.repository;

import com.example.it210ticketbus.model.Seat;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SeatRepository extends JpaRepository<Seat, Long> {

    List<Seat> findByTripIdOrderBySeatNumber(Long tripId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT s FROM Seat s WHERE s.id = :seatId AND s.trip.id = :tripId")
    Optional<Seat> findByIdAndTripIdForUpdate(@Param("seatId") Long seatId, @Param("tripId") Long tripId);

    @Query("SELECT s FROM Seat s WHERE s.trip.id = :tripId ORDER BY s.floor, s.seatNumber")
    List<Seat> findByTripIdOrderByFloorAndSeatNumber(@Param("tripId") Long tripId);
}
