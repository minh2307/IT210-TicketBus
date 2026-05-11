package com.example.it210ticketbus.service.impl;

import com.example.it210ticketbus.dto.response.TicketDTO;
import com.example.it210ticketbus.enums.SeatStatus;
import com.example.it210ticketbus.enums.TicketStatus;
import com.example.it210ticketbus.exception.*;
import com.example.it210ticketbus.model.*;
import com.example.it210ticketbus.repository.SeatRepository;
import com.example.it210ticketbus.repository.TicketRepository;
import com.example.it210ticketbus.repository.UserRepository;
import com.example.it210ticketbus.service.TicketService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TicketServiceImpl implements TicketService {

    private final TicketRepository ticketRepository;
    private final UserRepository userRepository;
    private final SeatRepository seatRepository;
    private final com.example.it210ticketbus.repository.TripRepository tripRepository;

    // ===================== CORE-06: Đặt vé =====================
    @Override
    @Transactional
    public List<TicketDTO> bookTickets(Long userId, List<Long> seatIds) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng"));
        UserProfile profile = user.getProfile();
        
        if (profile == null) {
            System.err.println("[DEBUG] CANH BAO: Nguoi dung " + userId + " khong co profile!");
        } else {
            System.out.println("[DEBUG] Tim thay profile cho user: " + profile.getFullName() + ", email: " + profile.getEmail());
        }
        
        List<Ticket> tickets = new java.util.ArrayList<>();

        for (Long seatId : seatIds) {
            Seat seat = seatRepository.findById(seatId)
                    .orElseThrow(() -> new SeatUnavailableException("Không tìm thấy ghế: " + seatId));

            // B1: Kiểm tra trạng thái ghế — chỉ cho phép PENDING (đã hold trước đó)
            if (seat.getStatus() != SeatStatus.PENDING) {
                throw new SeatUnavailableException("Ghế " + seat.getSeatNumber() + " không khả dụng. Vui lòng chọn lại.");
            }

            // B2: Chuyển trạng thái ghế sang BOOKED
            seat.setStatus(SeatStatus.BOOKED);
            seat.setLockedUntil(null);
            seatRepository.save(seat);

            // B3: Tạo vé
            String ticketCode = "TK-" + System.currentTimeMillis() + "-" + seat.getSeatNumber();

            Ticket ticket = Ticket.builder()
                    .ticketCode(ticketCode)
                    .passengerName(profile != null && profile.getFullName() != null ? profile.getFullName() : user.getUsername())
                    .passengerPhone(profile != null && profile.getPhone() != null ? profile.getPhone() : "N/A")
                    .passengerEmail(profile != null && profile.getEmail() != null ? profile.getEmail() : "N/A")
                    .totalPrice(seat.getPrice())
                    .status(TicketStatus.PENDING)
                    .bookedAt(LocalDateTime.now(java.time.ZoneId.of("Asia/Ho_Chi_Minh")))
                    .seat(seat)
                    .user(user)
                    .build();
            ticketRepository.save(ticket);
            tickets.add(ticket);
        }
        
        List<TicketDTO> result = tickets.stream().map(this::convertToDTO).collect(Collectors.toList());
        
        return result;
    }

    // ===================== CORE-07: Tra cứu vé =====================
    @Override
    @Transactional(readOnly = true)
    public List<TicketDTO> getTicketsByUser(Long userId) {
        return ticketRepository.findByUserId(userId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public TicketDTO getTicketByCode(String code) {
        return ticketRepository.findByTicketCode(code)
                .map(this::convertToDTO)
                .orElse(null);
    }

    @Override
    @Transactional(readOnly = true)
    public TicketDTO lookupTicket(String code, String phone) {
        return ticketRepository.findByTicketCodeAndPhone(code, phone)
                .map(this::convertToDTO)
                .orElse(null);
    }

    // ===================== CORE-08: Staff xác nhận / hủy vé =====================
    @Override
    @Transactional(readOnly = true)
    public List<TicketDTO> getPendingTickets() {
        return ticketRepository.findByStatusOrderByBookedAtDesc(TicketStatus.PENDING).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void confirmTicket(Long ticketId) {
        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new TicketNotFoundException("Không tìm thấy vé với ID: " + ticketId));

        if (ticket.getStatus() != TicketStatus.PENDING) {
            throw new InvalidTicketStatusException("Chỉ có thể xác nhận vé đang ở trạng thái CHỜ THANH TOÁN");
        }

        // Cập nhật vé
        ticket.setStatus(TicketStatus.PAID);
        ticket.setConfirmedAt(LocalDateTime.now(java.time.ZoneId.of("Asia/Ho_Chi_Minh")));
        ticketRepository.save(ticket);

        // Cập nhật ghế sang BOOKED
        Seat seat = ticket.getSeat();
        seat.setStatus(SeatStatus.BOOKED);
        seat.setLockedUntil(null);
        seatRepository.save(seat);
    }

    @Override
    @Transactional
    public void cancelTicketByStaff(Long ticketId, String reason) {
        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new TicketNotFoundException("Không tìm thấy vé với ID: " + ticketId));

        if (ticket.getStatus() != TicketStatus.PENDING) {
            throw new InvalidTicketStatusException("Chỉ có thể hủy vé đang ở trạng thái CHỜ THANH TOÁN");
        }

        // Cập nhật vé
        ticket.setStatus(TicketStatus.CANCELLED);
        ticket.setCancelledAt(LocalDateTime.now(java.time.ZoneId.of("Asia/Ho_Chi_Minh")));
        ticket.setCancelReason(reason != null ? reason : "Staff hủy vé");
        ticketRepository.save(ticket);

        // Trả lại ghế
        Seat seat = ticket.getSeat();
        seat.setStatus(SeatStatus.AVAILABLE);
        seat.setLockedUntil(null);
        seatRepository.save(seat);
    }

    // ===================== CORE-09: Hành khách tự hủy vé =====================
    @Override
    @Transactional
    public void cancelTicketByPassenger(String ticketCode, String phone) {
        // B1: SELECT FOR UPDATE — khóa dòng để tránh race condition
        Ticket ticket = ticketRepository.findByTicketCodeAndPhoneForUpdate(ticketCode, phone)
                .orElseThrow(() -> new TicketNotFoundException("Không tìm thấy vé với mã " + ticketCode + " và số điện thoại " + phone));

        // B2: Kiểm tra trạng thái
        if (ticket.getStatus() != TicketStatus.PENDING) {
            throw new InvalidTicketStatusException("Vé đã được thanh toán hoặc đã hủy trước đó");
        }

        // B3: Kiểm tra khoảng cách giờ khởi hành (>= 12 giờ)
        LocalDateTime now = LocalDateTime.now(java.time.ZoneId.of("Asia/Ho_Chi_Minh"));
        LocalDateTime departure = ticket.getSeat().getTrip().getDepartureTime();
        long hoursLeft = ChronoUnit.HOURS.between(now, departure);
        if (hoursLeft < 12) {
            throw new CancellationNotAllowedException("Không thể hủy vé trong vòng 12 giờ trước giờ khởi hành");
        }

        // B4: Hủy vé
        ticket.setStatus(TicketStatus.CANCELLED);
        ticket.setCancelledAt(now);
        ticket.setCancelReason("Hành khách tự hủy");
        ticketRepository.save(ticket);

        // B5: Trả lại ghế
        Seat seat = ticket.getSeat();
        seat.setStatus(SeatStatus.AVAILABLE);
        seat.setLockedUntil(null);
        seatRepository.save(seat);
    }

    @Override
    @Transactional(readOnly = true)
    public org.springframework.data.domain.Page<TicketDTO> searchTickets(String status, String search, org.springframework.data.domain.Pageable pageable) {
        TicketStatus ticketStatus = null;
        if (status != null && !status.isEmpty() && !status.equalsIgnoreCase("ALL")) {
            try {
                ticketStatus = TicketStatus.valueOf(status.toUpperCase());
            } catch (IllegalArgumentException ignored) {}
        }
        
        String searchTerm = (search != null && !search.trim().isEmpty()) ? search.trim() : null;
        
        return ticketRepository.searchTickets(ticketStatus, searchTerm, pageable)
                .map(this::convertToDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public java.util.Map<String, Object> getStaffDashboardStats() {
        java.time.LocalDateTime now = java.time.LocalDateTime.now(java.time.ZoneId.of("Asia/Ho_Chi_Minh"));
        java.time.LocalDateTime startOfToday = now.toLocalDate().atStartOfDay();
        java.time.LocalDateTime endOfToday = now.toLocalDate().atTime(23, 59, 59);

        long todayTrips = tripRepository.countTripsBetween(startOfToday, endOfToday);
        long totalBookings = ticketRepository.countBookingsSince(startOfToday);
        double revenue = ticketRepository.sumRevenueSince(startOfToday);

        java.util.Map<String, Object> stats = new java.util.HashMap<>();
        stats.put("todayTrips", todayTrips);
        stats.put("totalBookings", totalBookings);
        stats.put("revenue", revenue);
        
        return stats;
    }

    @Override
    @Transactional(readOnly = true)
    public List<TicketDTO> getRecentTickets(int limit) {
        org.springframework.data.domain.Pageable pageable = org.springframework.data.domain.PageRequest.of(0, limit);
        return ticketRepository.findRecentTickets(pageable).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    // ===================== HELPER: Convert Entity → DTO =====================
    private TicketDTO convertToDTO(Ticket ticket) {
        Seat seat = ticket.getSeat();
        Trip trip = seat.getTrip();
        
        return TicketDTO.builder()
                .id(ticket.getId())
                .ticketCode(ticket.getTicketCode())
                .passengerName(ticket.getPassengerName())
                .passengerPhone(ticket.getPassengerPhone())
                .passengerEmail(ticket.getPassengerEmail())
                .totalPrice(ticket.getTotalPrice())
                .status(ticket.getStatus().name())
                .bookedAt(ticket.getBookedAt())
                .seatNumber(seat.getSeatNumber())
                .seatFloor(seat.getFloor())
                .tripId(trip.getId())
                .departureTime(trip.getDepartureTime())
                .originName(trip.getRoute().getOrigin().getName())
                .destinationName(trip.getRoute().getDestination().getName())
                .busLicensePlate(trip.getBus().getLicensePlate())
                .busType(trip.getBus().getBusType().name())
                .cancelReason(ticket.getCancelReason())
                .confirmedAt(ticket.getConfirmedAt())
                .cancelledAt(ticket.getCancelledAt())
                .build();
    }
}
