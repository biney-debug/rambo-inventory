package com.rambo.service.impl;

import com.rambo.dto.SaleRequestDTO;
import com.rambo.dto.SaleResponseDTO;
import com.rambo.entity.Product;
import com.rambo.entity.Sale;
import com.rambo.exception.InsufficientStockException;
import com.rambo.exception.ResourceNotFoundException;
import com.rambo.mapper.SaleMapper;
import com.rambo.repository.ProductRepository;
import com.rambo.repository.SaleRepository;
import com.rambo.service.SaleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class SaleServiceImpl implements SaleService {

    private final SaleRepository saleRepository;
    private final ProductRepository productRepository;
    private final SaleMapper saleMapper;

    @Override
    @Transactional
    public SaleResponseDTO register(SaleRequestDTO dto) {
        log.debug("Registering sale for product ID: {}", dto.getProductId());

        // 1. Find the product
        Product product = productRepository.findById(dto.getProductId())
                .orElseThrow(() -> new ResourceNotFoundException(
                    "Product not found with ID: " + dto.getProductId()
                ));

        // 2. Check there is enough stock
        if (product.getStock() < dto.getQuantity()) {
            throw new InsufficientStockException(
                "Insufficient stock for '" + product.getName() + "'. " +
                "Available: " + product.getStock() + ", requested: " + dto.getQuantity()
            );
        }

        // 3. Determine the sale price (use the one sent, or fall back to the current product price)
        BigDecimal salePrice = (dto.getUnitPrice() != null)
                ? dto.getUnitPrice()
                : product.getSalePrice();

        // 4. Build the sale with price snapshots
        Sale sale = Sale.builder()
                .product(product)
                .quantity(dto.getQuantity())
                .unitPrice(salePrice)
                .unitCost(product.getPurchasePrice())   // snapshot of the current cost
                .customer(dto.getCustomer())
                .notes(dto.getNotes())
                .build();
        sale.calculateTotals();

        // 5. Deduct stock from the product
        product.setStock(product.getStock() - dto.getQuantity());
        productRepository.save(product);

        // 6. Persist the sale
        Sale saved = saleRepository.save(sale);
        log.info("Sale registered ID: {} | Product: {} | Qty: {} | Total: {}",
                saved.getId(), product.getName(), dto.getQuantity(), saved.getTotalAmount());

        return saleMapper.toResponseDTO(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public SaleResponseDTO findById(Long id) {
        log.debug("Fetching sale ID: {}", id);
        Sale sale = saleRepository.findByIdWithProduct(id)
                .orElseThrow(() -> new ResourceNotFoundException("Sale not found with ID: " + id));
        return saleMapper.toResponseDTO(sale);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<SaleResponseDTO> findAll(Pageable pageable) {
        log.debug("Fetching all sales page={} size={}", pageable.getPageNumber(), pageable.getPageSize());
        return saleRepository.findAllWithProduct(pageable)
                .map(saleMapper::toResponseDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public List<SaleResponseDTO> findByProduct(Long productId) {
        log.debug("Fetching sales for product ID: {}", productId);
        if (!productRepository.existsById(productId)) {
            throw new ResourceNotFoundException("Product not found with ID: " + productId);
        }
        return saleRepository.findByProductIdOrderBySoldAtDesc(productId)
                .stream()
                .map(saleMapper::toResponseDTO)
                .toList();
    }
}
