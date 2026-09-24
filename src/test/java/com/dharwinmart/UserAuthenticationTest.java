package com.dharwinmart;

import com.dharwinmart.entity.User;
import com.dharwinmart.repository.UserRepository;
import com.dharwinmart.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class UserAuthenticationTest {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        if (!userRepository.existsByUsernameIgnoreCase("testbuyer")) {
            userService.registerUser("testbuyer", "pass123", "testbuyer@test.com", "Test Buyer", "BUYER", "1234567890", "Test St");
        }
        if (!userRepository.existsByUsernameIgnoreCase("testseller")) {
            userService.registerUser("testseller", "pass123", "testseller@test.com", "Test Seller", "SELLER", "0987654321", "Market St");
        }
    }

    @Test
    void testBuyerAuthenticationSuccess() {
        User user = userService.authenticate("testbuyer", "pass123", "BUYER");
        assertNotNull(user);
        assertEquals("BUYER", user.getRole());
        assertTrue(user.isBuyer());
    }

    @Test
    void testSellerAuthenticationSuccess() {
        User user = userService.authenticate("testseller", "pass123", "SELLER");
        assertNotNull(user);
        assertEquals("SELLER", user.getRole());
        assertTrue(user.isSeller());
    }

    @Test
    void testInvalidCredentialsThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> 
            userService.authenticate("testbuyer", "wrongpass", "BUYER"));
    }

    @Test
    void testRoleMismatchThrowsException() {
        // Buyer account attempting to log in with SELLER role
        assertThrows(IllegalArgumentException.class, () -> 
            userService.authenticate("testbuyer", "pass123", "SELLER"));
    }

    @Test
    void testDisabledAccountThrowsException() {
        User buyer = userService.getUserByUsername("testbuyer");
        buyer.setEnabled(false);
        userRepository.save(buyer);

        assertThrows(IllegalStateException.class, () -> 
            userService.authenticate("testbuyer", "pass123", "BUYER"));
    }

    @Test
    void testPreventPublicAdminRegistration() {
        assertThrows(IllegalArgumentException.class, () ->
            userService.registerUser("fakeadmin", "pass123", "admin@fake.com", "Fake Admin", "ADMIN", "", ""));
    }

    @Test
    void testToggleUserStatus() {
        User seller = userService.getUserByUsername("testseller");
        assertTrue(seller.isEnabled());

        boolean newStatus = userService.toggleUserStatus(seller.getId(), "admin");
        assertFalse(newStatus);

        User updated = userService.getUserById(seller.getId());
        assertFalse(updated.isEnabled());
    }
}
