package com.dharwinmart.controller;

import com.dharwinmart.dto.Cart;
import com.dharwinmart.service.CartService;
import com.dharwinmart.service.ProductService;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.util.List;

@ControllerAdvice
public class GlobalControllerAdvice {

    private final CartService cartService;
    private final ProductService productService;

    public GlobalControllerAdvice(CartService cartService, ProductService productService) {
        this.cartService = cartService;
        this.productService = productService;
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
}
