package com.example.it210ticketbus.service.impl;

import com.example.it210ticketbus.dto.response.SeatDTO;
import com.example.it210ticketbus.enums.SeatStatus;
import com.example.it210ticketbus.model.Seat;
import com.example.it210ticketbus.repository.SeatRepository;
import com.example.it210ticketbus.service.SeatService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SeatServiceImpl implements SeatService {

    private final SeatRepository seatRepository;

    @Override
    @Transactional(readOnly = true)
    public List<SeatDTO> getSeatsByTripId(Long tripId) {
        List<Seat> seats = seatRepository.findByTripIdOrderByFloorAndSeatNumber(tripId);
        LocalDateTime now = LocalDateTime.now();

        return seats.stream().map(seat -> {
            // Auto-release expired holds: if PENDING and past lockedUntil, treat as AVAILABLE
            String effectiveStatus;
            if (seat.getStatus() == SeatStatus.PENDING &&
                seat.getLockedUntil() != null &&
                seat.getLockedUntil().isBefore(now)) {
                effectiveStatus = SeatStatus.AVAILABLE.name();
            } else {
                effectiveStatus = seat.getStatus().name();
            }

            return SeatDTO.builder()
                    .seatId(seat.getId())
                    .seatNumber(seat.getSeatNumber())
                    .floor(seat.getFloor())
                    .status(effectiveStatus)
                    .price(seat.getPrice())
                    .lockedUntil(seat.getLockedUntil())
                    .build();
        }).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public boolean holdSeat(Long seatId, Long tripId) {
        // SELECT FOR UPDATE — locks the row to prevent race conditions
        Seat seat = seatRepository.findByIdAndTripIdForUpdate(seatId, tripId)
                .orElse(null);

        if (seat == null) {
            return false;
        }

        LocalDateTime now = LocalDateTime.now();

        // Check if seat is available (or expired hold)
        boolean isAvailable = seat.getStatus() == SeatStatus.AVAILABLE ||
                (seat.getStatus() == SeatStatus.PENDING &&
                 seat.getLockedUntil() != null &&
                 seat.getLockedUntil().isBefore(now));

        if (!isAvailable) {
            return false; // Seat already held or booked by someone else
        }

        // Hold the seat for 15 minutes
        seat.setStatus(SeatStatus.PENDING);
        seat.setLockedUntil(now.plusMinutes(15));
        seatRepository.save(seat);

        return true;
    }

    @Override
    @Transactional
    public boolean releaseSeat(Long seatId, Long tripId) {
        Seat seat = seatRepository.findByIdAndTripIdForUpdate(seatId, tripId)
                .orElse(null);

        if (seat == null || seat.getStatus() != SeatStatus.PENDING) {
            return false;
        }

        seat.setStatus(SeatStatus.AVAILABLE);
        seat.setLockedUntil(null);
        seatRepository.save(seat);

        return true;
    }
}
