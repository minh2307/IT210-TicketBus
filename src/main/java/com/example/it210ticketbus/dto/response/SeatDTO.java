package com.example.it210ticketbus.dto.response;

import com.example.it210ticketbus.enums.SeatStatus;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SeatDTO {
    private Long seatId;
    private String seatNumber;
    private Integer floor;
    private String status; // AVAILABLE, PENDING, BOOKED
    private java.math.BigDecimal price;
    private java.time.LocalDateTime lockedUntil;
}
