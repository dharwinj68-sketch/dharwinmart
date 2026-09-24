package com.dharwinmart.config;

import com.dharwinmart.entity.Product;
import com.dharwinmart.entity.User;
import com.dharwinmart.entity.Wishlist;
import com.dharwinmart.repository.ProductRepository;
import com.dharwinmart.repository.UserRepository;
import com.dharwinmart.repository.WishlistRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

@Component
public class DataInitializer implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(DataInitializer.class);
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final WishlistRepository wishlistRepository;

    public DataInitializer(ProductRepository productRepository,
                           UserRepository userRepository,
                           WishlistRepository wishlistRepository) {
        this.productRepository = productRepository;
        this.userRepository = userRepository;
        this.wishlistRepository = wishlistRepository;
    }

    @Override
    public void run(String... args) {
        // 1. Seed Users (Admin, 2 Sellers, 2 Buyers)
        User admin = null;
        User techSeller = null;
        User styleSeller = null;
        User buyer1 = null;
        User buyer2 = null;

        if (userRepository.count() == 0) {
            logger.info("Database users empty. Seeding initial Admin, Sellers, and Buyers...");

            admin = new User("admin", "admin123", "admin@dharwinmart.com", "System Administrator", "ADMIN");
            techSeller = new User("techseller", "seller123", "techseller@dharwinmart.com", "Apex Tech Solutions", "SELLER", "9876500001", "Technology Park, Suite 402");
            styleSeller = new User("styleseller", "seller123", "styleseller@dharwinmart.com", "Campus Trendz & Living", "SELLER", "9876500002", "Fashion Street, Shop 18");
            buyer1 = new User("buyer1", "buyer123", "dharwin@example.com", "Dharwin J", "BUYER", "9876543210", "Hostel Block B, Room 204, Campus");
            buyer2 = new User("buyer2", "buyer123", "anita@example.com", "Anita Sharma", "BUYER", "9123456780", "Staff Quarters 12, West Campus");

            userRepository.saveAll(Arrays.asList(admin, techSeller, styleSeller, buyer1, buyer2));
            logger.info("Successfully seeded 5 initial user accounts!");
        } else {
            admin = userRepository.findByUsernameIgnoreCase("admin").orElse(null);
            techSeller = userRepository.findByUsernameIgnoreCase("techseller").orElse(null);
            styleSeller = userRepository.findByUsernameIgnoreCase("styleseller").orElse(null);
            buyer1 = userRepository.findByUsernameIgnoreCase("buyer1").orElse(null);
            buyer2 = userRepository.findByUsernameIgnoreCase("buyer2").orElse(null);
        }

        // 2. Seed Products
        if (productRepository.count() == 0) {
            logger.info("Database products empty. Seeding catalog products...");

            List<Product> sampleProducts = Arrays.asList(
                new Product(
                    "UltraSlim Pro Laptop 15",
                    "High-performance laptop featuring 16GB RAM, 512GB NVMe SSD, and vivid IPS display for productivity and gaming.",
                    new BigDecimal("84999.00"),
                    "https://images.unsplash.com/photo-1496181133206-80ce9b88a853?w=600&auto=format&fit=crop&q=80",
                    "Electronics",
                    15,
                    techSeller
                ),
                new Product(
                    "Smartphone Galaxy Pro 5G",
                    "Next-gen smartphone with AMOLED 120Hz display, 108MP AI triple camera, and 5000mAh all-day battery.",
                    new BigDecimal("42999.00"),
                    "https://images.unsplash.com/photo-1511707171634-5f897ff02aa9?w=600&auto=format&fit=crop&q=80",
                    "Electronics",
                    22,
                    techSeller
                ),
                new Product(
                    "Wireless ANC Headphones",
                    "Premium active noise cancellation headphones with 40-hour battery life and ultra-comfortable memory foam ear cushions.",
                    new BigDecimal("6999.00"),
                    "https://images.unsplash.com/photo-1505740420928-5e560c06d30e?w=600&auto=format&fit=crop&q=80",
                    "Electronics",
                    30,
                    techSeller
                ),
                new Product(
                    "Smart Fitness Watch V2",
                    "Fitness companion with heart rate monitor, SpO2 sensor, sleep tracking, built-in GPS, and 5ATM water resistance.",
                    new BigDecimal("3499.00"),
                    "https://images.unsplash.com/photo-1523275335684-37898b6baf30?w=600&auto=format&fit=crop&q=80",
                    "Electronics",
                    25,
                    techSeller
                ),
                new Product(
                    "Tablet Ultra 11-inch",
                    "Crisp Retina-grade display tablet with stylus support, stereo quad-speakers, and octa-core processor.",
                    new BigDecimal("28999.00"),
                    "https://images.unsplash.com/photo-1544244015-0df4b3ffc6b0?w=600&auto=format&fit=crop&q=80",
                    "Electronics",
                    12,
                    techSeller
                ),
                new Product(
                    "Mechanical RGB Gaming Keyboard",
                    "Tactile mechanical switches with customizable per-key RGB backlighting and durable aircraft-grade aluminum top plate.",
                    new BigDecimal("3899.00"),
                    "https://images.unsplash.com/photo-1587829741301-dc798b83add3?w=600&auto=format&fit=crop&q=80",
                    "Accessories",
                    18,
                    techSeller
                ),
                new Product(
                    "Ergonomic Wireless Mouse",
                    "Precision 4000 DPI sensor with silent click buttons, multi-device Bluetooth pairing, and ergonomic thumb rest.",
                    new BigDecimal("1499.00"),
                    "https://images.unsplash.com/photo-1527864550417-7fd91fc51a46?w=600&auto=format&fit=crop&q=80",
                    "Accessories",
                    40,
                    techSeller
                ),
                new Product(
                    "Water-Resistant Everyday Backpack",
                    "Spacious 25L commuter backpack with padded 15.6-inch laptop compartment, USB charging port, and anti-theft pockets.",
                    new BigDecimal("2199.00"),
                    "https://images.unsplash.com/photo-1553062407-98eeb64c6a62?w=600&auto=format&fit=crop&q=80",
                    "Accessories",
                    20,
                    styleSeller
                ),
                new Product(
                    "Pro Performance Running Shoes",
                    "Lightweight athletic sneakers engineered with responsive energy-return foam and breathable mesh upper.",
                    new BigDecimal("2999.00"),
                    "https://images.unsplash.com/photo-1542291026-7eec264c27ff?w=600&auto=format&fit=crop&q=80",
                    "Fashion",
                    28,
                    styleSeller
                ),
                new Product(
                    "Classic Organic Cotton T-Shirt",
                    "100% premium combed organic cotton t-shirt with tailored fit, ribbed collar, and ultra-soft pre-washed finish.",
                    new BigDecimal("799.00"),
                    "https://images.unsplash.com/photo-1521572267360-ee0c2909d518?w=600&auto=format&fit=crop&q=80",
                    "Fashion",
                    50,
                    styleSeller
                ),
                new Product(
                    "Modern Java Programming Guide",
                    "Comprehensive hands-on guide covering Java 17+, Spring Boot 3, REST APIs, design patterns, and clean architecture.",
                    new BigDecimal("1299.00"),
                    "https://images.unsplash.com/photo-1544716278-ca5e3f4abd8c?w=600&auto=format&fit=crop&q=80",
                    "Books",
                    35,
                    styleSeller
                ),
                new Product(
                    "Minimalist LED Desk Lamp",
                    "Eye-caring dimmable LED architect lamp featuring touch controls, 5 color modes, flexible gooseneck, and timer.",
                    new BigDecimal("1899.00"),
                    "https://images.unsplash.com/photo-1507473885765-e6ed057f782c?w=600&auto=format&fit=crop&q=80",
                    "Home",
                    16,
                    styleSeller
                ),
                new Product(
                    "Stainless Steel Insulated Bottle",
                    "Double-wall vacuum insulated 750ml thermal water flask keeping beverages ice cold for 24h or hot for 12h.",
                    new BigDecimal("999.00"),
                    "https://images.unsplash.com/photo-1602143407151-7111542de6e8?w=600&auto=format&fit=crop&q=80",
                    "Home",
                    45,
                    styleSeller
                ),
                new Product(
                    "Out of Stock Demo Item",
                    "Sample product with 0 stock to demonstrate the out of stock badge and disabled cart button functionality.",
                    new BigDecimal("499.00"),
                    "https://images.unsplash.com/photo-1583394838336-acd977736f90?w=600&auto=format&fit=crop&q=80",
                    "Accessories",
                    0,
                    techSeller
                )
            );

            productRepository.saveAll(sampleProducts);
            logger.info("Successfully seeded {} products with seller associations!", sampleProducts.size());
        } else {
            // Ensure existing products have seller associations if missing
            List<Product> existingProducts = productRepository.findAll();
            boolean updated = false;
            for (Product p : existingProducts) {
                if (p.getSeller() == null && techSeller != null && styleSeller != null) {
                    if ("Fashion".equalsIgnoreCase(p.getCategory()) || "Home".equalsIgnoreCase(p.getCategory()) || "Books".equalsIgnoreCase(p.getCategory())) {
                        p.setSeller(styleSeller);
                    } else {
                        p.setSeller(techSeller);
                    }
                    if (p.getApprovalStatus() == null) {
                        p.setApprovalStatus("APPROVED");
                    }
                    updated = true;
                }
            }
            if (updated) {
                productRepository.saveAll(existingProducts);
                logger.info("Associated existing products with sellers.");
            }
        }

        // 3. Seed sample wishlist for buyer1 if empty
        if (buyer1 != null && wishlistRepository.countByUserId(buyer1.getId()) == 0) {
            List<Product> products = productRepository.findAll();
            if (products.size() >= 3) {
                Wishlist w1 = new Wishlist(buyer1, products.get(0));
                Wishlist w2 = new Wishlist(buyer1, products.get(2));
                wishlistRepository.saveAll(Arrays.asList(w1, w2));
                logger.info("Seeded initial wishlist items for demo buyer1.");
            }
        }
    }
}
