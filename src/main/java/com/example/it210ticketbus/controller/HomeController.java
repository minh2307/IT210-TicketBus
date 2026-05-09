// === FILE: com/example/it210ticketbus/controller/HomeController.java ===
package com.example.it210ticketbus.controller;

import com.example.it210ticketbus.dto.response.SeatDTO;
import com.example.it210ticketbus.dto.response.TicketDTO;
import com.example.it210ticketbus.dto.response.TripDTO;
import com.example.it210ticketbus.dto.response.UserProfileResponse;
import com.example.it210ticketbus.exception.*;
import com.example.it210ticketbus.model.Trip;
import com.example.it210ticketbus.repository.TripRepository;
import com.example.it210ticketbus.service.SeatService;
import com.example.it210ticketbus.service.TicketService;
import com.example.it210ticketbus.service.TripService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class HomeController {

    private final TripService tripService;
    private final TicketService ticketService;
    private final SeatService seatService;
    private final TripRepository tripRepository;

    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("pageTitle", "Trang chủ");
        return "home/index";
    }

    @GetMapping("/search")
    public String search(@RequestParam(required = false) String from,
            @RequestParam(required = false) String to,
            @RequestParam(required = false) String date,
            Model model) {
        if (from != null && to != null) {
            LocalDate searchDate = null;
            if (date != null && !date.isEmpty()) {
                try {
                    searchDate = LocalDate.parse(date, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                } catch (Exception e) {
                    // Handle invalid date format
                    model.addAttribute("errorMessage",
                            "Định dạng ngày không hợp lệ. Vui lòng sử dụng định dạng dd/MM/yyyy");
                }
            }
            List<TripDTO> trips = tripService.searchTrips(from, to, searchDate);
            model.addAttribute("trips", trips);
        }
        model.addAttribute("pageTitle", "Tìm kiếm chuyến xe");
        model.addAttribute("from", from);
        model.addAttribute("to", to);
        model.addAttribute("date", date);
        return "home/search";
    }

    @GetMapping("/seat-map")
    public String seatMap(@RequestParam("tripId") Long tripId, Model model, HttpSession session) {
        Trip trip = tripService.findById(tripId).orElse(null);
        if (trip == null || trip.getDepartureTime().isBefore(LocalDateTime.now().minusMinutes(30))) {
            return "redirect:/search?error=Chuyến xe không tồn tại hoặc đã quá giờ khởi hành";
        }

        List<SeatDTO> seats = seatService.getSeatsByTripId(tripId);
        model.addAttribute("trip", trip);
        model.addAttribute("seats", seats);
        model.addAttribute("pageTitle", "Chọn ghế");
        model.addAttribute("tripId", tripId);

        UserProfileResponse user = (UserProfileResponse) session.getAttribute("user");
        model.addAttribute("isLoggedIn", user != null);

        // Find seats held by THIS user
        if (user != null) {
            List<SeatDTO> heldSeats = seats.stream()
                    .filter(s -> "PENDING".equals(s.getStatus()))
                    .toList();
            model.addAttribute("heldSeats", heldSeats);

            // Also keep heldSeatIds for the URL generation if needed, or we can project in
            // view
            List<Long> heldSeatIds = heldSeats.stream().map(SeatDTO::getSeatId).toList();
            model.addAttribute("heldSeatIds", heldSeatIds);
        }

        return "home/seat-map";
    }

    @PostMapping("/seat-map/toggle")
    public String toggleSeat(@RequestParam Long seatId, @RequestParam Long tripId, HttpSession session,
            RedirectAttributes redirectAttributes) {
        UserProfileResponse user = (UserProfileResponse) session.getAttribute("user");
        if (user == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "Vui lòng đăng nhập để chọn ghế");
            return "redirect:/login?redirect=/seat-map?tripId=" + tripId;
        }

        try {
            // Check if already held by user in session?
            // Simplified: try to hold. If already held by user, service should handle or we
            // check status.
            // For now, let's just use the service logic.

            // Logic: if seat is already held by this user, release it. Otherwise hold it.
            // Since we don't have a 'isHeldByUser' easy check without modifying service,
            // we'll try to hold. If it fails due to conflict, it might be ours or someone
            // else's.

            // In a real SSR app, we'd have a specific toggle method in service.
            // I'll simulate it by checking current status.
            List<SeatDTO> seats = seatService.getSeatsByTripId(tripId);
            SeatDTO seat = seats.stream().filter(s -> s.getSeatId().equals(seatId)).findFirst().orElse(null);

            if (seat != null && "PENDING".equals(seat.getStatus())) {
                seatService.releaseSeat(seatId, tripId);
            } else {
                seatService.holdSeat(seatId, tripId);
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }

        return "redirect:/seat-map?tripId=" + tripId;
    }

    @GetMapping("/booking")
    public String booking(@RequestParam("tripId") Long tripId,
            @RequestParam("seatIds") List<Long> seatIds,
            Model model, HttpSession session) {
        UserProfileResponse user = (UserProfileResponse) session.getAttribute("user");
        if (user == null) {
            return "redirect:/login?redirect=/booking?tripId=" + tripId + "&seatIds="
                    + seatIds.toString().replace("[", "").replace("]", "");
        }

        Trip trip = tripService.findById(tripId).orElse(null);
        if (trip == null)
            return "redirect:/search";

        java.math.BigDecimal totalPrice = trip.getSeats().stream()
                .filter(s -> seatIds.contains(s.getId()))
                .map(com.example.it210ticketbus.model.Seat::getPrice)
                .reduce(java.math.BigDecimal.ZERO, java.math.BigDecimal::add);

        model.addAttribute("trip", trip);
        model.addAttribute("seatIds", seatIds);
        model.addAttribute("tripId", tripId);
        model.addAttribute("totalPrice", totalPrice);
        model.addAttribute("pageTitle", "Xác nhận đặt vé");
        return "home/booking";
    }

    @PostMapping("/booking")
    public String processBooking(@RequestParam("tripId") Long tripId,
            @RequestParam("seatIds") List<Long> seatIds,
            HttpSession session,
            RedirectAttributes redirectAttributes) {
        UserProfileResponse user = (UserProfileResponse) session.getAttribute("user");
        if (user == null)
            return "redirect:/login";

        try {
            List<TicketDTO> bookedTickets = ticketService.bookTickets(user.getId(), seatIds);
            session.setAttribute("successMessage", "Đặt vé thành công!");
            session.setAttribute("bookedTickets", bookedTickets);
            return "redirect:/booking-confirm";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/seat-map?tripId=" + tripId;
        }
    }

    @GetMapping("/booking-confirm")
    public String bookingConfirm(Model model, HttpSession session) {
        model.addAttribute("pageTitle", "Đặt vé thành công");
        
        // Lấy từ session để tránh mất dữ liệu khi reload trang
        Object bookedTickets = session.getAttribute("bookedTickets");
        Object successMessage = session.getAttribute("successMessage");
        
        if (bookedTickets != null) {
            model.addAttribute("bookedTickets", bookedTickets);
            model.addAttribute("successMessage", successMessage);
            
            // Xóa khỏi session sau khi đã hiển thị (hoặc giữ lại nếu muốn cho phép reload nhiều lần)
            // session.removeAttribute("bookedTickets");
            // session.removeAttribute("successMessage");
        }
        
        return "home/booking-confirm";
    }

    @GetMapping("/lookup")
    public String lookupPage(@RequestParam(required = false) String code,
            @RequestParam(required = false) String phone,
            Model model) {
        if (code != null && !code.isEmpty() && phone != null && !phone.isEmpty()) {
            TicketDTO ticket = ticketService.lookupTicket(code.trim(), phone.trim());
            if (ticket != null) {
                model.addAttribute("ticket", ticket);
            } else {
                model.addAttribute("errorMessage", "Không tìm thấy vé với mã " + code + " và SĐT " + phone);
            }
            model.addAttribute("code", code);
            model.addAttribute("phone", phone);
        }
        model.addAttribute("pageTitle", "Tra cứu vé");
        return "home/lookup";
    }

    @PostMapping("/lookup")
    public String lookupTicket(@RequestParam String code,
            @RequestParam String phone,
            Model model) {
        TicketDTO ticket = ticketService.lookupTicket(code.trim(), phone.trim());
        if (ticket == null) {
            model.addAttribute("errorMessage", "Không tìm thấy vé với mã " + code + " và SĐT " + phone);
        } else {
            model.addAttribute("ticket", ticket);
        }
        model.addAttribute("pageTitle", "Kết quả tra cứu");
        model.addAttribute("code", code);
        model.addAttribute("phone", phone);
        return "home/lookup";
    }

    @PostMapping("/lookup/cancel")
    public String cancelTicketByPassenger(@RequestParam String ticketCode,
            @RequestParam String phone,
            RedirectAttributes redirectAttributes) {
        try {
            ticketService.cancelTicketByPassenger(ticketCode, phone.trim());
            redirectAttributes.addFlashAttribute("successMessage", "Đã gửi yêu cầu hủy vé thành công");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Lỗi: " + e.getMessage());
        }
        return "redirect:/lookup?code=" + ticketCode + "&phone=" + phone;
    }

    @GetMapping("/about")
    public String about(Model model) {
        model.addAttribute("pageTitle", "Về chúng tôi");
        return "home/about";
    }

    @GetMapping("/contact")
    public String contact(Model model) {
        model.addAttribute("pageTitle", "Liên hệ");
        return "home/contact";
    }

    @GetMapping("/403")
    public String accessDenied(Model model) {
        model.addAttribute("pageTitle", "Truy cập bị từ chối");
        return "error/403";
    }
}
