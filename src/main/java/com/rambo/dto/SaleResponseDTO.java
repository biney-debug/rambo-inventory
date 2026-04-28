package com.rambo.dto;

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
    private String productName;    // product name at the time of the sale
    private Integer quantity;
    private BigDecimal unitPrice;
    private BigDecimal unitCost;
    private BigDecimal totalAmount;
    private BigDecimal profit;
    private String customer;
    private String notes;
    private LocalDateTime soldAt;
}
