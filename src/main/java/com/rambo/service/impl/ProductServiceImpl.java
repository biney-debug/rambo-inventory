package com.rambo.service.impl;

import com.rambo.dto.ProductRequestDTO;
import com.rambo.dto.ProductResponseDTO;
import com.rambo.entity.Product;
import com.rambo.exception.ConflictException;
import com.rambo.exception.ResourceNotFoundException;
import com.rambo.mapper.ProductMapper;
import com.rambo.repository.ProductRepository;
import com.rambo.service.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final ProductMapper productMapper;

    @Override
    @Transactional
    public ProductResponseDTO create(ProductRequestDTO dto) {
        log.debug("Creating product: {}", dto.getName());

        if (productRepository.existsByNameIgnoreCase(dto.getName())) {
            throw new ConflictException(
                "A product named '" + dto.getName() + "' already exists. " +
                "Use the restock endpoint to add more units."
            );
        }

        Product product = productMapper.toEntity(dto);
        Product saved   = productRepository.save(product);
        log.info("Product created with ID: {}", saved.getId());
        return productMapper.toResponseDTO(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public ProductResponseDTO findById(Long id) {
        log.debug("Fetching product ID: {}", id);
        return productMapper.toResponseDTO(findOrThrow(id));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ProductResponseDTO> findAll(Pageable pageable) {
        log.debug("Fetching all products page={} size={}", pageable.getPageNumber(), pageable.getPageSize());
        return productRepository.findAllByOrderByNameAsc(pageable)
                .map(productMapper::toResponseDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductResponseDTO> findByCategory(String category) {
        log.debug("Fetching products by category: {}", category);
        return productRepository.findByCategoryIgnoreCaseOrderByNameAsc(category)
                .stream()
                .map(productMapper::toResponseDTO)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductResponseDTO> findLowStock() {
        log.debug("Fetching low-stock products");
        return productRepository.findLowStockProducts()
                .stream()
                .map(productMapper::toResponseDTO)
                .toList();
    }

    @Override
    @Transactional
    public ProductResponseDTO update(Long id, ProductRequestDTO dto) {
        log.debug("Updating product ID: {}", id);
        Product product = findOrThrow(id);

        // If the name changed, make sure it doesn't collide with another product
        boolean nameChanged = !product.getName().equalsIgnoreCase(dto.getName());
        if (nameChanged && productRepository.existsByNameIgnoreCase(dto.getName())) {
            throw new ConflictException("Another product already has the name: " + dto.getName());
        }

        productMapper.updateEntityFromDTO(dto, product);
        Product updated = productRepository.save(product);
        log.info("Product ID {} updated", id);
        return productMapper.toResponseDTO(updated);
    }

    @Override
    @Transactional
    public ProductResponseDTO restock(Long id, Integer quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("Restock quantity must be greater than 0");
        }
        Product product = findOrThrow(id);
        product.setStock(product.getStock() + quantity);
        Product saved = productRepository.save(product);
        log.info("Product ID {} restocked by {}. New stock: {}", id, quantity, saved.getStock());
        return productMapper.toResponseDTO(saved);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        Product product = findOrThrow(id);
        productRepository.delete(product);
        log.info("Product ID {} deleted", id);
    }

    // Private helper: finds product or throws 404
    private Product findOrThrow(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with ID: " + id));
    }
}
