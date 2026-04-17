package com.easybuy.service;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.easybuy.entity.Product;
import com.easybuy.repository.ProductRepository;

@Service
public class ProductService {

    private final ProductRepository repo;

    public ProductService(ProductRepository repo) {
        this.repo = repo;
    }

    public List<Product> getAllProducts() {
        return repo.findAll();
    }

    public List<Product> getByCategory(String category) {
        return repo.findByCategoryIgnoreCase(category);
    }

    public Product getProductById(Long id) {
        return repo.findById(id)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Product not found"
                        )
                );
    }
    public List<Product> searchProducts(String query, String sort) {

        List<Product> products =
            repo.findByNameContainingIgnoreCaseOrCategoryContainingIgnoreCaseOrBrandContainingIgnoreCaseOrDescriptionContainingIgnoreCase(
                query, query, query, query
            );

        // 🔽 Sorting
        if ("priceLow".equals(sort)) {
            products.sort((a, b) -> Double.compare(a.getPrice(), b.getPrice()));
        } 
        else if ("priceHigh".equals(sort)) {
            products.sort((a, b) -> Double.compare(b.getPrice(), a.getPrice()));
        }

        return products;
    }
}
