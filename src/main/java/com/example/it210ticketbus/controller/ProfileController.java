// === FILE: com/example/it210ticketbus/controller/ProfileController.java ===
package com.example.it210ticketbus.controller;

import com.example.it210ticketbus.dto.request.ProfileUpdateRequest;
import com.example.it210ticketbus.dto.response.ProfileDTO;
import com.example.it210ticketbus.dto.response.UserProfileResponse;
import com.example.it210ticketbus.service.ProfileService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/profile")
@RequiredArgsConstructor
public class ProfileController {

    private final ProfileService profileService;

    @GetMapping("/view")
    public String viewProfile(HttpSession session, Model model) {
        UserProfileResponse currentUser = (UserProfileResponse) session.getAttribute("user");
        if (currentUser == null) {
            return "redirect:/auth/login";
        }

        ProfileDTO profile = profileService.getProfile(currentUser.getId());
        model.addAttribute("profile", profile);
        return "profile/view";
    }

    @GetMapping("/edit")
    public String editProfile(HttpSession session, Model model) {
        UserProfileResponse currentUser = (UserProfileResponse) session.getAttribute("user");
        if (currentUser == null) {
            return "redirect:/auth/login";
        }

        ProfileDTO profile = profileService.getProfile(currentUser.getId());
        model.addAttribute("profileDto", profile);
        // Also add ProfileUpdateRequest for the form binding
        ProfileUpdateRequest profileRequest = ProfileUpdateRequest.builder()
                .fullName(profile.getFullName())
                .phone(profile.getPhone())
                .email(profile.getEmail())
                .address(profile.getAddress())
                .build();
        model.addAttribute("profileUpdateRequest", profileRequest);
        return "profile/edit";
    }

    @PostMapping("/edit")
    public String updateProfile(@Valid @ModelAttribute("profileUpdateRequest") ProfileUpdateRequest profileRequest,
                               BindingResult bindingResult,
                               HttpSession session,
                               RedirectAttributes redirectAttributes,
                               Model model) {
        UserProfileResponse currentUser = (UserProfileResponse) session.getAttribute("user");
        if (currentUser == null) {
            return "redirect:/auth/login";
        }

        if (bindingResult.hasErrors()) {
            // Re-add the current profile data for display
            ProfileDTO profile = profileService.getProfile(currentUser.getId());
            model.addAttribute("profileDto", profile);
            return "profile/edit";
        }

        try {
            ProfileDTO updatedProfile = profileService.updateProfile(currentUser.getId(), profileRequest);
            
            // Update session with new profile data (keep as UserProfileResponse to satisfy AuthInterceptor)
            currentUser.setFullName(updatedProfile.getFullName());
            currentUser.setPhone(updatedProfile.getPhone());
            currentUser.setEmail(updatedProfile.getEmail());
            session.setAttribute("user", currentUser);
            
            redirectAttributes.addAttribute("success", "true");
            return "redirect:/profile/view";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            return "profile/edit";
        }
    }

    @GetMapping("/password")
    public String changePasswordPage() {
        return "profile/password";
    }

    @PostMapping("/password")
    public String changePassword(@RequestParam String oldPassword,
                                 @RequestParam String newPassword,
                                 @RequestParam String confirmPassword,
                                 HttpSession session,
                                 RedirectAttributes redirectAttributes,
                                 Model model) {
        UserProfileResponse currentUser = (UserProfileResponse) session.getAttribute("user");
        if (currentUser == null) {
            return "redirect:/auth/login";
        }

        // Validate new password confirmation
        if (!newPassword.equals(confirmPassword)) {
            model.addAttribute("error", "Mật khẩu mới và xác nhận mật khẩu không khớp");
            return "profile/password";
        }

        // Validate password length
        if (newPassword.length() < 8) {
            model.addAttribute("error", "Mật khẩu mới phải có ít nhất 8 ký tự");
            return "profile/password";
        }

        try {
            profileService.changePassword(currentUser.getId(), oldPassword, newPassword);
            redirectAttributes.addAttribute("pwdChanged", "true");
            return "redirect:/profile/view";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            return "profile/password";
        }
    }
}
