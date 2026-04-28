package com.rambo.service.impl;

import com.rambo.dto.DashboardDTO;
import com.rambo.mapper.ProductMapper;
import com.rambo.repository.ProductRepository;
import com.rambo.repository.SaleRepository;
import com.rambo.service.DashboardService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
@RequiredArgsConstructor
@Slf4j
public class DashboardServiceImpl implements DashboardService {

    private final ProductRepository productRepository;
    private final SaleRepository saleRepository;
    private final ProductMapper productMapper;

    @Override
    @Transactional(readOnly = true)
    public DashboardDTO getSummary() {
        log.debug("Building dashboard summary");

        // Total value of current stock (purchase_price * stock for each product)
        BigDecimal stockValue = productRepository.findAll()
                .stream()
                .map(p -> p.getPurchasePrice().multiply(BigDecimal.valueOf(p.getStock())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalRevenue       = saleRepository.sumTotalRevenue();
        BigDecimal netProfit          = saleRepository.sumTotalProfit();
        Long       totalTransactions  = saleRepository.countTotalTransactions();

        // Overall average margin
        BigDecimal averageMargin = BigDecimal.ZERO;
        if (totalRevenue.compareTo(BigDecimal.ZERO) > 0) {
            averageMargin = netProfit
                    .divide(totalRevenue, 4, RoundingMode.HALF_UP)
                    .multiply(BigDecimal.valueOf(100))
                    .setScale(2, RoundingMode.HALF_UP);
        }

        return DashboardDTO.builder()
                .totalProducts(productRepository.findAll().size())
                .totalStockValue(stockValue.setScale(2, RoundingMode.HALF_UP))
                .totalRevenue(totalRevenue)
                .netProfit(netProfit)
                .averageMargin(averageMargin)
                .totalTransactions(totalTransactions)
                .lowStockProducts(
                    productRepository.findLowStockProducts()
                        .stream()
                        .map(productMapper::toResponseDTO)
                        .toList()
                )
                .topProducts(saleRepository.findTopProfitableProducts())
                .build();
    }
}
