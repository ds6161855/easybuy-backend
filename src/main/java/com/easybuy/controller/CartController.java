package com.easybuy.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.easybuy.entity.Cart;
import com.easybuy.service.CartService;
import com.easybuy.dto.CartDTO;
import com.easybuy.dto.CartRequest;

import java.util.List;

@RestController
@RequestMapping("/api/cart")
@CrossOrigin(origins = "https://easybuy-frontend-ochre.vercel.app")
public class CartController {

    private final CartService cartService;

    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    // ✅ ADD TO CART
    @PostMapping("/add")
    public ResponseEntity<?> addToCart(@RequestBody CartRequest request) {

        if (request.getUserId() == null || request.getUserId().trim().isEmpty()) {
            return ResponseEntity.badRequest().body("Invalid user ❌");
        }

        if (request.getProductId() == null) {
            return ResponseEntity.badRequest().body("Product missing ❌");
        }

        Cart cart = cartService.addToCart(
                request.getUserId(),
                request.getProductId(),
                request.getQuantity()
        );

        return ResponseEntity.ok(cart);
    }

    // ✅ GET CART
    @GetMapping("/{userId}")
    public List<CartDTO> getCart(@PathVariable String userId) {
        return cartService.getUserCart(userId);
    }

    // ✅ REMOVE ITEM
    @DeleteMapping("/remove")
    public ResponseEntity<?> removeCart(@RequestBody CartRequest request) {

        cartService.removeFromCart(
                request.getUserId(),
                request.getProductId()
        );

        return ResponseEntity.ok("Removed from cart ✅");
    }
}
