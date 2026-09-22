package com.dharwinmart;

import com.dharwinmart.dto.Cart;
import com.dharwinmart.dto.CheckoutForm;
import com.dharwinmart.entity.Order;
import com.dharwinmart.entity.Product;
import com.dharwinmart.exception.InsufficientStockException;
import com.dharwinmart.repository.ProductRepository;
import com.dharwinmart.service.OrderService;
import com.dharwinmart.service.ProductService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class OrderServiceTest {

    @Autowired
    private OrderService orderService;

    @Autowired
    private ProductService productService;

    @Autowired
    private ProductRepository productRepository;

    @Test
    void testPlaceOrderSuccessAndStockReduction() {
        // 1. Create a dedicated product
        Product product = new Product(
                "Test Gaming Headset",
                "High quality test headset",
                new BigDecimal("2500.00"),
                "https://example.com/headset.jpg",
                "Electronics",
                10
        );
        product = productService.createProduct(product);
        Long productId = product.getId();

        // 2. Prepare Cart
        Cart cart = new Cart();
        cart.addItem(product, 2);

        // 3. Prepare Checkout Form
        CheckoutForm form = new CheckoutForm(
                "Dharwin",
                "dharwin@test.com",
                "9876543210",
                "Room 101, Campus Hostel, University City"
        );

        // 4. Place Order
        Order order = orderService.placeOrder(cart, form);

        // 5. Assertions
        assertNotNull(order.getId());
        assertEquals("Dharwin", order.getCustomerName());
        assertEquals(new BigDecimal("5000.00"), order.getTotalAmount());
        assertEquals("PLACED", order.getStatus());
        assertEquals(1, order.getItems().size());

        // 6. Verify stock reduction: initial was 10, ordered 2 -> remaining should be 8
        Product updatedProduct = productService.getProductById(productId);
        assertEquals(8, updatedProduct.getStock());
    }

    @Test
    void testPlaceOrderInsufficientStock() {
        Product product = new Product(
                "Limited Edition Item",
                "Only 1 left in stock",
                new BigDecimal("100.00"),
                "https://example.com/item.jpg",
                "Accessories",
                1
        );
        product = productService.createProduct(product);

        Cart cart = new Cart();
        cart.addItem(product, 1);
        // Force quantity higher than stock
        cart.getItem(product.getId()).setQuantity(5);

        CheckoutForm form = new CheckoutForm(
                "Test Buyer",
                "buyer@test.com",
                "9876543210",
                "Test Address"
        );

        assertThrows(InsufficientStockException.class, () -> orderService.placeOrder(cart, form));
    }
}
