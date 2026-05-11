package com.example.it210ticketbus.repository;

import com.example.it210ticketbus.enums.TripStatus;
import com.example.it210ticketbus.model.Trip;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface TripRepository extends JpaRepository<Trip, Long> {

    @Query("SELECT t FROM Trip t " +
           "JOIN FETCH t.route r " +
           "JOIN FETCH r.origin o " +
           "JOIN FETCH r.destination d " +
           "JOIN FETCH t.bus b " +
           "LEFT JOIN FETCH t.seats s " +
           "WHERE t.id = :tripId")
    Optional<Trip> findWithDetailsById(@Param("tripId") Long tripId);

    @Query("SELECT DISTINCT t FROM Trip t " +
           "JOIN FETCH t.route r " +
           "JOIN FETCH r.origin o " +
           "JOIN FETCH r.destination d " +
           "JOIN FETCH t.bus b " +
           "LEFT JOIN FETCH t.seats s " +
           "WHERE o.provinceCode = :fromCode " +
           "AND d.provinceCode = :toCode " +
           "AND t.departureTime BETWEEN :startOfDay AND :endOfDay " +
           "AND t.status = :status " +
           "ORDER BY t.departureTime ASC")
    List<Trip> searchTrips(@Param("fromCode") String fromCode,
                           @Param("toCode") String toCode,
                           @Param("startOfDay") LocalDateTime startOfDay,
                           @Param("endOfDay") LocalDateTime endOfDay,
                           @Param("status") TripStatus status);

    @Query("SELECT COUNT(t) FROM Trip t WHERE t.departureTime BETWEEN :start AND :end")
    long countTripsBetween(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);
}
