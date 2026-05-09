package com.example.it210ticketbus.controller;

import com.example.it210ticketbus.model.Location;
import com.example.it210ticketbus.model.Route;
import com.example.it210ticketbus.repository.LocationRepository;
import com.example.it210ticketbus.repository.RouteRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.util.List;

@Controller
@RequestMapping("/admin/routes")
@RequiredArgsConstructor
public class AdminRouteController {

    private final RouteRepository routeRepository;
    private final LocationRepository locationRepository;

    @GetMapping
    public String listRoutes(Model model) {
        List<Route> routes = routeRepository.findAllWithLocations();
        model.addAttribute("routes", routes);
        return "admin/routes/list";
    }

    @GetMapping("/new")
    public String createRouteForm(Model model) {
        model.addAttribute("route", new Route());
        model.addAttribute("locations", locationRepository.findAll());
        return "admin/routes/form";
    }

    @PostMapping("/save")
    public String saveRoute(@Valid @ModelAttribute("route") Route route,
                           BindingResult bindingResult,
                           RedirectAttributes redirectAttributes,
                           Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("locations", locationRepository.findAll());
            return "admin/routes/form";
        }

        // Validate origin != destination
        if (route.getOrigin() != null && route.getDestination() != null 
            && route.getOrigin().getId().equals(route.getDestination().getId())) {
            model.addAttribute("error", "Điểm đi và điểm đến không được giống nhau");
            model.addAttribute("locations", locationRepository.findAll());
            return "admin/routes/form";
        }

        try {
            routeRepository.save(route);
            redirectAttributes.addAttribute("success", "created");
            return "redirect:/admin/routes";
        } catch (Exception e) {
            model.addAttribute("error", "Lỗi: " + e.getMessage());
            model.addAttribute("locations", locationRepository.findAll());
            return "admin/routes/form";
        }
    }

    @GetMapping("/edit/{id}")
    public String editRouteForm(@PathVariable Long id, Model model) {
        Route route = routeRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy tuyến đường với ID: " + id));
        model.addAttribute("route", route);
        model.addAttribute("locations", locationRepository.findAll());
        return "admin/routes/form";
    }

    @PostMapping("/update/{id}")
    public String updateRoute(@PathVariable Long id,
                             @Valid @ModelAttribute("route") Route route,
                             BindingResult bindingResult,
                             RedirectAttributes redirectAttributes,
                             Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("locations", locationRepository.findAll());
            return "admin/routes/form";
        }

        // Validate origin != destination
        if (route.getOrigin() != null && route.getDestination() != null 
            && route.getOrigin().getId().equals(route.getDestination().getId())) {
            model.addAttribute("error", "Điểm đi và điểm đến không được giống nhau");
            model.addAttribute("locations", locationRepository.findAll());
            return "admin/routes/form";
        }

        try {
            route.setId(id);
            routeRepository.save(route);
            redirectAttributes.addAttribute("success", "updated");
            return "redirect:/admin/routes";
        } catch (Exception e) {
            model.addAttribute("error", "Lỗi: " + e.getMessage());
            model.addAttribute("locations", locationRepository.findAll());
            return "admin/routes/form";
        }
    }

    @PostMapping("/delete/{id}")
    public String deleteRoute(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            routeRepository.deleteById(id);
            redirectAttributes.addAttribute("success", "deleted");
        } catch (Exception e) {
            redirectAttributes.addAttribute("error", "Không thể xóa tuyến đường này: " + e.getMessage());
        }
        return "redirect:/admin/routes";
    }
}
