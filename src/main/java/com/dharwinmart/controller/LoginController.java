package com.dharwinmart.controller;

import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class LoginController {

    public static final String SESSION_USER_KEY = "currentUser";
    public static final String SESSION_ROLE_KEY = "userRole";

    @GetMapping("/login")
    public String showLoginPage(@RequestParam(value = "role", required = false) String role,
                                @RequestParam(value = "logout", required = false) String logout,
                                HttpSession session,
                                Model model) {
        if (logout != null) {
            session.removeAttribute(SESSION_USER_KEY);
            session.removeAttribute(SESSION_ROLE_KEY);
            model.addAttribute("logoutMessage", "You have been logged out successfully.");
        }

        if (session.getAttribute(SESSION_USER_KEY) != null) {
            String existingRole = (String) session.getAttribute(SESSION_ROLE_KEY);
            if ("ADMIN".equalsIgnoreCase(existingRole)) {
                return "redirect:/admin";
            }
            return "redirect:/";
        }

        model.addAttribute("selectedRole", role != null ? role.toUpperCase() : "CUSTOMER");
        return "login";
    }

    @PostMapping("/login")
    public String processLogin(@RequestParam("username") String username,
                               @RequestParam("password") String password,
                               @RequestParam(value = "role", defaultValue = "CUSTOMER") String role,
                               HttpSession session,
                               RedirectAttributes redirectAttributes) {
        String cleanUser = username != null ? username.trim() : "";
        String cleanPass = password != null ? password.trim() : "";

        if (cleanUser.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Username or email cannot be empty.");
            return "redirect:/login";
        }

        // Demo MVP Authentication Logic
        if ("ADMIN".equalsIgnoreCase(role) || "admin".equalsIgnoreCase(cleanUser)) {
            session.setAttribute(SESSION_USER_KEY, cleanUser.isEmpty() ? "Admin" : cleanUser);
            session.setAttribute(SESSION_ROLE_KEY, "ADMIN");
            redirectAttributes.addFlashAttribute("successMessage", "Welcome back, Admin!");
            return "redirect:/admin";
        } else {
            session.setAttribute(SESSION_USER_KEY, cleanUser);
            session.setAttribute(SESSION_ROLE_KEY, "CUSTOMER");
            redirectAttributes.addFlashAttribute("successMessage", "Welcome back to DHARWINMART, " + cleanUser + "!");
            return "redirect:/";
        }
    }

    @GetMapping("/logout")
    public String logout(HttpSession session, RedirectAttributes redirectAttributes) {
        session.removeAttribute(SESSION_USER_KEY);
        session.removeAttribute(SESSION_ROLE_KEY);
        redirectAttributes.addFlashAttribute("successMessage", "You have been logged out successfully.");
        return "redirect:/login?logout";
    }
}
