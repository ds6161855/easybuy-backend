package com.easybuy.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.easybuy.entity.Product;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    List<Product> findByCategoryIgnoreCase(String category);

    List<Product> findByNameContainingIgnoreCaseOrCategoryContainingIgnoreCaseOrBrandContainingIgnoreCaseOrDescriptionContainingIgnoreCase(
            String name,
            String category,
            String brand,
            String description
    );

    @Query("SELECT p.name, COUNT(p) FROM Product p GROUP BY p.name HAVING COUNT(p) > 1")
    List<Object[]> findDuplicateProducts();
}
