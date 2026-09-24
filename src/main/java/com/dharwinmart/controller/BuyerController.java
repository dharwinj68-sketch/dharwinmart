package com.dharwinmart.controller;

import com.dharwinmart.entity.Order;
import com.dharwinmart.entity.Product;
import com.dharwinmart.entity.User;
import com.dharwinmart.entity.Wishlist;
import com.dharwinmart.service.OrderService;
import com.dharwinmart.service.ProductService;
import com.dharwinmart.service.UserService;
import com.dharwinmart.service.WishlistService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/buyer")
public class BuyerController {

    private final ProductService productService;
    private final OrderService orderService;
    private final WishlistService wishlistService;
    private final UserService userService;

    public BuyerController(ProductService productService,
                           OrderService orderService,
                           WishlistService wishlistService,
                           UserService userService) {
        this.productService = productService;
        this.orderService = orderService;
        this.wishlistService = wishlistService;
        this.userService = userService;
    }

    private Long getBuyerId(HttpSession session) {
        Long buyerId = (Long) session.getAttribute(LoginController.SESSION_USER_ID_KEY);
        if (buyerId == null) {
            throw new IllegalStateException("Buyer session expired or not authenticated.");
        }
        return buyerId;
    }

    @GetMapping({"", "/", "/home", "/dashboard"})
    public String buyerHome(HttpSession session, Model model) {
        Long buyerId = getBuyerId(session);
        User buyer = userService.getUserById(buyerId);
        List<Order> orders = orderService.getOrdersByUserId(buyerId);
        List<Wishlist> wishlistItems = wishlistService.getWishlistForUser(buyerId);
        List<Product> featured = productService.getFeaturedProducts();

        model.addAttribute("buyer", buyer);
        model.addAttribute("totalOrders", orders.size());
        model.addAttribute("wishlistCount", wishlistItems.size());
        model.addAttribute("recentOrders", orders.size() > 3 ? orders.subList(0, 3) : orders);
        model.addAttribute("wishlistItems", wishlistItems.size() > 4 ? wishlistItems.subList(0, 4) : wishlistItems);
        model.addAttribute("featuredProducts", featured);
        return "buyer/home";
    }

    @GetMapping("/orders")
    public String buyerOrders(HttpSession session, Model model) {
        Long buyerId = getBuyerId(session);
        List<Order> orders = orderService.getOrdersByUserId(buyerId);
        model.addAttribute("orders", orders);
        return "buyer/orders";
    }

    @GetMapping("/profile")
    public String buyerProfile() {
        return "redirect:/profile";
    }
}
