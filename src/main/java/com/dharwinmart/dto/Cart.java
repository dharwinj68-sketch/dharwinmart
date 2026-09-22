package com.dharwinmart.dto;

import com.dharwinmart.entity.Product;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

public class Cart implements Serializable {
    private static final long serialVersionUID = 1L;

    private final Map<Long, CartItem> items = new LinkedHashMap<>();

    public void addItem(Product product, int quantity) {
        if (product == null || product.getId() == null || quantity <= 0) {
            return;
        }
        Long productId = product.getId();
        if (items.containsKey(productId)) {
            CartItem existing = items.get(productId);
            int newQty = existing.getQuantity() + quantity;
            // Cap at product stock if available
            if (product.getStock() != null && newQty > product.getStock()) {
                newQty = product.getStock();
            }
            existing.setQuantity(newQty);
        } else {
            int initialQty = quantity;
            if (product.getStock() != null && initialQty > product.getStock()) {
                initialQty = product.getStock();
            }
            items.put(productId, new CartItem(product, initialQty));
        }
    }

    public void updateQuantity(Long productId, int quantity) {
        if (productId == null) {
            return;
        }
        if (quantity <= 0) {
            items.remove(productId);
        } else if (items.containsKey(productId)) {
            CartItem item = items.get(productId);
            if (item.getProduct() != null && item.getProduct().getStock() != null) {
                if (quantity > item.getProduct().getStock()) {
                    quantity = item.getProduct().getStock();
                }
            }
            item.setQuantity(quantity);
        }
    }

    public void removeItem(Long productId) {
        if (productId != null) {
            items.remove(productId);
        }
    }

    public void clear() {
        items.clear();
    }

    public Collection<CartItem> getItems() {
        return items.values();
    }

    public CartItem getItem(Long productId) {
        return items.get(productId);
    }

    public int getTotalQuantity() {
        return items.values().stream().mapToInt(CartItem::getQuantity).sum();
    }

    public BigDecimal getTotalAmount() {
        return items.values().stream()
                .map(CartItem::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public boolean isEmpty() {
        return items.isEmpty();
    }
}
