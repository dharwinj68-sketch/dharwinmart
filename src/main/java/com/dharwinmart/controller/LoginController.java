package com.dharwinmart.controller;

import com.dharwinmart.entity.User;
import com.dharwinmart.service.UserService;
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
    public static final String SESSION_USER_ID_KEY = "userId";
    public static final String SESSION_USER_NAME_KEY = "userFullName";

    private final UserService userService;

    public LoginController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/login")
    public String showLoginPage(@RequestParam(value = "role", required = false) String role,
                                @RequestParam(value = "logout", required = false) String logout,
                                @RequestParam(value = "error", required = false) String error,
                                HttpSession session,
                                Model model) {
        if (logout != null) {
            clearUserSession(session);
            model.addAttribute("logoutMessage", "You have been logged out successfully.");
        }

        if ("auth_required".equalsIgnoreCase(error)) {
            model.addAttribute("errorMessage", "Please sign in to access this page.");
        }

        // If user already logged in, redirect them to their role dashboard
        if (session != null && session.getAttribute(SESSION_USER_KEY) != null) {
            String existingRole = (String) session.getAttribute(SESSION_ROLE_KEY);
            return getRedirectForRole(existingRole);
        }

        String targetRole = role != null && !role.trim().isEmpty() ? role.toUpperCase() : "BUYER";
        model.addAttribute("selectedRole", targetRole);
        return "login";
    }

    @PostMapping("/login")
    public String processLogin(@RequestParam("username") String username,
                               @RequestParam("password") String password,
                               @RequestParam(value = "role", defaultValue = "BUYER") String role,
                               HttpSession session,
                               RedirectAttributes redirectAttributes) {
        try {
            User user = userService.authenticate(username, password, role);

            // Set session attributes
            session.setAttribute(SESSION_USER_KEY, user.getUsername());
            session.setAttribute(SESSION_ROLE_KEY, user.getRole().toUpperCase());
            session.setAttribute(SESSION_USER_ID_KEY, user.getId());
            session.setAttribute(SESSION_USER_NAME_KEY, user.getFullName());

            redirectAttributes.addFlashAttribute("successMessage",
                    "Welcome back, " + user.getFullName() + "! (Signed in as " + user.getRole() + ")");

            return getRedirectForRole(user.getRole());
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/login?role=" + (role != null ? role.toUpperCase() : "BUYER");
        }
    }

    @GetMapping("/register")
    public String showRegisterPage(@RequestParam(value = "role", required = false) String role,
                                   HttpSession session,
                                   Model model) {
        if (session != null && session.getAttribute(SESSION_USER_KEY) != null) {
            String existingRole = (String) session.getAttribute(SESSION_ROLE_KEY);
            return getRedirectForRole(existingRole);
        }

        String cleanRole = (role != null && "SELLER".equalsIgnoreCase(role)) ? "SELLER" : "BUYER";
        model.addAttribute("selectedRole", cleanRole);
        return "register";
    }

    @PostMapping("/register")
    public String processRegistration(@RequestParam("username") String username,
                                      @RequestParam("password") String password,
                                      @RequestParam("email") String email,
                                      @RequestParam("fullName") String fullName,
                                      @RequestParam(value = "role", defaultValue = "BUYER") String role,
                                      @RequestParam(value = "phone", required = false) String phone,
                                      @RequestParam(value = "address", required = false) String address,
                                      RedirectAttributes redirectAttributes) {
        try {
            User user = userService.registerUser(username, password, email, fullName, role, phone, address);
            redirectAttributes.addFlashAttribute("successMessage",
                    "Account registered successfully as " + user.getRole() + "! Please sign in to continue.");
            return "redirect:/login?role=" + user.getRole();
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/register?role=" + (role != null ? role : "BUYER");
        }
    }

    @GetMapping("/profile")
    public String viewProfile(HttpSession session, Model model) {
        Long userId = (Long) session.getAttribute(SESSION_USER_ID_KEY);
        if (userId == null) {
            return "redirect:/login?error=auth_required";
        }
        User user = userService.getUserById(userId);
        model.addAttribute("user", user);
        return "profile";
    }

    @PostMapping("/profile")
    public String updateProfile(@RequestParam("fullName") String fullName,
                                @RequestParam(value = "phone", required = false) String phone,
                                @RequestParam(value = "address", required = false) String address,
                                HttpSession session,
                                RedirectAttributes redirectAttributes) {
        Long userId = (Long) session.getAttribute(SESSION_USER_ID_KEY);
        if (userId == null) {
            return "redirect:/login?error=auth_required";
        }
        try {
            User updated = userService.updateProfile(userId, fullName, phone, address);
            session.setAttribute(SESSION_USER_NAME_KEY, updated.getFullName());
            redirectAttributes.addFlashAttribute("successMessage", "Profile updated successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error updating profile: " + e.getMessage());
        }
        return "redirect:/profile";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session, RedirectAttributes redirectAttributes) {
        clearUserSession(session);
        redirectAttributes.addFlashAttribute("successMessage", "You have been logged out successfully.");
        return "redirect:/login?logout";
    }

    @GetMapping("/access-denied")
    public String accessDenied(HttpSession session, Model model) {
        String role = (session != null) ? (String) session.getAttribute(SESSION_ROLE_KEY) : null;
        model.addAttribute("userRole", role);
        return "access-denied";
    }

    private void clearUserSession(HttpSession session) {
        if (session != null) {
            session.removeAttribute(SESSION_USER_KEY);
            session.removeAttribute(SESSION_ROLE_KEY);
            session.removeAttribute(SESSION_USER_ID_KEY);
            session.removeAttribute(SESSION_USER_NAME_KEY);
        }
    }

    private String getRedirectForRole(String role) {
        if ("ADMIN".equalsIgnoreCase(role)) {
            return "redirect:/admin/dashboard";
        } else if ("SELLER".equalsIgnoreCase(role)) {
            return "redirect:/seller/dashboard";
        } else {
            return "redirect:/buyer/home";
        }
    }
}
