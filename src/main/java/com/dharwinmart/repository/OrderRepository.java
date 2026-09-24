package com.dharwinmart.repository;

import com.dharwinmart.entity.Order;
import com.dharwinmart.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findAllByOrderByOrderDateDesc();
    List<Order> findTop5ByOrderByOrderDateDesc();

    List<Order> findByUserOrderByOrderDateDesc(User user);
    List<Order> findByUserIdOrderByOrderDateDesc(Long userId);
    long countByUserId(Long userId);

    @Query("SELECT DISTINCT o FROM Order o JOIN o.items i WHERE i.product.seller.id = :sellerId ORDER BY o.orderDate DESC")
    List<Order> findOrdersBySellerId(@Param("sellerId") Long sellerId);
}
