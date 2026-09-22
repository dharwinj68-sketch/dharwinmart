package com.dharwinmart.service;

import com.dharwinmart.entity.Product;
import com.dharwinmart.exception.ResourceNotFoundException;
import com.dharwinmart.repository.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class ProductService {

    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
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
        return productRepository.save(product);
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

    public void deleteProduct(Long id) {
        if (!productRepository.existsById(id)) {
            throw new ResourceNotFoundException("Cannot delete: Product not found with ID: " + id);
        }
        productRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public long countProducts() {
        return productRepository.count();
    }
}
