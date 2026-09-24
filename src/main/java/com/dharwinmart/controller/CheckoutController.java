package com.dharwinmart.controller;

import com.dharwinmart.dto.Cart;
import com.dharwinmart.dto.CheckoutForm;
import com.dharwinmart.entity.Order;
import com.dharwinmart.entity.User;
import com.dharwinmart.exception.InsufficientStockException;
import com.dharwinmart.service.CartService;
import com.dharwinmart.service.OrderService;
import com.dharwinmart.service.UserService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class CheckoutController {

    private final CartService cartService;
    private final OrderService orderService;
    private final UserService userService;

    public CheckoutController(CartService cartService, OrderService orderService, UserService userService) {
        this.cartService = cartService;
        this.orderService = orderService;
        this.userService = userService;
    }

    @GetMapping("/checkout")
    public String viewCheckout(HttpSession session, Model model, RedirectAttributes redirectAttributes) {
        Cart cart = cartService.getCart(session);
        if (cart == null || cart.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Your cart is empty. Please add items before checking out.");
            return "redirect:/cart";
        }
        if (!model.containsAttribute("checkoutForm")) {
            CheckoutForm form = new CheckoutForm();
            Long userId = (Long) session.getAttribute(LoginController.SESSION_USER_ID_KEY);
            if (userId != null) {
                try {
                    User user = userService.getUserById(userId);
                    form.setCustomerName(user.getFullName());
                    form.setEmail(user.getEmail());
                    if (user.getPhone() != null) form.setPhone(user.getPhone());
                    if (user.getAddress() != null) form.setAddress(user.getAddress());
                } catch (Exception ignored) {
                }
            }
            model.addAttribute("checkoutForm", form);
        }
        model.addAttribute("cart", cart);
        return "checkout";
    }

    @PostMapping("/checkout")
    public String processCheckout(@Valid @ModelAttribute("checkoutForm") CheckoutForm checkoutForm,
                                   BindingResult bindingResult,
                                   HttpSession session,
                                   Model model,
                                   RedirectAttributes redirectAttributes) {
        Cart cart = cartService.getCart(session);
        if (cart == null || cart.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Your cart is empty. Cannot place an order.");
            return "redirect:/cart";
        }

        if (bindingResult.hasErrors()) {
            model.addAttribute("cart", cart);
            return "checkout";
        }

        try {
            Long userId = (Long) session.getAttribute(LoginController.SESSION_USER_ID_KEY);
            User user = null;
            if (userId != null) {
                try {
                    user = userService.getUserById(userId);
                } catch (Exception ignored) {
                }
            }

            Order order = orderService.placeOrder(cart, checkoutForm, user);
            // Clear cart upon successful order
            cartService.clearCart(session);
            redirectAttributes.addFlashAttribute("successMessage", "Order placed successfully!");
            return "redirect:/order-confirmation/" + order.getId();
        } catch (InsufficientStockException e) {
            model.addAttribute("cart", cart);
            model.addAttribute("stockError", e.getMessage());
            return "checkout";
        } catch (Exception e) {
            model.addAttribute("cart", cart);
            model.addAttribute("generalError", "An error occurred while processing your order: " + e.getMessage());
            return "checkout";
        }
    }

    @GetMapping("/order-confirmation/{id}")
    public String orderConfirmation(@PathVariable("id") Long id, Model model) {
        Order order = orderService.getOrderById(id);
        model.addAttribute("order", order);
        return "order-confirmation";
    }
}
