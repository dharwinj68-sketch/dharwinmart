package com.dharwinmart.service;

import com.dharwinmart.entity.Product;
import com.dharwinmart.entity.User;
import com.dharwinmart.entity.Wishlist;
import com.dharwinmart.exception.ResourceNotFoundException;
import com.dharwinmart.repository.ProductRepository;
import com.dharwinmart.repository.UserRepository;
import com.dharwinmart.repository.WishlistRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
@Transactional
public class WishlistService {

    private final WishlistRepository wishlistRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;

    public WishlistService(WishlistRepository wishlistRepository,
                           UserRepository userRepository,
                           ProductRepository productRepository) {
        this.wishlistRepository = wishlistRepository;
        this.userRepository = userRepository;
        this.productRepository = productRepository;
    }

    /**
     * Toggles an item in the buyer's wishlist.
     * Returns true if the product was added, false if it was removed.
     */
    public boolean toggleWishlist(Long userId, Long productId) {
        Optional<Wishlist> existing = wishlistRepository.findByUserIdAndProductId(userId, productId);
        if (existing.isPresent()) {
            wishlistRepository.delete(existing.get());
            return false;
        } else {
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + userId));
            Product product = productRepository.findById(productId)
                    .orElseThrow(() -> new ResourceNotFoundException("Product not found with ID: " + productId));
            Wishlist wishlist = new Wishlist(user, product);
            wishlistRepository.save(wishlist);
            return true;
        }
    }

    /**
     * Explicitly adds product to user wishlist (idempotent; prevents duplicate).
     */
    public boolean addToWishlist(Long userId, Long productId) {
        if (wishlistRepository.existsByUserIdAndProductId(userId, productId)) {
            return false; // Already present
        }
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + userId));
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with ID: " + productId));
        wishlistRepository.save(new Wishlist(user, product));
        return true;
    }

    /**
     * Removes product from user wishlist.
     */
    public boolean removeFromWishlist(Long userId, Long productId) {
        Optional<Wishlist> existing = wishlistRepository.findByUserIdAndProductId(userId, productId);
        if (existing.isPresent()) {
            wishlistRepository.delete(existing.get());
            return true;
        }
        return false;
    }

    @Transactional(readOnly = true)
    public List<Wishlist> getWishlistForUser(Long userId) {
        return wishlistRepository.findByUserIdOrderByCreatedAtDesc(userId);
    }

    @Transactional(readOnly = true)
    public boolean isWishlisted(Long userId, Long productId) {
        if (userId == null || productId == null) {
            return false;
        }
        return wishlistRepository.existsByUserIdAndProductId(userId, productId);
    }

    @Transactional(readOnly = true)
    public Set<Long> getWishlistedProductIds(Long userId) {
        if (userId == null) {
            return new HashSet<>();
        }
        return new HashSet<>(wishlistRepository.findProductIdsByUserId(userId));
    }

    @Transactional(readOnly = true)
    public long getWishlistCount(Long userId) {
        if (userId == null) {
            return 0;
        }
        return wishlistRepository.countByUserId(userId);
    }
}
