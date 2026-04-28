package com.rambo.dto;

import lombok.*;
import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DashboardDTO {

    private Integer totalProducts;
    private BigDecimal totalStockValue;       // sum of (purchase_price * stock) for all products
    private BigDecimal totalRevenue;          // sum of total_amount across all sales
    private BigDecimal netProfit;             // sum of profit across all sales
    private BigDecimal averageMargin;         // net_profit / total_revenue * 100
    private Long totalTransactions;           // total number of recorded sales
    private List<ProductResponseDTO> lowStockProducts;    // products triggering the alert
    private List<TopProductDTO> topProducts;              // top 5 most profitable products
}
