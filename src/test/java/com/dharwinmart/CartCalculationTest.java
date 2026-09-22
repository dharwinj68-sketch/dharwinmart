package com.dharwinmart;

import com.dharwinmart.dto.Cart;
import com.dharwinmart.entity.Product;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class CartCalculationTest {

    @Test
    void testCartCalculations() {
        Cart cart = new Cart();

        Product p1 = new Product("Item 1", "Desc 1", new BigDecimal("100.00"), "", "Cat", 10);
        p1.setId(1L);

        Product p2 = new Product("Item 2", "Desc 2", new BigDecimal("250.50"), "", "Cat", 5);
        p2.setId(2L);

        // Add 2 of Item 1
        cart.addItem(p1, 2);
        // Add 1 of Item 2
        cart.addItem(p2, 1);

        assertEquals(3, cart.getTotalQuantity());
        // Expected total = (2 * 100.00) + (1 * 250.50) = 450.50
        assertEquals(new BigDecimal("450.50"), cart.getTotalAmount());

        // Update quantity of Item 1 to 3
        cart.updateQuantity(1L, 3);
        assertEquals(4, cart.getTotalQuantity());
        // Total = (3 * 100.00) + (1 * 250.50) = 550.50
        assertEquals(new BigDecimal("550.50"), cart.getTotalAmount());

        // Remove Item 2
        cart.removeItem(2L);
        assertEquals(3, cart.getTotalQuantity());
        assertEquals(new BigDecimal("300.00"), cart.getTotalAmount());

        // Clear cart
        cart.clear();
        assertTrue(cart.isEmpty());
        assertEquals(0, cart.getTotalQuantity());
        assertEquals(BigDecimal.ZERO, cart.getTotalAmount());
    }
}
