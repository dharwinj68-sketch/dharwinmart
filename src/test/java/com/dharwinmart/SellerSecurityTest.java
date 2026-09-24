package com.dharwinmart;

import com.dharwinmart.entity.Product;
import com.dharwinmart.entity.User;
import com.dharwinmart.exception.AccessDeniedException;
import com.dharwinmart.service.ProductService;
import com.dharwinmart.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class SellerSecurityTest {

    @Autowired
    private ProductService productService;

    @Autowired
    private UserService userService;

    private User seller1;
    private User seller2;
    private Product seller1Product;

    @BeforeEach
    void setUp() {
        seller1 = userService.registerUser("seller1_" + System.currentTimeMillis(), "pass123",
                "seller1_" + System.currentTimeMillis() + "@test.com", "Seller One", "SELLER", "", "");
        seller2 = userService.registerUser("seller2_" + System.currentTimeMillis(), "pass123",
                "seller2_" + System.currentTimeMillis() + "@test.com", "Seller Two", "SELLER", "", "");

        Product p = new Product("Seller 1 Gadget", "Desc", new BigDecimal("500.00"), "", "Electronics", 10);
        seller1Product = productService.createSellerProduct(p, seller1);
    }

    @Test
    void testSellerCanEditOwnProduct() {
        Product updatedData = new Product("Updated Name", "Updated Desc", new BigDecimal("600.00"), "", "Electronics", 12);
        Product result = productService.updateSellerProduct(seller1Product.getId(), updatedData, seller1.getId());
        assertEquals("Updated Name", result.getName());
        assertEquals(new BigDecimal("600.00"), result.getPrice());
    }

    @Test
    void testSellerCannotEditOtherSellerProduct() {
        Product updatedData = new Product("Hacked Name", "Hacked Desc", new BigDecimal("1.00"), "", "Electronics", 99);
        // Seller 2 attempts to modify Seller 1's product
        assertThrows(AccessDeniedException.class, () ->
            productService.updateSellerProduct(seller1Product.getId(), updatedData, seller2.getId()));
    }

    @Test
    void testSellerCannotDeleteOtherSellerProduct() {
        // Seller 2 attempts to delete Seller 1's product
        assertThrows(AccessDeniedException.class, () ->
            productService.deleteSellerProduct(seller1Product.getId(), seller2.getId()));
    }
}
