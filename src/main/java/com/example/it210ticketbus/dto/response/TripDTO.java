package com.example.it210ticketbus.dto.response;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TripDTO {
    private Long tripId;
    private String originName;
    private String destinationName;
    private String originCode;
    private String destinationCode;
    private LocalDateTime departureTime;
    private BigDecimal ticketPrice;
    private String busType;
    private String busLicensePlate;
    private String companyName;
    private Integer totalSeats;
    private Integer availableSeats;
    private String status;
}
