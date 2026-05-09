package com.example.it210ticketbus.model;

import com.example.it210ticketbus.enums.TicketStatus;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;


@Entity
@Table(name = "tickets")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder //
public class Ticket {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "ticket_code", nullable = false, unique = true, length = 50)
    private String ticketCode;
    
    @Column(name = "passenger_name", nullable = false, length = 100)
    private String passengerName;
    
    @Column(name = "passenger_phone", nullable = false, length = 20)
    private String passengerPhone;
    
    @Column(name = "passenger_email", nullable = false, length = 100)
    private String passengerEmail;
    
    @Column(name = "total_price", nullable = false, precision = 10, scale = 2)
    private BigDecimal totalPrice;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TicketStatus status;
    
    @Column(name = "booked_at", nullable = false)
    private LocalDateTime bookedAt;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "seat_id", nullable = false)
    private Seat seat;
    
    @ManyToOne(fetch = FetchType.LAZY, optional = true)
    @JoinColumn(name = "user_id")
    private User user;
    
    @OneToOne(mappedBy = "ticket", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Payment payment;
    
    @Column(name = "confirmed_at")
    private LocalDateTime confirmedAt;
    
    @Column(name = "cancelled_at")
    private LocalDateTime cancelledAt;
    
    @Column(name = "cancel_reason", length = 500)
    private String cancelReason;
}
