package com.example.it210ticketbus.dto.stats;

import java.math.BigDecimal;
import java.time.LocalDate;

public interface DailyRevenue {
    LocalDate getReportDate();
    BigDecimal getTotalRevenue();
    Long getSuccessRides();
}
