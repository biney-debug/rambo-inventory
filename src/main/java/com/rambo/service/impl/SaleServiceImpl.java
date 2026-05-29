package com.rambo.service.impl;

import com.rambo.dto.SaleRequestDTO;
import com.rambo.dto.SaleResponseDTO;
import com.rambo.entity.Currency;
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

        Product product = productRepository.findById(dto.getProductId())
                .orElseThrow(() -> new ResourceNotFoundException(
                    "Product not found with ID: " + dto.getProductId()
                ));

        if (product.getStock() < dto.getQuantity()) {
            throw new InsufficientStockException(
                "Insufficient stock for '" + product.getName() + "'. " +
                "Available: " + product.getStock() + ", requested: " + dto.getQuantity()
            );
        }

        Currency currency = dto.getCurrency() != null ? dto.getCurrency() : Currency.PEN;

        BigDecimal originalUnitPrice = null;
        BigDecimal exchangeRateUsed  = null;
        BigDecimal unitPriceInPen;

        if (currency == Currency.USD) {
            if (dto.getExchangeRate() == null || dto.getExchangeRate().compareTo(BigDecimal.ZERO) <= 0) {
                throw new IllegalArgumentException(
                    "Exchange rate is required and must be greater than 0 when currency is USD"
                );
            }
            BigDecimal inputPrice = dto.getUnitPrice() != null
                    ? dto.getUnitPrice()
                    : null; // handled below

            if (inputPrice == null) {
                throw new IllegalArgumentException(
                    "Unit price in USD is required when currency is USD"
                );
            }

            originalUnitPrice = inputPrice;
            exchangeRateUsed  = dto.getExchangeRate();
            unitPriceInPen    = inputPrice.multiply(exchangeRateUsed).setScale(2, java.math.RoundingMode.HALF_UP);
        } else {
            unitPriceInPen = dto.getUnitPrice() != null ? dto.getUnitPrice() : product.getSalePrice();
        }

        Sale sale = Sale.builder()
                .product(product)
                .quantity(dto.getQuantity())
                .unitPrice(unitPriceInPen)
                .unitCost(product.getPurchasePrice())
                .currency(currency)
                .exchangeRateUsed(exchangeRateUsed)
                .originalUnitPrice(originalUnitPrice)
                .customer(dto.getCustomer())
                .notes(dto.getNotes())
                .build();
        sale.calculateTotals();

        product.setStock(product.getStock() - dto.getQuantity());
        productRepository.save(product);

        Sale saved = saleRepository.save(sale);
        log.info("Sale registered ID: {} | Product: {} | Qty: {} | Currency: {} | Total (PEN): {}",
                saved.getId(), product.getName(), dto.getQuantity(), currency, saved.getTotalAmount());

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

    @Override
    @Transactional
    public void delete(Long id) {
        Sale sale = saleRepository.findByIdWithProduct(id)
                .orElseThrow(() -> new ResourceNotFoundException("Sale not found with ID: " + id));
        Product product = sale.getProduct();
        product.setStock(product.getStock() + sale.getQuantity());
        productRepository.save(product);
        saleRepository.delete(sale);
        log.info("Sale ID {} deleted, stock restored for product ID {}", id, product.getId());
    }
}
