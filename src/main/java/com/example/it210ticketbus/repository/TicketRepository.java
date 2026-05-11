package com.example.it210ticketbus.repository;

import com.example.it210ticketbus.dto.stats.DailyRevenue;
import com.example.it210ticketbus.dto.stats.DriverRevenue;
import com.example.it210ticketbus.dto.stats.RouteStat;
import com.example.it210ticketbus.enums.TicketStatus;
import com.example.it210ticketbus.model.Ticket;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TicketRepository extends JpaRepository<Ticket, Long> {
    Optional<Ticket> findByTicketCode(String ticketCode);
    List<Ticket> findByUserId(Long userId);
    
    List<Ticket> findByStatusOrderByBookedAtDesc(TicketStatus status);
    
    @Query("SELECT t FROM Ticket t ORDER BY t.bookedAt DESC")
    List<Ticket> findRecentTickets(org.springframework.data.domain.Pageable pageable);
    
    List<Ticket> findTop10ByOrderByBookedAtDesc();
    
    long countByStatus(TicketStatus status);
    
    @Query("SELECT t FROM Ticket t WHERE t.ticketCode = :code AND t.passengerPhone = :phone")
    Optional<Ticket> findByTicketCodeAndPhone(@Param("code") String code, @Param("phone") String phone);
    
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT t FROM Ticket t WHERE t.ticketCode = :code AND t.passengerPhone = :phone")
    Optional<Ticket> findByTicketCodeAndPhoneForUpdate(@Param("code") String code, @Param("phone") String phone);

    @Query("SELECT t FROM Ticket t WHERE " +
           "(:status IS NULL OR t.status = :status) AND " +
           "(:search IS NULL OR LOWER(t.ticketCode) LIKE LOWER(CONCAT('%', :search, '%')) " +
           "OR LOWER(t.passengerName) LIKE LOWER(CONCAT('%', :search, '%')) " +
           "OR LOWER(t.passengerPhone) LIKE LOWER(CONCAT('%', :search, '%'))) " +
           "ORDER BY t.bookedAt DESC")
    org.springframework.data.domain.Page<Ticket> searchTickets(@Param("status") TicketStatus status, @Param("search") String search, org.springframework.data.domain.Pageable pageable);

    // 1. Thống kê doanh thu theo từng ngày trong tháng hiện tại
    @Query(value = "SELECT DATE(booked_at) as reportDate, COALESCE(SUM(total_price), 0) as totalRevenue, COUNT(id) as successRides " +
                   "FROM tickets WHERE status = 'PAID' " +
                   "AND booked_at >= DATE_FORMAT(NOW(), '%Y-%m-01') " +
                   "AND booked_at < DATE_FORMAT(DATE_ADD(NOW(), INTERVAL 1 MONTH), '%Y-%m-01') " +
                   "GROUP BY DATE(booked_at) ORDER BY reportDate ASC", nativeQuery = true)
    List<DailyRevenue> getDailyRevenueCurrentMonth();

    // 2. Top 5 tài xế doanh thu cao nhất tháng này
    @Query(value = "SELECT 0 as driverId, b.driver_name as driverName, " +
                   "COALESCE(SUM(t.total_price), 0) as driverRevenue, COUNT(t.id) as totalCompletedRides " +
                   "FROM tickets t JOIN seats s ON t.seat_id = s.id " +
                   "JOIN trips tr ON s.trip_id = tr.id " +
                   "JOIN buses b ON tr.bus_id = b.id " +
                   "WHERE t.status = 'PAID' AND t.booked_at >= DATE_FORMAT(NOW(), '%Y-%m-01') " +
                   "GROUP BY b.driver_name ORDER BY driverRevenue DESC LIMIT 5", nativeQuery = true)
    List<DriverRevenue> getTopDriversCurrentMonth();

    // 3. Top 10 tuyến đường được đặt nhiều nhất
    @Query(value = "SELECT tr.route_id as routeId, CONCAT(lo.name, ' - ', ld.name) as routeName, " +
                   "COUNT(t.id) as bookingCount, COALESCE(SUM(CASE WHEN t.status = 'PAID' THEN t.total_price ELSE 0 END), 0) as routeRevenue " +
                   "FROM tickets t JOIN seats s ON t.seat_id = s.id " +
                   "JOIN trips tr ON s.trip_id = tr.id " +
                   "JOIN routes r ON tr.route_id = r.id " +
                   "JOIN locations lo ON r.origin_id = lo.id " +
                   "JOIN locations ld ON r.destination_id = ld.id " +
                   "GROUP BY tr.route_id, lo.name, ld.name ORDER BY bookingCount DESC LIMIT 10", nativeQuery = true)
    List<RouteStat> getTopRoutes();

    @Query("SELECT COALESCE(SUM(t.totalPrice), 0) FROM Ticket t WHERE t.status = 'PAID' AND t.bookedAt >= :since")
    double sumRevenueSince(@Param("since") LocalDateTime since);

    @Query("SELECT COUNT(t) FROM Ticket t WHERE t.bookedAt >= :since")
    long countBookingsSince(@Param("since") LocalDateTime since);
}
