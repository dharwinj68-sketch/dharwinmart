package com.dharwinmart.repository;

import com.dharwinmart.entity.Product;
import com.dharwinmart.entity.User;
import com.dharwinmart.entity.Wishlist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface WishlistRepository extends JpaRepository<Wishlist, Long> {

    List<Wishlist> findByUserOrderByCreatedAtDesc(User user);

    List<Wishlist> findByUserIdOrderByCreatedAtDesc(Long userId);

    Optional<Wishlist> findByUserIdAndProductId(Long userId, Long productId);

    boolean existsByUserIdAndProductId(Long userId, Long productId);

    void deleteByUserIdAndProductId(Long userId, Long productId);

    @Modifying
    @Query("DELETE FROM Wishlist w WHERE w.product.id = :productId")
    void deleteByProductId(@Param("productId") Long productId);

    long countByUserId(Long userId);

    @Query("SELECT w.product.id FROM Wishlist w WHERE w.user.id = :userId")
    List<Long> findProductIdsByUserId(@Param("userId") Long userId);
}
