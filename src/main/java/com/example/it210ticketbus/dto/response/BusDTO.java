package com.example.it210ticketbus.dto.response;

import com.example.it210ticketbus.enums.BusStatus;
import com.example.it210ticketbus.enums.BusType;
import lombok.*;

import java.time.LocalDateTime;

/**
 * DTO for Bus response data (CORE-04)
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BusDTO {
    
    private Long id;
    private String licensePlate;
    private BusType busType;
    private Integer totalSeats;
    private String companyName;
    private String driverName;
    private String driverPhone;
    private BusStatus status;
    private java.util.List<com.example.it210ticketbus.model.Route> routes;
    private java.util.List<Long> routeIds;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
