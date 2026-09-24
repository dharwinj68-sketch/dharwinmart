package com.dharwinmart.controller;

import com.dharwinmart.dto.ProductForm;
import com.dharwinmart.entity.Order;
import com.dharwinmart.entity.Product;
import com.dharwinmart.entity.User;
import com.dharwinmart.exception.AccessDeniedException;
import com.dharwinmart.service.OrderService;
import com.dharwinmart.service.ProductService;
import com.dharwinmart.service.UserService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.util.List;

@Controller
@RequestMapping("/seller")
public class SellerController {

    private final ProductService productService;
    private final OrderService orderService;
    private final UserService userService;

    public SellerController(ProductService productService, OrderService orderService, UserService userService) {
        this.productService = productService;
        this.orderService = orderService;
        this.userService = userService;
    }

    private Long getSellerId(HttpSession session) {
        Long sellerId = (Long) session.getAttribute(LoginController.SESSION_USER_ID_KEY);
        if (sellerId == null) {
            throw new AccessDeniedException("Session expired or invalid seller login.");
        }
        return sellerId;
    }

    @GetMapping({"", "/", "/dashboard"})
    public String dashboard(HttpSession session, Model model) {
        Long sellerId = getSellerId(session);
        List<Product> products = productService.getProductsBySellerId(sellerId);
        List<Order> sellerOrders = orderService.getOrdersForSeller(sellerId);
        BigDecimal sellerRevenue = orderService.calculateSellerRevenue(sellerId);

        long outOfStockCount = products.stream().filter(Product::isOutOfStock).count();

        model.addAttribute("products", products);
        model.addAttribute("totalProducts", products.size());
        model.addAttribute("totalOrders", sellerOrders.size());
        model.addAttribute("sellerRevenue", sellerRevenue);
        model.addAttribute("outOfStockCount", outOfStockCount);
        model.addAttribute("recentOrders", sellerOrders.size() > 5 ? sellerOrders.subList(0, 5) : sellerOrders);

        return "seller/dashboard";
    }

    @GetMapping("/products")
    public String listProducts(HttpSession session, Model model) {
        Long sellerId = getSellerId(session);
        List<Product> products = productService.getProductsBySellerId(sellerId);
        model.addAttribute("products", products);
        return "seller/products";
    }

    @GetMapping("/products/new")
    public String newProductForm(Model model) {
        if (!model.containsAttribute("productForm")) {
            model.addAttribute("productForm", new ProductForm());
        }
        model.addAttribute("categories", productService.getAllCategories());
        model.addAttribute("isEdit", false);
        return "seller/product-form";
    }

    @GetMapping("/products/edit/{id}")
    public String editProductForm(@PathVariable("id") Long id, HttpSession session, Model model) {
        Long sellerId = getSellerId(session);
        Product product = productService.getProductById(id);

        if (product.getSeller() == null || !product.getSeller().getId().equals(sellerId)) {
            throw new AccessDeniedException("Unauthorized: You do not own this product and cannot modify it.");
        }

        ProductForm form = new ProductForm(
                product.getId(),
                product.getName(),
                product.getDescription(),
                product.getPrice(),
                product.getCategory(),
                product.getStock(),
                product.getImageUrl()
        );
        model.addAttribute("productForm", form);
        model.addAttribute("categories", productService.getAllCategories());
        model.addAttribute("isEdit", true);
        return "seller/product-form";
    }

    @PostMapping("/products/save")
    public String saveProduct(@Valid @ModelAttribute("productForm") ProductForm productForm,
                               BindingResult bindingResult,
                               HttpSession session,
                               Model model,
                               RedirectAttributes redirectAttributes) {
        Long sellerId = getSellerId(session);
        if (bindingResult.hasErrors()) {
            model.addAttribute("categories", productService.getAllCategories());
            model.addAttribute("isEdit", productForm.getId() != null);
            return "seller/product-form";
        }

        Product product = new Product(
                productForm.getName(),
                productForm.getDescription(),
                productForm.getPrice(),
                productForm.getImageUrl(),
                productForm.getCategory(),
                productForm.getStock()
        );

        if (productForm.getId() != null) {
            // Update existing seller product with ownership enforcement
            productService.updateSellerProduct(productForm.getId(), product, sellerId);
            redirectAttributes.addFlashAttribute("successMessage", "Product updated successfully!");
        } else {
            // Create new seller product
            User seller = userService.getUserById(sellerId);
            productService.createSellerProduct(product, seller);
            redirectAttributes.addFlashAttribute("successMessage", "New product listed successfully!");
        }

        return "redirect:/seller/products";
    }

    @PostMapping("/products/delete/{id}")
    public String deleteProduct(@PathVariable("id") Long id, HttpSession session, RedirectAttributes redirectAttributes) {
        Long sellerId = getSellerId(session);
        try {
            productService.deleteSellerProduct(id, sellerId);
            redirectAttributes.addFlashAttribute("successMessage", "Product removed from your catalog.");
        } catch (AccessDeniedException e) {
            throw e;
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error deleting product: " + e.getMessage());
        }
        return "redirect:/seller/products";
    }

    @GetMapping("/orders")
    public String listSellerOrders(HttpSession session, Model model) {
        Long sellerId = getSellerId(session);
        List<Order> orders = orderService.getOrdersForSeller(sellerId);
        model.addAttribute("orders", orders);
        model.addAttribute("currentSellerId", sellerId);
        return "seller/orders";
    }
}
