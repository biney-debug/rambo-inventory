package com.rambo.service;

import com.rambo.dto.ProductRequestDTO;
import com.rambo.dto.ProductResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ProductService {

    ProductResponseDTO create(ProductRequestDTO dto);

    ProductResponseDTO findById(Long id);

    Page<ProductResponseDTO> findAll(Pageable pageable);

    List<ProductResponseDTO> findByCategory(String category);

    List<ProductResponseDTO> findLowStock();

    ProductResponseDTO update(Long id, ProductRequestDTO dto);

    // Restock: adds units to the current stock without touching other fields
    ProductResponseDTO restock(Long id, Integer quantity);

    void delete(Long id);
}
