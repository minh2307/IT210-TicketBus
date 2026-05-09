package com.example.it210ticketbus.service;

import com.example.it210ticketbus.dto.response.SeatDTO;
import com.example.it210ticketbus.dto.response.TripDTO;
import com.example.it210ticketbus.model.Trip;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface TripService {
    
    /**
     * Search trips by origin, destination and date
     */
    List<TripDTO> searchTrips(String fromCode, String toCode, LocalDate date);
    
    /**
     * Find trip by ID
     */
    Optional<Trip> findById(Long tripId);
}
