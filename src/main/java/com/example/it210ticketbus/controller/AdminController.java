// === FILE: com/example/it210ticketbus/controller/AdminController.java ===
package com.example.it210ticketbus.controller;

import com.example.it210ticketbus.dto.stats.DailyRevenue;
import com.example.it210ticketbus.enums.BusStatus;
import com.example.it210ticketbus.model.Bus;
import com.example.it210ticketbus.repository.BusRepository;
import com.example.it210ticketbus.repository.RouteRepository;
import com.example.it210ticketbus.repository.TicketRepository;
import com.example.it210ticketbus.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

    private final BusRepository busRepository;
    private final RouteRepository routeRepository;
    private final UserRepository userRepository;
    private final TicketRepository ticketRepository;

    @GetMapping
    public String index() {
        return "redirect:/admin/dashboard";
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        // Real statistics from database (Quick at-a-glance metrics)
        model.addAttribute("totalBuses", busRepository.count());
        model.addAttribute("activeBuses", busRepository.countByStatus(BusStatus.ACTIVE));
        model.addAttribute("totalRoutes", routeRepository.count());
        model.addAttribute("totalUsers", userRepository.count());
        
        // Action-oriented data for Dashboard
        model.addAttribute("pendingTicketsCount", ticketRepository.countByStatus(com.example.it210ticketbus.enums.TicketStatus.PENDING));
        model.addAttribute("recentTickets", ticketRepository.findTop10ByOrderByBookedAtDesc());
        
        // Recent buses (last 5 added)
        List<Bus> recentBuses = busRepository.findTop5ByOrderByIdDesc();
        model.addAttribute("recentBuses", recentBuses);
        
        model.addAttribute("pageTitle", "Admin Dashboard");
        return "admin/dashboard";
    }

    @GetMapping("/reports")
    public String reports(Model model) {
        model.addAttribute("dailyRevenue", ticketRepository.getDailyRevenueCurrentMonth());
        model.addAttribute("topDrivers", ticketRepository.getTopDriversCurrentMonth());
        model.addAttribute("topRoutes", ticketRepository.getTopRoutes());
        model.addAttribute("pageTitle", "Báo cáo thống kê");
        return "admin/reports";
    }
}
