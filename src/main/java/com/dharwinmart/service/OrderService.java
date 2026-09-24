package com.dharwinmart.service;

import com.dharwinmart.dto.Cart;
import com.dharwinmart.dto.CartItem;
import com.dharwinmart.dto.CheckoutForm;
import com.dharwinmart.entity.Order;
import com.dharwinmart.entity.OrderItem;
import com.dharwinmart.entity.Product;
import com.dharwinmart.entity.User;
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

    public Order placeOrder(Cart cart, CheckoutForm checkoutForm) {
        return placeOrder(cart, checkoutForm, null);
    }

    /**
     * Executes the complete checkout flow transactionally:
     * 1. Validates cart is not empty
     * 2. Checks sufficient stock for each item
     * 3. Calculates total amount
     * 4. Creates Order and OrderItems (linking user if logged in)
     * 5. Reduces product stock in database
     * 6. Saves and persists Order
     */
    public Order placeOrder(Cart cart, CheckoutForm checkoutForm, User user) {
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
        if (user != null) {
            order.setUser(user);
        }

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
    public List<Order> getOrdersByUserId(Long userId) {
        return orderRepository.findByUserIdOrderByOrderDateDesc(userId);
    }

    @Transactional(readOnly = true)
    public List<Order> getOrdersForSeller(Long sellerId) {
        return orderRepository.findOrdersBySellerId(sellerId);
    }

    @Transactional(readOnly = true)
    public long countOrders() {
        return orderRepository.count();
    }

    @Transactional(readOnly = true)
    public long countOrdersByUserId(Long userId) {
        return orderRepository.countByUserId(userId);
    }

    @Transactional(readOnly = true)
    public BigDecimal calculateTotalRevenue() {
        List<Order> orders = orderRepository.findAll();
        BigDecimal total = BigDecimal.ZERO;
        for (Order o : orders) {
            if (o.getTotalAmount() != null) {
                total = total.add(o.getTotalAmount());
            }
        }
        return total;
    }

    @Transactional(readOnly = true)
    public BigDecimal calculateSellerRevenue(Long sellerId) {
        List<Order> sellerOrders = getOrdersForSeller(sellerId);
        BigDecimal total = BigDecimal.ZERO;
        for (Order o : sellerOrders) {
            for (OrderItem item : o.getItems()) {
                if (item.getProduct() != null && item.getProduct().getSeller() != null
                        && item.getProduct().getSeller().getId().equals(sellerId)) {
                    total = total.add(item.getSubtotal());
                }
            }
        }
        return total;
    }
}
