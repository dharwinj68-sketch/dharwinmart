package com.dharwinmart.controller;

import com.dharwinmart.dto.Cart;
import com.dharwinmart.dto.CheckoutForm;
import com.dharwinmart.entity.Order;
import com.dharwinmart.exception.InsufficientStockException;
import com.dharwinmart.service.CartService;
import com.dharwinmart.service.OrderService;
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

    public CheckoutController(CartService cartService, OrderService orderService) {
        this.cartService = cartService;
        this.orderService = orderService;
    }

    @GetMapping("/checkout")
    public String viewCheckout(HttpSession session, Model model, RedirectAttributes redirectAttributes) {
        Cart cart = cartService.getCart(session);
        if (cart == null || cart.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Your cart is empty. Please add items before checking out.");
            return "redirect:/cart";
        }
        if (!model.containsAttribute("checkoutForm")) {
            model.addAttribute("checkoutForm", new CheckoutForm());
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
            Order order = orderService.placeOrder(cart, checkoutForm);
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
