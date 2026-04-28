package com.rambo.dto;

import lombok.*;
import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TopProductDTO {

    private Long productId;
    private String name;
    private Long unitsSold;
    private BigDecimal totalRevenue;
    private BigDecimal totalProfit;
    private BigDecimal profitMargin;
}
