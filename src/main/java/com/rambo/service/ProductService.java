package com.rambo.service;

import com.rambo.dto.ProductRequestDTO;
import com.rambo.dto.ProductResponseDTO;

import java.util.List;

public interface ProductService {

    ProductResponseDTO create(ProductRequestDTO dto);

    ProductResponseDTO findById(Long id);

    List<ProductResponseDTO> findAll();

    List<ProductResponseDTO> findByCategory(String category);

    List<ProductResponseDTO> findLowStock();

    ProductResponseDTO update(Long id, ProductRequestDTO dto);

    // Restock: adds units to the current stock without touching other fields
    ProductResponseDTO restock(Long id, Integer quantity);

    void delete(Long id);
}
