package com.dharwinmart.controller;

import com.dharwinmart.entity.Product;
import com.dharwinmart.service.ProductService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class HomeController {

    private final ProductService productService;

    public HomeController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping("/")
    public String index(Model model) {
        model.addAttribute("featuredProducts", productService.getFeaturedProducts());
        model.addAttribute("categories", productService.getAllCategories());
        return "index";
    }

    @GetMapping("/products")
    public String products(@RequestParam(name = "category", required = false) String category, Model model) {
        List<Product> products;
        if (category != null && !category.trim().isEmpty() && !category.equalsIgnoreCase("All")) {
            products = productService.getProductsByCategory(category);
            model.addAttribute("selectedCategory", category);
            model.addAttribute("pageTitle", category + " Products");
        } else {
            products = productService.getAllProducts();
            model.addAttribute("selectedCategory", "All");
            model.addAttribute("pageTitle", "All Products");
        }
        model.addAttribute("products", products);
        model.addAttribute("categories", productService.getAllCategories());
        return "products";
    }

    @GetMapping("/search")
    public String search(@RequestParam(name = "keyword", required = false) String keyword, Model model) {
        String cleanKeyword = (keyword != null) ? keyword.trim() : "";
        List<Product> products = productService.searchProducts(cleanKeyword);

        model.addAttribute("keyword", cleanKeyword);
        model.addAttribute("products", products);
        model.addAttribute("categories", productService.getAllCategories());
        model.addAttribute("isSearch", true);
        model.addAttribute("pageTitle", "Search results for: " + cleanKeyword);
        return "products";
    }

    @GetMapping("/products/{id}")
    public String productDetails(@PathVariable("id") Long id, Model model) {
        Product product = productService.getProductById(id);
        model.addAttribute("product", product);
        return "product-details";
    }
}
