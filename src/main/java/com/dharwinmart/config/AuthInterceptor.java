package com.dharwinmart.config;

import com.dharwinmart.controller.LoginController;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class AuthInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String path = request.getServletPath();
        if (path == null || path.isEmpty()) {
            path = request.getRequestURI();
        }

        HttpSession session = request.getSession(false);
        String currentUser = (session != null) ? (String) session.getAttribute(LoginController.SESSION_USER_KEY) : null;
        String userRole = (session != null) ? (String) session.getAttribute(LoginController.SESSION_ROLE_KEY) : null;

        boolean isAdminPath = path.equals("/admin") || path.startsWith("/admin/");
        boolean isSellerPath = path.equals("/seller") || path.startsWith("/seller/");
        boolean isBuyerPath = path.equals("/buyer") || path.startsWith("/buyer/");
        boolean isWishlistPath = path.equals("/wishlist") || path.startsWith("/wishlist/");

        // Check for AJAX / API requests (like /wishlist/toggle)
        boolean isAjaxRequest = "/wishlist/toggle".equals(path)
                || "XMLHttpRequest".equalsIgnoreCase(request.getHeader("X-Requested-With"))
                || (request.getHeader("Accept") != null && request.getHeader("Accept").contains("application/json"));

        // 1. Admin Routes
        if (isAdminPath) {
            if (currentUser == null) {
                if (isAjaxRequest) {
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    response.setContentType("application/json");
                    response.setCharacterEncoding("UTF-8");
                    response.getWriter().write("{\"success\":false,\"error\":\"auth_required\",\"message\":\"Authentication required. Please sign in as Admin.\"}");
                    return false;
                }
                response.sendRedirect(request.getContextPath() + "/login?role=ADMIN&error=auth_required");
                return false;
            }
            if (!"ADMIN".equalsIgnoreCase(userRole)) {
                if (isAjaxRequest) {
                    response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                    response.setContentType("application/json");
                    response.setCharacterEncoding("UTF-8");
                    response.getWriter().write("{\"success\":false,\"error\":\"access_denied\",\"message\":\"Access denied: Admin role required.\"}");
                    return false;
                }
                response.sendRedirect(request.getContextPath() + "/access-denied");
                return false;
            }
            return true;
        }

        // 2. Seller Routes
        if (isSellerPath) {
            if (currentUser == null) {
                if (isAjaxRequest) {
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    response.setContentType("application/json");
                    response.setCharacterEncoding("UTF-8");
                    response.getWriter().write("{\"success\":false,\"error\":\"auth_required\",\"message\":\"Authentication required. Please sign in as Seller.\"}");
                    return false;
                }
                response.sendRedirect(request.getContextPath() + "/login?role=SELLER&error=auth_required");
                return false;
            }
            if (!"SELLER".equalsIgnoreCase(userRole)) {
                if (isAjaxRequest) {
                    response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                    response.setContentType("application/json");
                    response.setCharacterEncoding("UTF-8");
                    response.getWriter().write("{\"success\":false,\"error\":\"access_denied\",\"message\":\"Access denied: Seller role required.\"}");
                    return false;
                }
                response.sendRedirect(request.getContextPath() + "/access-denied");
                return false;
            }
            return true;
        }

        // 3. Buyer & Wishlist Routes
        if (isBuyerPath || isWishlistPath) {
            if (currentUser == null) {
                if (isAjaxRequest) {
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    response.setContentType("application/json");
                    response.setCharacterEncoding("UTF-8");
                    response.getWriter().write("{\"success\":false,\"error\":\"auth_required\",\"message\":\"Please sign in as a Buyer to manage your wishlist.\"}");
                    return false;
                }
                response.sendRedirect(request.getContextPath() + "/login?role=BUYER&error=auth_required");
                return false;
            }
            if (!"BUYER".equalsIgnoreCase(userRole)) {
                if (isAjaxRequest) {
                    response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                    response.setContentType("application/json");
                    response.setCharacterEncoding("UTF-8");
                    response.getWriter().write("{\"success\":false,\"error\":\"access_denied\",\"message\":\"Only Buyers can have a wishlist. (Signed in as " + userRole + ")\"}");
                    return false;
                }
                response.sendRedirect(request.getContextPath() + "/access-denied");
                return false;
            }
            return true;
        }

        return true;
    }
}
