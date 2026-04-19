package com.easybuy.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.easybuy.entity.Product;
import com.easybuy.service.ProductService;
import com.easybuy.service.CartService;

@RestController
@RequestMapping("/api/products")
@CrossOrigin(origins = "https://easybuy-backend-85si.onrender.com")
public class ProductController {

    private final ProductService productService;
    private final CartService cartService;

    // ✅ SINGLE constructor (correct)
    public ProductController(ProductService productService, CartService cartService) {
        this.productService = productService;
        this.cartService = cartService;
    }

    // ✅ Get all or category
    @GetMapping
    public List<Product> getProducts(@RequestParam(required = false) String category) {
        if (category != null) {
            return productService.getByCategory(category);
        }
        return productService.getAllProducts();
    }

    // ✅ Get by ID
    @GetMapping("/{id}")
    public ResponseEntity<Product> getProductById(@PathVariable Long id) {
        Product product = productService.getProductById(id);
        return ResponseEntity.ok(product);
    }

    // 🔥 SEARCH API (FIXED)
    @GetMapping("/search")
    public List<Product> searchProducts(
            @RequestParam String query,
            @RequestParam(required = false, defaultValue = "") String sort
    ) {
        return productService.searchProducts(query, sort);
    }
}
