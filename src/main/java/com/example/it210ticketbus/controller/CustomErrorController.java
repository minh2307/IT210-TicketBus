package com.example.it210ticketbus.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.boot.webmvc.error.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class CustomErrorController implements ErrorController {

    @RequestMapping("/error")
    public String handleError(HttpServletRequest request, Model model) {
        Object status = request.getAttribute("jakarta.servlet.error.status_code");
        Object message = request.getAttribute("jakarta.servlet.error.message");
        Object exception = request.getAttribute("jakarta.servlet.error.exception");

        if (status != null) {
            model.addAttribute("status", status);
        }
        if (message != null && !message.toString().isEmpty()) {
            model.addAttribute("error", message);
        } else if (exception != null) {
            model.addAttribute("error", exception.toString());
        } else {
            model.addAttribute("error", "Đã xảy ra lỗi không mong muốn. Vui lòng thử lại sau.");
        }

        return "error/error";
    }
}
