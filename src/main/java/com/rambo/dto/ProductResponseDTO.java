package com.rambo.dto;

import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductResponseDTO {

    private Long id;
    private String name;
    private String category;
    private BigDecimal purchasePrice;
    private BigDecimal salePrice;
    private Integer stock;
    private Integer minimumStock;
    private BigDecimal profitMargin;   // calculated field
    private boolean lowStock;          // calculated field
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
