package com.example.it210ticketbus.dto.response;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TicketDTO {
    private Long id;
    private String ticketCode;
    private String passengerName;
    private String passengerPhone;
    private String passengerEmail;
    private BigDecimal totalPrice;
    private String status;
    private LocalDateTime bookedAt;
    private String seatNumber;
    private Integer seatFloor;
    private Long tripId;
    private LocalDateTime departureTime;
    private String originName;
    private String destinationName;
    private String busLicensePlate;
    private String busType;
    private String cancelReason;
    private LocalDateTime confirmedAt;
    private LocalDateTime cancelledAt;
}
