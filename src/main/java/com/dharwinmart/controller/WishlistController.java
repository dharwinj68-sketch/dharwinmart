package com.dharwinmart.controller;

import com.dharwinmart.entity.Wishlist;
import com.dharwinmart.service.WishlistService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/wishlist")
public class WishlistController {

    private final WishlistService wishlistService;

    public WishlistController(WishlistService wishlistService) {
        this.wishlistService = wishlistService;
    }

    private Long getBuyerId(HttpSession session) {
        Long userId = (Long) session.getAttribute(LoginController.SESSION_USER_ID_KEY);
        if (userId == null) {
            throw new IllegalStateException("You must be logged in as a Buyer to manage your wishlist.");
        }
        return userId;
    }

    @GetMapping
    public String viewWishlist(HttpSession session, Model model) {
        Long userId = getBuyerId(session);
        List<Wishlist> wishlist = wishlistService.getWishlistForUser(userId);
        model.addAttribute("wishlist", wishlist);
        return "wishlist";
    }

    @PostMapping("/toggle")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> toggleWishlistAjax(@RequestParam("productId") Long productId,
                                                                 HttpSession session) {
        Long userId = (Long) session.getAttribute(LoginController.SESSION_USER_ID_KEY);
        Map<String, Object> response = new HashMap<>();

        if (userId == null) {
            response.put("success", false);
            response.put("error", "auth_required");
            response.put("message", "Please sign in as a Buyer to add items to your wishlist.");
            return ResponseEntity.status(401).body(response);
        }

        try {
            boolean isWishlisted = wishlistService.toggleWishlist(userId, productId);
            long count = wishlistService.getWishlistCount(userId);

            response.put("success", true);
            response.put("wishlisted", isWishlisted);
            response.put("count", count);
            response.put("message", isWishlisted ? "Item added to wishlist! ♥" : "Item removed from wishlist.");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error updating wishlist: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @PostMapping("/add")
    public String addToWishlistForm(@RequestParam("productId") Long productId,
                                    HttpServletRequest request,
                                    HttpSession session,
                                    RedirectAttributes redirectAttributes) {
        try {
            Long userId = getBuyerId(session);
            boolean added = wishlistService.addToWishlist(userId, productId);
            if (added) {
                redirectAttributes.addFlashAttribute("successMessage", "Product added to your wishlist!");
            } else {
                redirectAttributes.addFlashAttribute("infoMessage", "Product is already in your wishlist.");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }

        String referer = request.getHeader("Referer");
        return "redirect:" + (referer != null && !referer.isEmpty() ? referer : "/wishlist");
    }

    @PostMapping("/remove")
    public String removeFromWishlist(@RequestParam("productId") Long productId,
                                     HttpServletRequest request,
                                     HttpSession session,
                                     RedirectAttributes redirectAttributes) {
        try {
            Long userId = getBuyerId(session);
            wishlistService.removeFromWishlist(userId, productId);
            redirectAttributes.addFlashAttribute("successMessage", "Product removed from your wishlist.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }

        String referer = request.getHeader("Referer");
        return "redirect:" + (referer != null && !referer.isEmpty() ? referer : "/wishlist");
    }
}
