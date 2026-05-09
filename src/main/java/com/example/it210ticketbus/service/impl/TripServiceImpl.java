package com.example.it210ticketbus.service.impl;

import com.example.it210ticketbus.dto.response.TripDTO;
import com.example.it210ticketbus.enums.SeatStatus;
import com.example.it210ticketbus.enums.TripStatus;
import com.example.it210ticketbus.model.Trip;
import com.example.it210ticketbus.repository.TripRepository;
import com.example.it210ticketbus.service.TripService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TripServiceImpl implements TripService {

    private final TripRepository tripRepository;

    @Override
    @Transactional(readOnly = true)
    public List<TripDTO> searchTrips(String fromCode, String toCode, LocalDate date) {
        LocalDateTime startOfDay = date.atStartOfDay();
        LocalDateTime endOfDay = date.atTime(LocalTime.MAX);

        List<Trip> trips = tripRepository.searchTrips(fromCode, toCode, startOfDay, endOfDay, TripStatus.SCHEDULED);
        
        // Sử dụng múi giờ Việt Nam để đồng bộ với Database
        LocalDateTime now = LocalDateTime.now(java.time.ZoneId.of("Asia/Ho_Chi_Minh"));
        LocalDateTime expirationThreshold = now.minusMinutes(30);

        return trips.stream()
                .filter(trip -> trip.getDepartureTime().isAfter(expirationThreshold))
                .map(trip -> {
            // Count available seats (including expired holds)
            long availableCount = trip.getSeats() == null ? 0 :
                trip.getSeats().stream()
                    .filter(seat -> {
                        if (seat.getStatus() == SeatStatus.AVAILABLE) return true;
                        // Expired PENDING seats count as available
                        if (seat.getStatus() == SeatStatus.PENDING && 
                            seat.getLockedUntil() != null && 
                            seat.getLockedUntil().isBefore(now)) return true;
                        return false;
                    })
                    .count();

            return TripDTO.builder()
                    .tripId(trip.getId())
                    .originName(trip.getRoute().getOrigin().getName())
                    .destinationName(trip.getRoute().getDestination().getName())
                    .originCode(trip.getRoute().getOrigin().getProvinceCode())
                    .destinationCode(trip.getRoute().getDestination().getProvinceCode())
                    .departureTime(trip.getDepartureTime())
                    .ticketPrice(trip.getSeats() != null && !trip.getSeats().isEmpty() ? trip.getSeats().get(0).getPrice() : java.math.BigDecimal.ZERO)
                    .busType(trip.getBus().getBusType().getDisplayName())
                    .busLicensePlate(trip.getBus().getLicensePlate())
                    .companyName(trip.getBus().getCompanyName())
                    .totalSeats(trip.getBus().getTotalSeats())
                    .availableSeats((int) availableCount)
                    .status(trip.getStatus().name())
                    .build();
        }).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Trip> findById(Long tripId) {
        return tripRepository.findWithDetailsById(tripId);
    }
}
