// === FILE: com/example/it210ticketbus/controller/AdminBusController.java ===
package com.example.it210ticketbus.controller;

import com.example.it210ticketbus.dto.request.BusRequest;
import com.example.it210ticketbus.dto.response.BusDTO;
import com.example.it210ticketbus.enums.BusStatus;
import com.example.it210ticketbus.enums.BusType;
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

@Controller
@RequestMapping("/admin/buses")
@RequiredArgsConstructor
public class AdminBusController {

    private final BusService busService;

    @GetMapping
    public String listBuses(@RequestParam(defaultValue = "0") int page,
                           @RequestParam(defaultValue = "10") int size,
                           @RequestParam(required = false) String keyword,
                           @RequestParam(required = false) String status,
                           Model model) {
        
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());
        Page<BusDTO> buses = busService.getAllBuses(keyword, status, pageable);
        
        model.addAttribute("buses", buses);
        model.addAttribute("keyword", keyword);
        model.addAttribute("status", status);
        model.addAttribute("busTypes", BusType.values());
        model.addAttribute("busStatuses", BusStatus.values());
        model.addAttribute("currentPage", page);
        
        return "admin/buses/list";
    }

    @GetMapping("/new")
    public String createBusForm(Model model) {
        model.addAttribute("busDto", new BusRequest());
        model.addAttribute("busTypes", BusType.values());
        model.addAttribute("busStatuses", BusStatus.values());
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
            model.addAttribute("isEdit", false);
            return "admin/buses/form";
        }
    }

    @GetMapping("/{id}/edit")
    public String editBusForm(@PathVariable Long id, Model model) {
        try {
            BusDTO bus = busService.getBusById(id);
            model.addAttribute("busDto", bus);
            model.addAttribute("busTypes", BusType.values());
            model.addAttribute("busStatuses", BusStatus.values());
            model.addAttribute("isEdit", true);
            return "admin/buses/form";
        } catch (Exception e) {
            return "redirect:/admin/buses?error=" + e.getMessage();
        }
    }

    @PostMapping("/{id}/edit")
    public String updateBus(@PathVariable Long id,
                           @Valid @ModelAttribute("busDto") BusRequest busRequest,
                           BindingResult bindingResult,
                           RedirectAttributes redirectAttributes,
                           Model model) {
        
        if (bindingResult.hasErrors()) {
            model.addAttribute("busTypes", BusType.values());
            model.addAttribute("busStatuses", BusStatus.values());
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
            model.addAttribute("isEdit", true);
            return "admin/buses/form";
        }
    }

    @PostMapping("/{id}/delete")
    public String deleteBus(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            busService.deleteBus(id);
            redirectAttributes.addAttribute("success", "deleted");
        } catch (Exception e) {
            redirectAttributes.addAttribute("error", e.getMessage());
        }
        return "redirect:/admin/buses";
    }

    @GetMapping("/{id}/view")
    public String viewBus(@PathVariable Long id, Model model) {
        try {
            BusDTO bus = busService.getBusById(id);
            model.addAttribute("bus", bus);
            return "admin/buses/detail";
        } catch (Exception e) {
            return "redirect:/admin/buses?error=" + e.getMessage();
        }
    }
}
