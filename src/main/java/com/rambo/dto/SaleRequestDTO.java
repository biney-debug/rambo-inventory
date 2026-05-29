package com.rambo.dto;

import com.rambo.entity.Currency;
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

    // Price in the selected currency (PEN or USD). If null, uses current product sale price (PEN).
    @DecimalMin(value = "0.01", message = "Unit price must be greater than 0")
    private BigDecimal unitPrice;

    // Defaults to PEN when not provided
    private Currency currency;

    // Required when currency = USD. The PEN/USD exchange rate to use for conversion.
    @DecimalMin(value = "0.01", message = "Exchange rate must be greater than 0")
    private BigDecimal exchangeRate;

    @Size(max = 150, message = "Customer name must not exceed 150 characters")
    private String customer;

    @Size(max = 300, message = "Notes must not exceed 300 characters")
    private String notes;
}
