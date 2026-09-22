package com.dharwinmart.controller;

import com.dharwinmart.dto.ProductForm;
import com.dharwinmart.entity.Order;
import com.dharwinmart.entity.Product;
import com.dharwinmart.service.OrderService;
import com.dharwinmart.service.ProductService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final ProductService productService;
    private final OrderService orderService;

    public AdminController(ProductService productService, OrderService orderService) {
        this.productService = productService;
        this.orderService = orderService;
    }

    @GetMapping
    public String dashboard(Model model) {
        long totalProducts = productService.countProducts();
        long totalOrders = orderService.countOrders();
        List<String> categories = productService.getAllCategories();
        List<Order> recentOrders = orderService.getRecentOrders();

        model.addAttribute("totalProducts", totalProducts);
        model.addAttribute("totalOrders", totalOrders);
        model.addAttribute("totalCategories", categories.size());
        model.addAttribute("recentOrders", recentOrders);
        return "admin/dashboard";
    }

    @GetMapping("/products")
    public String listProducts(Model model) {
        List<Product> products = productService.getAllProducts();
        model.addAttribute("products", products);
        return "admin/products";
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
