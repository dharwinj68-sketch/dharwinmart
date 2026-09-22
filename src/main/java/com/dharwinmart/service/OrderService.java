package com.dharwinmart.service;

import com.dharwinmart.dto.Cart;
import com.dharwinmart.dto.CartItem;
import com.dharwinmart.dto.CheckoutForm;
import com.dharwinmart.entity.Order;
import com.dharwinmart.entity.OrderItem;
import com.dharwinmart.entity.Product;
import com.dharwinmart.exception.InsufficientStockException;
import com.dharwinmart.exception.ResourceNotFoundException;
import com.dharwinmart.repository.OrderRepository;
import com.dharwinmart.repository.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class OrderService {

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;

    public OrderService(OrderRepository orderRepository, ProductRepository productRepository) {
        this.orderRepository = orderRepository;
        this.productRepository = productRepository;
    }

    /**
     * Executes the complete checkout flow transactionally:
     * 1. Validates cart is not empty
     * 2. Checks sufficient stock for each item
     * 3. Calculates total amount
     * 4. Creates Order and OrderItems
     * 5. Reduces product stock in database
     * 6. Saves and persists Order
     */
    public Order placeOrder(Cart cart, CheckoutForm checkoutForm) {
        if (cart == null || cart.isEmpty()) {
            throw new IllegalStateException("Cart is empty. Cannot place order.");
        }

        // 1 & 2. Verify stock availability for all cart items first
        for (CartItem item : cart.getItems()) {
            Product product = productRepository.findById(item.getProduct().getId())
                    .orElseThrow(() -> new ResourceNotFoundException("Product not found with ID: " + item.getProduct().getId()));

            if (product.getStock() < item.getQuantity()) {
                throw new InsufficientStockException(
                        "Insufficient stock for '" + product.getName() + "'. Available: " +
                        product.getStock() + ", Requested: " + item.getQuantity()
                );
            }
        }

        // 3. Calculate total
        BigDecimal totalAmount = cart.getTotalAmount();

        // 4. Create Order
        Order order = new Order();
        order.setCustomerName(checkoutForm.getCustomerName());
        order.setEmail(checkoutForm.getEmail());
        order.setPhone(checkoutForm.getPhone());
        order.setAddress(checkoutForm.getAddress());
        order.setTotalAmount(totalAmount);
        order.setOrderDate(LocalDateTime.now());
        order.setStatus("PLACED");

        // 5 & 6. Create OrderItems and reduce stock
        for (CartItem item : cart.getItems()) {
            Product product = productRepository.findById(item.getProduct().getId()).get();

            // Reduce stock
            product.setStock(product.getStock() - item.getQuantity());
            productRepository.save(product);

            // Create OrderItem with price snapshot
            OrderItem orderItem = new OrderItem(order, product, item.getQuantity(), product.getPrice());
            order.addItem(orderItem);
        }

        // 7. Save and return order
        return orderRepository.save(order);
    }

    @Transactional(readOnly = true)
    public Order getOrderById(Long id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with ID: " + id));
    }

    @Transactional(readOnly = true)
    public List<Order> getAllOrders() {
        return orderRepository.findAllByOrderByOrderDateDesc();
    }

    @Transactional(readOnly = true)
    public List<Order> getRecentOrders() {
        return orderRepository.findTop5ByOrderByOrderDateDesc();
    }

    @Transactional(readOnly = true)
    public long countOrders() {
        return orderRepository.count();
    }
}
