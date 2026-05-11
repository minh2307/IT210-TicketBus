package com.example.it210ticketbus.service;

import com.example.it210ticketbus.dto.response.TicketDTO;
import java.util.List;

public interface TicketService {
    // CORE-06: Đặt vé
    List<TicketDTO> bookTickets(Long userId, List<Long> seatIds);
    
    // CORE-07: Tra cứu vé
    List<TicketDTO> getTicketsByUser(Long userId);
    TicketDTO getTicketByCode(String code);
    TicketDTO lookupTicket(String code, String phone);
    
    // CORE-08: Staff xác nhận / hủy vé
    List<TicketDTO> getPendingTickets();
    void confirmTicket(Long ticketId);
    void cancelTicketByStaff(Long ticketId, String reason);
    
    // CORE-09: Hành khách tự hủy vé
    void cancelTicketByPassenger(String ticketCode, String phone);

    // Bổ sung: Tìm kiếm vé cho Staff
    org.springframework.data.domain.Page<TicketDTO> searchTickets(String status, String search, org.springframework.data.domain.Pageable pageable);

    // Bổ sung cho Dashboard
    java.util.Map<String, Object> getStaffDashboardStats();
    List<TicketDTO> getRecentTickets(int limit);
}
