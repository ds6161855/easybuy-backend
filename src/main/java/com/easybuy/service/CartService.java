package com.easybuy.service;

import org.springframework.stereotype.Service;

import com.easybuy.entity.Cart;
import com.easybuy.repository.CartRepository;
import com.easybuy.dto.CartDTO;

import java.util.List;

@Service
public class CartService {

    private final CartRepository cartRepository;

    public CartService(CartRepository cartRepository) {
        this.cartRepository = cartRepository;
    }

    // ✅ ADD TO CART
    public Cart addToCart(String userId, Long productId, int quantity) {

        if (productId == null) {
            throw new RuntimeException("Product ID is NULL ❌");
        }

        Cart existing = cartRepository.findByUserIdAndProductId(userId, productId);

        if (existing != null) {
            existing.quantity += quantity;
            return cartRepository.save(existing);
        }

        Cart cart = new Cart();
        cart.userId = userId;
        cart.productId = productId;
        cart.quantity = quantity;

        return cartRepository.save(cart);
    }

    // ✅ GET CART
    public List<CartDTO> getUserCart(String userId) {
        return cartRepository.getCartWithProduct(userId);
    }

    // ✅ REMOVE
    public void removeFromCart(String userId, Long productId) {
        cartRepository.deleteByUserIdAndProductId(userId, productId);
    }
}