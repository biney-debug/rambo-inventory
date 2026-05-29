package com.rambo.dto;

import com.rambo.entity.Currency;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SaleResponseDTO {

    private Long id;
    private Long productId;
    private String productName;
    private Integer quantity;
    private BigDecimal unitPrice;       // always PEN
    private BigDecimal unitCost;        // always PEN
    private BigDecimal totalAmount;     // always PEN
    private BigDecimal profit;          // always PEN
    private Currency currency;
    private BigDecimal exchangeRateUsed;
    private BigDecimal originalUnitPrice; // price in original currency (null when PEN)
    private String customer;
    private String notes;
    private LocalDateTime soldAt;
}
