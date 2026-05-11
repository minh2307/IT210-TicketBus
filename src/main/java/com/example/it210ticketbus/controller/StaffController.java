// === FILE: com/example/it210ticketbus/controller/StaffController.java ===
package com.example.it210ticketbus.controller;

import com.example.it210ticketbus.dto.response.TicketDTO;
import com.example.it210ticketbus.exception.InvalidTicketStatusException;
import com.example.it210ticketbus.exception.TicketNotFoundException;
import com.example.it210ticketbus.service.TicketService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/staff")
@RequiredArgsConstructor
public class StaffController {

    private final TicketService ticketService;

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        java.util.Map<String, Object> stats = ticketService.getStaffDashboardStats();
        List<TicketDTO> recentTickets = ticketService.getRecentTickets(5);
        
        model.addAttribute("todayTrips", stats.get("todayTrips"));
        model.addAttribute("totalBookings", stats.get("totalBookings"));
        model.addAttribute("revenue", stats.get("revenue"));
        model.addAttribute("recentTickets", recentTickets);
        
        model.addAttribute("pageTitle", "Staff Dashboard");
        return "staff/dashboard";
    }

    @GetMapping("/trips")
    public String trips(Model model) {
        model.addAttribute("pageTitle", "Quản lý chuyến xe");
        return "staff/trips";
    }

    @GetMapping("/bookings")
    public String bookings(Model model) {
        return "redirect:/staff/tickets";
    }

    @GetMapping("/tickets")
    public String tickets(@RequestParam(required = false) String status,
                         @RequestParam(required = false) String search,
                         @RequestParam(defaultValue = "0") int page,
                         @RequestParam(defaultValue = "10") int size,
                         Model model) {
        Pageable pageable = PageRequest.of(page, size);
        Page<TicketDTO> ticketPage = ticketService.searchTickets(status, search, pageable);
        
        model.addAttribute("tickets", ticketPage.getContent());
        model.addAttribute("ticketPage", ticketPage);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", ticketPage.getTotalPages());
        model.addAttribute("totalItems", ticketPage.getTotalElements());
        model.addAttribute("pageNumbers", calculatePageNumbers(page, ticketPage.getTotalPages()));
        model.addAttribute("currentStatus", status);
        model.addAttribute("searchTerm", search);
        model.addAttribute("pageTitle", "Quản lý vé");
        return "staff/tickets";
    }

    @PostMapping("/tickets/{id}/confirm")
    public String confirmTicket(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            ticketService.confirmTicket(id);
            redirectAttributes.addFlashAttribute("successMessage", "Xác nhận thanh toán thành công cho vé #" + id);
        } catch (TicketNotFoundException | InvalidTicketStatusException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Lỗi: " + e.getMessage());
        }
        return "redirect:/staff/tickets";
    }

    @PostMapping("/tickets/{id}/cancel")
    public String cancelTicket(@PathVariable Long id, 
                              @RequestParam(required = false) String reason,
                              RedirectAttributes redirectAttributes) {
        try {
            ticketService.cancelTicketByStaff(id, reason);
            redirectAttributes.addFlashAttribute("successMessage", "Đã hủy vé #" + id + " thành công");
        } catch (TicketNotFoundException | InvalidTicketStatusException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Lỗi: " + e.getMessage());
        }
        return "redirect:/staff/tickets";
    }

    @GetMapping("/checkin")
    public String checkin(Model model) {
        model.addAttribute("pageTitle", "Check-in");
        return "staff/checkin";
    }

    @GetMapping("/reports")
    public String reports(Model model) {
        model.addAttribute("pageTitle", "Báo cáo");
        return "staff/reports";
    }

    private List<Integer> calculatePageNumbers(int currentPage, int totalPages) {
        List<Integer> pages = new ArrayList<>();
        if (totalPages <= 7) {
            for (int i = 0; i < totalPages; i++) {
                pages.add(i);
            }
        } else {
            // Luôn thêm trang đầu
            pages.add(0);

            int start = Math.max(1, currentPage - 2);
            int end = Math.min(totalPages - 2, currentPage + 2);

            // Xử lý dấu ba chấm đầu tiên
            if (start > 1) {
                pages.add(-1); // -1 đại diện cho dấu ba chấm
            }

            // Thêm các trang ở giữa
            for (int i = start; i <= end; i++) {
                pages.add(i);
            }

            // Xử lý dấu ba chấm cuối cùng
            if (end < totalPages - 2) {
                pages.add(-1);
            }

            // Luôn thêm trang cuối
            pages.add(totalPages - 1);
        }
        return pages;
    }
}
