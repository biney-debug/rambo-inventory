package com.rambo.dto;

import jakarta.validation.constraints.*;
import lombok.*;
import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SaleRequestDTO {

    @NotNull(message = "Product ID is required")
    private Long productId;

    @NotNull(message = "Quantity is required")
    @Min(value = 1, message = "Minimum quantity is 1")
    private Integer quantity;

    // Optional: if not provided, the current product sale price is used
    @DecimalMin(value = "0.01", message = "Unit price must be greater than 0")
    private BigDecimal unitPrice;

    @Size(max = 150, message = "Customer name must not exceed 150 characters")
    private String customer;

    @Size(max = 300, message = "Notes must not exceed 300 characters")
    private String notes;
}
