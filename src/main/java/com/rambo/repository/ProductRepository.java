package com.rambo.repository;

import com.rambo.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    // Find by name (case-insensitive) to prevent duplicates
    Optional<Product> findByNameIgnoreCase(String name);

    // Products with low or depleted stock
    @Query("SELECT p FROM Product p WHERE p.stock <= p.minimumStock ORDER BY p.stock ASC")
    List<Product> findLowStockProducts();

    // Filter by category
    List<Product> findByCategoryIgnoreCaseOrderByNameAsc(String category);

    // All products sorted alphabetically
    List<Product> findAllByOrderByNameAsc();

    // Check if a product with that name already exists (to prevent duplicates on create)
    boolean existsByNameIgnoreCase(String name);
}
