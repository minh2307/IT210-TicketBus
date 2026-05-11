package com.example.it210ticketbus.controller;

import com.example.it210ticketbus.dto.request.BusRequest;
import com.example.it210ticketbus.dto.response.BusDTO;
import com.example.it210ticketbus.enums.BusStatus;
import com.example.it210ticketbus.enums.BusType;
import com.example.it210ticketbus.model.Route;
import com.example.it210ticketbus.service.BusService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;

import java.util.List;

@Controller
@RequestMapping("/admin/buses")
@RequiredArgsConstructor
public class AdminBusController {

    private final BusService busService;
    private final com.example.it210ticketbus.repository.RouteRepository routeRepository;

    @InitBinder
    public void initBinder(WebDataBinder binder) {
        binder.registerCustomEditor(String.class, new StringTrimmerEditor(true));
    }

    @GetMapping
    public String listBuses(@RequestParam(defaultValue = "0") int page,
                            @RequestParam(defaultValue = "10") int size,
                            @RequestParam(required = false) String keyword,
                            @RequestParam(required = false) String status,
                            Model model) {

        // Chuyển đổi thủ công an toàn để tránh lỗi IllegalArgumentException
        BusStatus statusEnum = null;
        if (status != null && !status.isEmpty()) {
            try {
                statusEnum = BusStatus.valueOf(status.toUpperCase());
            } catch (IllegalArgumentException e) {}
        }

        Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());

        // Truyền các biến filter vào service (loại bỏ lọc theo type)
        Page<BusDTO> buses = busService.getAllBuses(keyword, statusEnum, null, pageable);

        model.addAttribute("buses", buses.getContent());
        model.addAttribute("keyword", keyword);
        model.addAttribute("status", status);
        model.addAttribute("busStatuses", BusStatus.values());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", buses.getTotalPages());

        return "admin/buses/list";
    }

    @GetMapping("/new")
    public String createBusForm(Model model) {
        model.addAttribute("busDto", new BusRequest());
        model.addAttribute("busTypes", BusType.values());
        model.addAttribute("busStatuses", BusStatus.values());
        model.addAttribute("allRoutes", routeRepository.findAllWithLocations());
        model.addAttribute("isEdit", false);
        return "admin/buses/form";
    }

    @PostMapping("/new")
    public String createBus(@Valid @ModelAttribute("busDto") BusRequest busRequest,
                           BindingResult bindingResult,
                           RedirectAttributes redirectAttributes,
                           Model model) {
        
        if (bindingResult.hasErrors()) {
            model.addAttribute("busTypes", BusType.values());
            model.addAttribute("busStatuses", BusStatus.values());
            model.addAttribute("allRoutes", routeRepository.findAllWithLocations());
            model.addAttribute("isEdit", false);
            return "admin/buses/form";
        }

        try {
            busService.createBus(busRequest);
            redirectAttributes.addAttribute("success", "created");
            return "redirect:/admin/buses";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("busTypes", BusType.values());
            model.addAttribute("busStatuses", BusStatus.values());
            model.addAttribute("allRoutes", routeRepository.findAllWithLocations());
            model.addAttribute("isEdit", false);
            return "admin/buses/form";
        }
    }

    @GetMapping("/edit/{id}")
    public String editBusForm(@PathVariable Long id, Model model) {
        try {
            BusDTO bus = busService.getBusById(id);
            // Chuyển đổi từ BusDTO sang BusRequest để form sử dụng đồng nhất
            BusRequest busRequest = BusRequest.builder()
                    .id(bus.getId())
                    .licensePlate(bus.getLicensePlate())
                    .busType(bus.getBusType())
                    .totalSeats(bus.getTotalSeats())
                    .companyName(bus.getCompanyName())
                    .driverName(bus.getDriverName())
                    .driverPhone(bus.getDriverPhone())
                    .status(bus.getStatus())
                    .routeIds(bus.getRouteIds())
                    .build();
            
            model.addAttribute("busDto", busRequest);
            model.addAttribute("busTypes", BusType.values());
            model.addAttribute("busStatuses", BusStatus.values());
            model.addAttribute("allRoutes", routeRepository.findAllWithLocations());
            model.addAttribute("isEdit", true);
            return "admin/buses/form";
        } catch (Exception e) {
            return "redirect:/admin/buses?error=" + e.getMessage();
        }
    }

    @PostMapping("/edit/{id}")
    public String updateBus(@PathVariable Long id,
                           @Valid @ModelAttribute("busDto") BusRequest busRequest,
                           BindingResult bindingResult,
                           RedirectAttributes redirectAttributes,
                           Model model) {
        
        if (bindingResult.hasErrors()) {
            model.addAttribute("busTypes", BusType.values());
            model.addAttribute("busStatuses", BusStatus.values());
            model.addAttribute("allRoutes", routeRepository.findAllWithLocations());
            model.addAttribute("isEdit", true);
            return "admin/buses/form";
        }

        try {
            busService.updateBus(id, busRequest);
            redirectAttributes.addAttribute("success", "updated");
            return "redirect:/admin/buses";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("busTypes", BusType.values());
            model.addAttribute("busStatuses", BusStatus.values());
            model.addAttribute("allRoutes", routeRepository.findAllWithLocations());
            model.addAttribute("isEdit", true);
            return "admin/buses/form";
        }
    }

    @PostMapping("/delete/{id}")
    public String deleteBus(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            busService.deleteBus(id);
            redirectAttributes.addAttribute("success", "deleted");
        } catch (Exception e) {
            redirectAttributes.addAttribute("error", e.getMessage());
        }
        return "redirect:/admin/buses";
    }

    @GetMapping("/view/{id}")
    public String viewBus(@PathVariable Long id, Model model) {
        try {
            BusDTO bus = busService.getBusById(id);
            List<Route> routes = routeRepository.findDistinctRoutesByBusId(id);
            model.addAttribute("bus", bus);
            model.addAttribute("routes", routes);
            return "admin/buses/detail";
        } catch (Exception e) {
            return "redirect:/admin/buses?error=" + e.getMessage();
        }
    }
}
