package com.rambo.dto;

import jakarta.validation.constraints.*;
import lombok.*;
import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductRequestDTO {

    @NotBlank(message = "Product name is required")
    @Size(max = 150, message = "Product name must not exceed 150 characters")
    private String name;

    @NotBlank(message = "Category is required")
    @Size(max = 80, message = "Category must not exceed 80 characters")
    private String category;

    @NotNull(message = "Purchase price is required")
    @DecimalMin(value = "0.01", message = "Purchase price must be greater than 0")
    private BigDecimal purchasePrice;

    @NotNull(message = "Sale price is required")
    @DecimalMin(value = "0.01", message = "Sale price must be greater than 0")
    private BigDecimal salePrice;

    @NotNull(message = "Stock is required")
    @Min(value = 0, message = "Stock cannot be negative")
    private Integer stock;

    @NotNull(message = "Minimum stock is required")
    @Min(value = 0, message = "Minimum stock cannot be negative")
    private Integer minimumStock;
}
