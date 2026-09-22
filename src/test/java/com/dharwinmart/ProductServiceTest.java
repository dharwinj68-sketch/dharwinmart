package com.dharwinmart;

import com.dharwinmart.entity.Product;
import com.dharwinmart.repository.ProductRepository;
import com.dharwinmart.service.ProductService;
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
class ProductServiceTest {

    @Autowired
    private ProductService productService;

    @Autowired
    private ProductRepository productRepository;

    private Product testProduct;

    @BeforeEach
    void setUp() {
        testProduct = new Product(
                "Test Wireless Mouse",
                "Ergonomic rechargeable mouse for testing",
                new BigDecimal("1299.00"),
                "https://example.com/mouse.jpg",
                "Accessories",
                25
        );
        testProduct = productService.createProduct(testProduct);
    }

    @Test
    void testCreateAndRetrieveProduct() {
        assertNotNull(testProduct.getId());
        Product retrieved = productService.getProductById(testProduct.getId());
        assertEquals("Test Wireless Mouse", retrieved.getName());
        assertEquals(new BigDecimal("1299.00"), retrieved.getPrice());
        assertEquals("Accessories", retrieved.getCategory());
        assertEquals(25, retrieved.getStock());
    }

    @Test
    void testSearchProducts() {
        List<Product> results = productService.searchProducts("Wireless Mouse");
        assertFalse(results.isEmpty());
        assertTrue(results.stream().anyMatch(p -> p.getName().contains("Wireless Mouse")));
    }

    @Test
    void testFilterByCategory() {
        List<Product> accessories = productService.getProductsByCategory("Accessories");
        assertFalse(accessories.isEmpty());
        assertTrue(accessories.stream().allMatch(p -> "Accessories".equalsIgnoreCase(p.getCategory())));
    }

    @Test
    void testDeleteProduct() {
        Long id = testProduct.getId();
        productService.deleteProduct(id);
        assertThrows(RuntimeException.class, () -> productService.getProductById(id));
    }
}
