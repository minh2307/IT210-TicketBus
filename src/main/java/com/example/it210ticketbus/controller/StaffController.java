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

import java.util.List;

@Controller
@RequestMapping("/staff")
@RequiredArgsConstructor
public class StaffController {

    private final TicketService ticketService;

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
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
                         Model model) {
        List<TicketDTO> tickets = ticketService.searchTickets(status, search);
        model.addAttribute("tickets", tickets);
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

}
