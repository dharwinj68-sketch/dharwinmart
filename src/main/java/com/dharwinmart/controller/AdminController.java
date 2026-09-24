package com.dharwinmart.controller;

import com.dharwinmart.dto.ProductForm;
import com.dharwinmart.entity.Order;
import com.dharwinmart.entity.Product;
import com.dharwinmart.entity.User;
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
@RequestMapping("/admin")
public class AdminController {

    private final ProductService productService;
    private final OrderService orderService;
    private final UserService userService;

    public AdminController(ProductService productService, OrderService orderService, UserService userService) {
        this.productService = productService;
        this.orderService = orderService;
        this.userService = userService;
    }

    @GetMapping({"", "/", "/dashboard"})
    public String dashboard(Model model) {
        long totalProducts = productService.countProducts();
        long totalOrders = orderService.countOrders();
        long totalBuyers = userService.countBuyers();
        long totalSellers = userService.countSellers();
        BigDecimal totalRevenue = orderService.calculateTotalRevenue();
        List<String> categories = productService.getAllCategories();
        List<Order> recentOrders = orderService.getRecentOrders();
        List<User> recentUsers = userService.getAllUsers();
        if (recentUsers.size() > 5) {
            recentUsers = recentUsers.subList(0, 5);
        }

        model.addAttribute("totalProducts", totalProducts);
        model.addAttribute("totalOrders", totalOrders);
        model.addAttribute("totalBuyers", totalBuyers);
        model.addAttribute("totalSellers", totalSellers);
        model.addAttribute("totalRevenue", totalRevenue);
        model.addAttribute("totalCategories", categories.size());
        model.addAttribute("recentOrders", recentOrders);
        model.addAttribute("recentUsers", recentUsers);
        return "admin/dashboard";
    }

    @GetMapping("/users")
    public String listUsers(@RequestParam(value = "role", defaultValue = "ALL") String role, Model model) {
        List<User> users = userService.getUsersByRole(role);
        model.addAttribute("users", users);
        model.addAttribute("selectedRole", role.toUpperCase());
        model.addAttribute("totalBuyers", userService.countBuyers());
        model.addAttribute("totalSellers", userService.countSellers());
        model.addAttribute("totalAdmins", userService.countAdmins());
        model.addAttribute("totalUsers", userService.countTotalUsers());
        return "admin/users";
    }

    @PostMapping("/users/{id}/toggle-status")
    public String toggleUserStatus(@PathVariable("id") Long id,
                                   @RequestParam(value = "filterRole", defaultValue = "ALL") String filterRole,
                                   HttpSession session,
                                   RedirectAttributes redirectAttributes) {
        try {
            String currentUsername = (String) session.getAttribute(LoginController.SESSION_USER_KEY);
            boolean active = userService.toggleUserStatus(id, currentUsername);
            redirectAttributes.addFlashAttribute("successMessage",
                    "User status successfully updated to " + (active ? "ACTIVE" : "DISABLED") + ".");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/admin/users?role=" + filterRole;
    }

    @GetMapping("/products")
    public String listProducts(Model model) {
        List<Product> products = productService.getAllProducts();
        model.addAttribute("products", products);
        return "admin/products";
    }

    @PostMapping("/products/{id}/approve")
    public String approveProduct(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        productService.setApprovalStatus(id, "APPROVED");
        redirectAttributes.addFlashAttribute("successMessage", "Product listing has been APPROVED.");
        return "redirect:/admin/products";
    }

    @PostMapping("/products/{id}/reject")
    public String rejectProduct(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        productService.setApprovalStatus(id, "REJECTED");
        redirectAttributes.addFlashAttribute("successMessage", "Product listing has been REJECTED.");
        return "redirect:/admin/products";
    }

    @GetMapping("/products/new")
    public String newProductForm(Model model) {
        if (!model.containsAttribute("productForm")) {
            model.addAttribute("productForm", new ProductForm());
        }
        model.addAttribute("categories", productService.getAllCategories());
        model.addAttribute("isEdit", false);
        return "admin/product-form";
    }

    @GetMapping("/products/edit/{id}")
    public String editProductForm(@PathVariable("id") Long id, Model model) {
        Product product = productService.getProductById(id);
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
        return "admin/product-form";
    }

    @PostMapping("/products/save")
    public String saveProduct(@Valid @ModelAttribute("productForm") ProductForm productForm,
                               BindingResult bindingResult,
                               Model model,
                               RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("categories", productService.getAllCategories());
            model.addAttribute("isEdit", productForm.getId() != null);
            return "admin/product-form";
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
            productService.updateProduct(productForm.getId(), product);
            redirectAttributes.addFlashAttribute("successMessage", "Product updated successfully!");
        } else {
            productService.createProduct(product);
            redirectAttributes.addFlashAttribute("successMessage", "Product created successfully!");
        }

        return "redirect:/admin/products";
    }

    @PostMapping("/products/delete/{id}")
    public String deleteProduct(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        try {
            productService.deleteProduct(id);
            redirectAttributes.addFlashAttribute("successMessage", "Product deleted successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error deleting product: " + e.getMessage());
        }
        return "redirect:/admin/products";
    }

    @GetMapping("/orders")
    public String listOrders(Model model) {
        List<Order> orders = orderService.getAllOrders();
        model.addAttribute("orders", orders);
        return "admin/orders";
    }

    @GetMapping("/orders/{id}")
    public String viewOrderDetails(@PathVariable("id") Long id, Model model) {
        Order order = orderService.getOrderById(id);
        model.addAttribute("order", order);
        return "admin/order-details";
    }
}
