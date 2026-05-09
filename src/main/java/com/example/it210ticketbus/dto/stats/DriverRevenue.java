package com.example.it210ticketbus.dto.stats;

import java.math.BigDecimal;

public interface DriverRevenue {
    Long getDriverId();
    String getDriverName();
    BigDecimal getDriverRevenue();
    Long getTotalCompletedRides();
}
