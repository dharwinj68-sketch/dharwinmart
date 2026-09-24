package com.dharwinmart.service;

import com.dharwinmart.entity.Product;
import com.dharwinmart.entity.User;
import com.dharwinmart.exception.AccessDeniedException;
import com.dharwinmart.exception.ResourceNotFoundException;
import com.dharwinmart.repository.ProductRepository;
import com.dharwinmart.repository.WishlistRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class ProductService {

    private final ProductRepository productRepository;
    private final WishlistRepository wishlistRepository;

    public ProductService(ProductRepository productRepository, WishlistRepository wishlistRepository) {
        this.productRepository = productRepository;
        this.wishlistRepository = wishlistRepository;
    }

    @Transactional(readOnly = true)
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Product getProductById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with ID: " + id));
    }

    @Transactional(readOnly = true)
    public List<Product> searchProducts(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return getAllProducts();
        }
        return productRepository.searchByKeyword(keyword.trim());
    }

    @Transactional(readOnly = true)
    public List<Product> getProductsByCategory(String category) {
        if (category == null || category.trim().isEmpty() || category.equalsIgnoreCase("All")) {
            return getAllProducts();
        }
        return productRepository.findByCategoryIgnoreCase(category.trim());
    }

    @Transactional(readOnly = true)
    public List<String> getAllCategories() {
        return productRepository.findDistinctCategories();
    }

    @Transactional(readOnly = true)
    public List<Product> getFeaturedProducts() {
        return productRepository.findTop8ByOrderByIdAsc();
    }

    public Product createProduct(Product product) {
        if (product.getImageUrl() == null || product.getImageUrl().trim().isEmpty()) {
            product.setImageUrl("https://images.unsplash.com/photo-1523275335684-37898b6baf30?w=600&auto=format&fit=crop&q=60");
        }
        if (product.getApprovalStatus() == null) {
            product.setApprovalStatus("APPROVED");
        }
        return productRepository.save(product);
    }

    public Product createSellerProduct(Product product, User seller) {
        product.setSeller(seller);
        product.setApprovalStatus("APPROVED"); // Approved by default or ready for listing
        return createProduct(product);
    }

    public Product updateProduct(Long id, Product updatedData) {
        Product existing = getProductById(id);
        existing.setName(updatedData.getName());
        existing.setDescription(updatedData.getDescription());
        existing.setPrice(updatedData.getPrice());
        existing.setCategory(updatedData.getCategory());
        existing.setStock(updatedData.getStock());
        if (updatedData.getImageUrl() != null && !updatedData.getImageUrl().trim().isEmpty()) {
            existing.setImageUrl(updatedData.getImageUrl().trim());
        }
        return productRepository.save(existing);
    }

    public Product updateSellerProduct(Long id, Product updatedData, Long sellerId) {
        Product existing = getProductById(id);
        if (existing.getSeller() == null || !existing.getSeller().getId().equals(sellerId)) {
            throw new AccessDeniedException("Unauthorized: You do not own this product and cannot modify it.");
        }
        existing.setName(updatedData.getName());
        existing.setDescription(updatedData.getDescription());
        existing.setPrice(updatedData.getPrice());
        existing.setCategory(updatedData.getCategory());
        existing.setStock(updatedData.getStock());
        if (updatedData.getImageUrl() != null && !updatedData.getImageUrl().trim().isEmpty()) {
            existing.setImageUrl(updatedData.getImageUrl().trim());
        }
        return productRepository.save(existing);
    }

    public void deleteProduct(Long id) {
        if (!productRepository.existsById(id)) {
            throw new ResourceNotFoundException("Cannot delete: Product not found with ID: " + id);
        }
        wishlistRepository.deleteByProductId(id);
        productRepository.deleteById(id);
    }

    public void deleteSellerProduct(Long id, Long sellerId) {
        Product existing = getProductById(id);
        if (existing.getSeller() == null || !existing.getSeller().getId().equals(sellerId)) {
            throw new AccessDeniedException("Unauthorized: You do not own this product and cannot delete it.");
        }
        wishlistRepository.deleteByProductId(id);
        productRepository.deleteById(id);
    }

    public Product setApprovalStatus(Long id, String status) {
        Product product = getProductById(id);
        product.setApprovalStatus(status != null ? status.toUpperCase() : "APPROVED");
        return productRepository.save(product);
    }

    @Transactional(readOnly = true)
    public List<Product> getProductsBySellerId(Long sellerId) {
        return productRepository.findBySellerIdOrderByIdDesc(sellerId);
    }

    @Transactional(readOnly = true)
    public long countProductsBySellerId(Long sellerId) {
        return productRepository.countBySellerId(sellerId);
    }

    @Transactional(readOnly = true)
    public long countProducts() {
        return productRepository.count();
    }
}
