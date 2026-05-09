package com.example.it210ticketbus.service;

import com.example.it210ticketbus.dto.response.SeatDTO;

import java.util.List;

public interface SeatService {
    
    /**
     * Get all seats for a trip with real-time status
     * (checks held_until to auto-release expired holds)
     */
    List<SeatDTO> getSeatsByTripId(Long tripId);
    
    /**
     * Hold a seat temporarily (15 minutes)
     * Uses SELECT FOR UPDATE to prevent race conditions
     * @return true if hold successful, false if seat already taken
     */
    boolean holdSeat(Long seatId, Long tripId);
    
    /**
     * Release a held seat (set back to AVAILABLE)
     */
    boolean releaseSeat(Long seatId, Long tripId);
}
