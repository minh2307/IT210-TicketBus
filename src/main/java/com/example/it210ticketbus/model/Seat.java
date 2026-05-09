package com.example.it210ticketbus.model;

import com.example.it210ticketbus.enums.SeatStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;


@Entity
@Table(name = "seats")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Seat {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "seat_number", nullable = false, length = 10)
    private String seatNumber;
    
    @Column(nullable = false)
    @Builder.Default
    private Integer floor = 1;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SeatStatus status;
    
    @Column(name = "locked_until")
    private LocalDateTime lockedUntil;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "trip_id", nullable = false)
    private Trip trip;

    @Column(precision = 10, scale = 2)
    private java.math.BigDecimal price;

    @Version
    private Long version;
}
