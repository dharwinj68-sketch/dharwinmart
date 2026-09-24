package com.dharwinmart;

import com.dharwinmart.entity.Product;
import com.dharwinmart.entity.User;
import com.dharwinmart.entity.Wishlist;
import com.dharwinmart.service.ProductService;
import com.dharwinmart.service.UserService;
import com.dharwinmart.service.WishlistService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class WishlistTest {

    @Autowired
    private WishlistService wishlistService;

    @Autowired
    private UserService userService;

    @Autowired
    private ProductService productService;

    private User buyerA;
    private User buyerB;
    private Product product1;
    private Product product2;

    @BeforeEach
    void setUp() {
        buyerA = userService.registerUser("buyerA_" + System.currentTimeMillis(), "pass123", 
                "buyera_" + System.currentTimeMillis() + "@test.com", "Buyer A", "BUYER", "", "");
        buyerB = userService.registerUser("buyerB_" + System.currentTimeMillis(), "pass123", 
                "buyerb_" + System.currentTimeMillis() + "@test.com", "Buyer B", "BUYER", "", "");

        product1 = productService.createProduct(new Product("Item 1", "Desc 1", new BigDecimal("100.00"), "", "Electronics", 10));
        product2 = productService.createProduct(new Product("Item 2", "Desc 2", new BigDecimal("200.00"), "", "Books", 5));
    }

    @Test
    void testAddAndToggleWishlist() {
        // Initially empty
        assertEquals(0, wishlistService.getWishlistCount(buyerA.getId()));

        // Toggle to add
        boolean added = wishlistService.toggleWishlist(buyerA.getId(), product1.getId());
        assertTrue(added);
        assertEquals(1, wishlistService.getWishlistCount(buyerA.getId()));
        assertTrue(wishlistService.isWishlisted(buyerA.getId(), product1.getId()));

        // Toggle again to remove
        boolean removed = wishlistService.toggleWishlist(buyerA.getId(), product1.getId());
        assertFalse(removed);
        assertEquals(0, wishlistService.getWishlistCount(buyerA.getId()));
        assertFalse(wishlistService.isWishlisted(buyerA.getId(), product1.getId()));
    }

    @Test
    void testDuplicateWishlistPrevention() {
        // Add item
        boolean firstAdd = wishlistService.addToWishlist(buyerA.getId(), product1.getId());
        assertTrue(firstAdd);

        // Attempt second add of same product
        boolean secondAdd = wishlistService.addToWishlist(buyerA.getId(), product1.getId());
        assertFalse(secondAdd); // Duplicate prevented

        assertEquals(1, wishlistService.getWishlistCount(buyerA.getId()));
    }

    @Test
    void testBuyerWishlistIsolation() {
        // Buyer A adds Product 1
        wishlistService.addToWishlist(buyerA.getId(), product1.getId());

        // Buyer B adds Product 2
        wishlistService.addToWishlist(buyerB.getId(), product2.getId());

        // Verify Buyer A only sees Product 1
        List<Wishlist> aWishlist = wishlistService.getWishlistForUser(buyerA.getId());
        assertEquals(1, aWishlist.size());
        assertEquals(product1.getId(), aWishlist.get(0).getProduct().getId());

        // Verify Buyer B only sees Product 2
        List<Wishlist> bWishlist = wishlistService.getWishlistForUser(buyerB.getId());
        assertEquals(1, bWishlist.size());
        assertEquals(product2.getId(), bWishlist.get(0).getProduct().getId());

        // Buyer B does NOT have Product 1
        assertFalse(wishlistService.isWishlisted(buyerB.getId(), product1.getId()));
    }
}
