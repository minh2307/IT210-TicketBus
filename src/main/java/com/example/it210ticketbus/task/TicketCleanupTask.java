package com.example.it210ticketbus.task;

import com.example.it210ticketbus.enums.SeatStatus;
import com.example.it210ticketbus.enums.TicketStatus;
import com.example.it210ticketbus.model.Seat;
import com.example.it210ticketbus.model.Ticket;
import com.example.it210ticketbus.repository.SeatRepository;
import com.example.it210ticketbus.repository.TicketRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class TicketCleanupTask {

    private final TicketRepository ticketRepository;
    private final SeatRepository seatRepository;

    /**
     * Tác vụ chạy định kỳ mỗi 10 phút (Cron Job) để dọn dẹp vé quá hạn.
     */
    @Scheduled(cron = "0 */10 * * * *")
    @Transactional
    public void cleanupExpiredTickets() {
        LocalDateTime threshold = LocalDateTime.now().minusMinutes(30);
        log.info(">>> [CRON] Bắt đầu quét vé PENDING quá 30 phút. Ngưỡng: {}", threshold);
        
        try {
            List<Ticket> pendingTickets = ticketRepository.findByStatusAndBookedAtBefore(TicketStatus.PENDING, threshold);
            
            if (pendingTickets.isEmpty()) {
                log.info(">>> [CRON] Không có vé nào cần hủy.");
                return;
            }
            
            log.info(">>> [CRON] Tìm thấy {} vé quá hạn cần xử lý.", pendingTickets.size());
            
            for (Ticket ticket : pendingTickets) {
                // 1. Cập nhật tickets.status = 'CANCELLED'
                ticket.setStatus(TicketStatus.CANCELLED);
                ticket.setCancelledAt(LocalDateTime.now());
                ticket.setCancelReason("Tự động hủy do quá 30 phút chưa thanh toán (Mailgun Automation)");
                ticketRepository.save(ticket);
                
                // 2. Cập nhật seats.status = 'AVAILABLE' để giải phóng ghế
                Seat seat = ticket.getSeat();
                if (seat != null) {
                    seat.setStatus(SeatStatus.AVAILABLE);
                    seat.setLockedUntil(null);
                    seatRepository.save(seat);
                    log.info(">>> [CRON] Đã hủy vé {} và giải phóng ghế {}", ticket.getTicketCode(), seat.getSeatNumber());
                }
            }
            
            log.info(">>> [CRON] Hoàn tất dọn dẹp vé quá hạn.");
            
        } catch (Exception e) {
            log.error(">>> [CRON] LỖI khi thực hiện dọn dẹp: {}", e.getMessage(), e);
            // Transactional đảm bảo Rollback nếu có lỗi
            throw e; 
        }
    }
}
