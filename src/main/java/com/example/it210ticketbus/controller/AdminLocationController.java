package com.example.it210ticketbus.controller;

import com.example.it210ticketbus.model.Location;
import com.example.it210ticketbus.repository.LocationRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/locations")
@RequiredArgsConstructor
public class AdminLocationController {

    private final LocationRepository locationRepository;

    @GetMapping
    public String listLocations(Model model) {
        model.addAttribute("locations", locationRepository.findAll());
        return "admin/locations/list";
    }

    @GetMapping("/new")
    public String createLocationForm(Model model) {
        model.addAttribute("location", new Location());
        return "admin/locations/form";
    }

    @PostMapping("/save")
    public String saveLocation(@Valid @ModelAttribute("location") Location location,
                              BindingResult bindingResult,
                              RedirectAttributes redirectAttributes,
                              Model model) {
        if (bindingResult.hasErrors()) {
            return "admin/locations/form";
        }

        try {
            locationRepository.save(location);
            redirectAttributes.addAttribute("success", "created");
            return "redirect:/admin/locations";
        } catch (Exception e) {
            model.addAttribute("error", "Lỗi: " + e.getMessage());
            return "admin/locations/form";
        }
    }

    @GetMapping("/edit/{id}")
    public String editLocationForm(@PathVariable Long id, Model model) {
        Location location = locationRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy địa điểm với ID: " + id));
        model.addAttribute("location", location);
        return "admin/locations/form";
    }

    @PostMapping("/update/{id}")
    public String updateLocation(@PathVariable Long id,
                                @Valid @ModelAttribute("location") Location location,
                                BindingResult bindingResult,
                                RedirectAttributes redirectAttributes,
                                Model model) {
        if (bindingResult.hasErrors()) {
            return "admin/locations/form";
        }

        try {
            location.setId(id);
            locationRepository.save(location);
            redirectAttributes.addAttribute("success", "updated");
            return "redirect:/admin/locations";
        } catch (Exception e) {
            model.addAttribute("error", "Lỗi: " + e.getMessage());
            return "admin/locations/form";
        }
    }

    @PostMapping("/delete/{id}")
    public String deleteLocation(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            locationRepository.deleteById(id);
            redirectAttributes.addAttribute("success", "deleted");
        } catch (Exception e) {
            redirectAttributes.addAttribute("error", "Không thể xóa địa điểm này: " + e.getMessage());
        }
        return "redirect:/admin/locations";
    }
}
