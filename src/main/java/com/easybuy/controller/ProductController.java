package com.easybuy.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.easybuy.entity.Product;
import com.easybuy.repository.ProductRepository;
import com.easybuy.service.CartService;
import com.easybuy.service.ProductService;

@RestController
@RequestMapping("/api/products")
@CrossOrigin(origins = "https://easybuy-frontend-ochre.vercel.app")
public class ProductController {

    private final ProductService productService;
    private final CartService cartService;
    private final ProductRepository productRepository;

    // ✅ Constructor
    public ProductController(ProductService productService,
                             CartService cartService,
                             ProductRepository productRepository) {
        this.productService = productService;
        this.cartService = cartService;
        this.productRepository = productRepository;
    }

    // ✅ Get all or category
    @GetMapping
    public List<Product> getProducts(@RequestParam(required = false) String category) {
        if (category != null) {
            return productService.getByCategory(category);
        }
        return productService.getAllProducts();
    }

    // ✅ Duplicate Check API
    @GetMapping("/duplicates")
    public List<Object[]> getDuplicates() {
        return productRepository.findDuplicateProducts();
    }

    // ✅ Get by ID
    @GetMapping("/{id}")
    public ResponseEntity<Product> getProductById(@PathVariable Long id) {
        Product product = productService.getProductById(id);
        return ResponseEntity.ok(product);
    }

    // ✅ Search API
    @GetMapping("/search")
    public List<Product> searchProducts(
            @RequestParam String query,
            @RequestParam(required = false, defaultValue = "") String sort) {

        return productService.searchProducts(query, sort);
    }
}
