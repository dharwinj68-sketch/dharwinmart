package com.dharwinmart.service;

import com.dharwinmart.dto.Cart;
import com.dharwinmart.entity.Product;
import com.dharwinmart.exception.ResourceNotFoundException;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Service;

@Service
public class CartService {

    public static final String SESSION_CART_KEY = "cart";

    private final ProductService productService;

    public CartService(ProductService productService) {
        this.productService = productService;
    }

    public Cart getCart(HttpSession session) {
        Cart cart = (Cart) session.getAttribute(SESSION_CART_KEY);
        if (cart == null) {
            cart = new Cart();
            session.setAttribute(SESSION_CART_KEY, cart);
        }
        return cart;
    }

    public void addToCart(HttpSession session, Long productId, int quantity) {
        Product product = productService.getProductById(productId);
        if (product.isOutOfStock()) {
            throw new IllegalStateException("Product is currently out of stock");
        }
        Cart cart = getCart(session);
        cart.addItem(product, quantity);
        session.setAttribute(SESSION_CART_KEY, cart);
    }

    public void updateQuantity(HttpSession session, Long productId, int quantity) {
        Cart cart = getCart(session);
        cart.updateQuantity(productId, quantity);
        session.setAttribute(SESSION_CART_KEY, cart);
    }

    public void removeFromCart(HttpSession session, Long productId) {
        Cart cart = getCart(session);
        cart.removeItem(productId);
        session.setAttribute(SESSION_CART_KEY, cart);
    }

    public void clearCart(HttpSession session) {
        Cart cart = getCart(session);
        cart.clear();
        session.setAttribute(SESSION_CART_KEY, cart);
    }
}
