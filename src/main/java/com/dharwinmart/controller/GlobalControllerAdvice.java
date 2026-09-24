package com.dharwinmart.controller;

import com.dharwinmart.dto.Cart;
import com.dharwinmart.service.CartService;
import com.dharwinmart.service.ProductService;
import com.dharwinmart.service.WishlistService;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.util.Collections;
import java.util.List;
import java.util.Set;

@ControllerAdvice
public class GlobalControllerAdvice {

    private final CartService cartService;
    private final ProductService productService;
    private final WishlistService wishlistService;

    public GlobalControllerAdvice(CartService cartService,
                                  ProductService productService,
                                  WishlistService wishlistService) {
        this.cartService = cartService;
        this.productService = productService;
        this.wishlistService = wishlistService;
    }

    @ModelAttribute("cartItemCount")
    public int getCartItemCount(HttpSession session) {
        Cart cart = cartService.getCart(session);
        return cart != null ? cart.getTotalQuantity() : 0;
    }

    @ModelAttribute("globalCategories")
    public List<String> getGlobalCategories() {
        return productService.getAllCategories();
    }

    @ModelAttribute("currentUser")
    public String getCurrentUser(HttpSession session) {
        return session != null ? (String) session.getAttribute(LoginController.SESSION_USER_KEY) : null;
    }

    @ModelAttribute("userRole")
    public String getUserRole(HttpSession session) {
        return session != null ? (String) session.getAttribute(LoginController.SESSION_ROLE_KEY) : null;
    }

    @ModelAttribute("userFullName")
    public String getUserFullName(HttpSession session) {
        return session != null ? (String) session.getAttribute(LoginController.SESSION_USER_NAME_KEY) : null;
    }

    @ModelAttribute("wishlistCount")
    public long getWishlistCount(HttpSession session) {
        if (session != null) {
            Long userId = (Long) session.getAttribute(LoginController.SESSION_USER_ID_KEY);
            if (userId != null) {
                return wishlistService.getWishlistCount(userId);
            }
        }
        return 0;
    }

    @ModelAttribute("wishlistProductIds")
    public Set<Long> getWishlistProductIds(HttpSession session) {
        if (session != null) {
            Long userId = (Long) session.getAttribute(LoginController.SESSION_USER_ID_KEY);
            if (userId != null) {
                return wishlistService.getWishlistedProductIds(userId);
            }
        }
        return Collections.emptySet();
    }
}
