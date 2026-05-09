// === FILE: com/example/it210ticketbus/controller/AuthController.java ===
package com.example.it210ticketbus.controller;

import com.example.it210ticketbus.dto.request.LoginRequest;
import com.example.it210ticketbus.dto.request.RegisterRequest;
import com.example.it210ticketbus.dto.response.UserProfileResponse;
import com.example.it210ticketbus.service.AuthService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @GetMapping("/login")
    public String loginPage(Model model) {
        model.addAttribute("loginRequest", new LoginRequest());
        return "auth/login";
    }

    @PostMapping("/login")
    public String login(@Valid @ModelAttribute("loginRequest") LoginRequest loginRequest,
                       BindingResult bindingResult,
                       HttpSession session,
                       RedirectAttributes redirectAttributes,
                       Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("loginRequest", loginRequest);
            return "auth/login";
        }

        try {
            UserProfileResponse userResponse = authService.login(loginRequest);
            session.setAttribute("user", userResponse);
            
            // Redirect based on role
            switch (userResponse.getRole()) {
                case ADMIN:
                    return "redirect:/admin/dashboard";
                case STAFF:
                    return "redirect:/staff/dashboard";
                case PASSENGER:
                    return "redirect:/";
                default:
                    return "redirect:/";
            }
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("loginRequest", loginRequest);
            return "auth/login";
        }
    }

    @GetMapping("/register")
    public String registerPage(Model model) {
        model.addAttribute("registerRequest", new RegisterRequest());
        return "auth/register";
    }

    @PostMapping("/register")
    public String register(@Valid @ModelAttribute("registerRequest") RegisterRequest registerRequest,
                          BindingResult bindingResult,
                          RedirectAttributes redirectAttributes,
                          HttpSession session,
                          Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("registerRequest", registerRequest);
            return "auth/register";
        }

        try {
            UserProfileResponse userResponse = authService.register(registerRequest);
            session.setAttribute("user", userResponse);
            redirectAttributes.addAttribute("registerSuccess", "true");
            return "redirect:/";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("registerRequest", registerRequest);
            return "auth/register";
        }
    }

    @PostMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/";
    }
}
