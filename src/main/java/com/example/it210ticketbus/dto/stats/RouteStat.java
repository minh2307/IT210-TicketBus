package com.example.it210ticketbus.dto.stats;

import java.math.BigDecimal;

public interface RouteStat {
    Long getRouteId();
    String getRouteName();
    Long getBookingCount();
    BigDecimal getRouteRevenue();
}
