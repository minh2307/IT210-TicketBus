// === FILE: com/example/it210ticketbus/interceptor/AuthInterceptor.java ===
package com.example.it210ticketbus.interceptor;

import com.example.it210ticketbus.dto.response.UserProfileResponse;
import com.example.it210ticketbus.enums.Role;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class AuthInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {
        String currentURI = request.getRequestURI();
        HttpSession session = request.getSession(false);

        // Get current user from session
        UserProfileResponse user = (session != null) ? (UserProfileResponse) session.getAttribute("user") : null;

        // Debug: Log session and user info
        System.out.println("DEBUG: URI = " + currentURI);
        System.out.println("DEBUG: Session exists = " + (session != null));
        System.out.println("DEBUG: User in session = " + (user != null));
        if (user != null) {
            System.out.println("DEBUG: User role = " + user.getRole());
        }

        // Public URLs - allow access without authentication
        if (isPublicUrl(currentURI)) {
            return true;
        }

        // If user is not logged in and URL is restricted, go to 403 instead of login
        // redirect
        if (user == null && (currentURI.startsWith("/admin/") || currentURI.startsWith("/staff/"))) {
            response.sendRedirect("/403");
            return false;
        }

        // If user is not logged in and URL is not public, redirect to login
        if (user == null) {
            response.sendRedirect("/auth/login");
            return false;
        }

        // Check role-based access
        Role userRole = user.getRole();

        // Admin only URLs - chỉ ADMIN được vào
        if (currentURI.startsWith("/admin/") && userRole != Role.ADMIN) {
            response.sendRedirect("/403");
            return false;
        }

        // Staff only URLs - chỉ STAFF và ADMIN được vào, PASSENGER không
        if (currentURI.startsWith("/staff/") && userRole == Role.PASSENGER) {
            response.sendRedirect("/403");
            return false;
        }

        // Ngăn ADMIN vào trang STAFF
        if (currentURI.startsWith("/staff/") && userRole == Role.ADMIN) {
            response.sendRedirect("/403");
            return false;
        }

        return true;
    }

    private boolean isPublicUrl(String uri) {
        // Clean URI from session parameters (e.g. ;jsessionid=...)
        int semicolonIndex = uri.indexOf(';');
        if (semicolonIndex != -1) {
            uri = uri.substring(0, semicolonIndex);
        }

        return uri.equals("/") ||
                uri.equals("/search") ||
                uri.equals("/seat-map") ||
                uri.equals("/booking-confirm") ||
                uri.equals("/lookup") ||
                uri.equals("/403") ||
                uri.startsWith("/api/") ||
                uri.startsWith("/auth/") ||
                uri.startsWith("/error") ||
                uri.startsWith("/images/") ||
                uri.equals("/favicon.ico");
    }
}
