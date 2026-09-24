package com.dharwinmart.repository;

import com.dharwinmart.entity.Product;
import com.dharwinmart.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    List<Product> findByCategoryIgnoreCase(String category);

    @Query("SELECT p FROM Product p WHERE " +
           "LOWER(p.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(p.description) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<Product> searchByKeyword(@Param("keyword") String keyword);

    @Query("SELECT DISTINCT p.category FROM Product p ORDER BY p.category ASC")
    List<String> findDistinctCategories();

    List<Product> findTop8ByOrderByIdAsc();

    List<Product> findBySellerOrderByIdDesc(User seller);

    List<Product> findBySellerIdOrderByIdDesc(Long sellerId);

    long countBySeller(User seller);

    long countBySellerId(Long sellerId);

    List<Product> findByApprovalStatusIgnoreCase(String approvalStatus);

    long countByApprovalStatusIgnoreCase(String approvalStatus);
}
