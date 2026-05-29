package com.rambo.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "sales")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Sale {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(nullable = false)
    private Integer quantity;

    // Unit price stored always in PEN (converted if sale was in USD)
    @Column(name = "unit_price", nullable = false, precision = 10, scale = 2)
    private BigDecimal unitPrice;

    @Column(name = "unit_cost", nullable = false, precision = 10, scale = 2)
    private BigDecimal unitCost;

    @Column(name = "total_amount", nullable = false, precision = 12, scale = 2)
    private BigDecimal totalAmount;

    @Column(name = "profit", nullable = false, precision = 12, scale = 2)
    private BigDecimal profit;

    // Currency in which the sale was originally entered
    @Enumerated(EnumType.STRING)
    @Column(name = "currency", nullable = false, length = 3, columnDefinition = "VARCHAR(3) DEFAULT 'PEN'")
    @Builder.Default
    private Currency currency = Currency.PEN;

    // Exchange rate used at time of sale (null when currency = PEN)
    @Column(name = "exchange_rate_used", precision = 10, scale = 4)
    private BigDecimal exchangeRateUsed;

    // Original unit price in the sale currency before PEN conversion (null when currency = PEN)
    @Column(name = "original_unit_price", precision = 10, scale = 2)
    private BigDecimal originalUnitPrice;

    @Column(length = 150)
    private String customer;

    @Column(length = 300)
    private String notes;

    @Column(name = "sold_at", updatable = false)
    private LocalDateTime soldAt;

    @PrePersist
    protected void onCreate() {
        soldAt = LocalDateTime.now();
    }

    public void calculateTotals() {
        if (unitPrice != null && quantity != null) {
            totalAmount = unitPrice.multiply(BigDecimal.valueOf(quantity));
        }
        if (unitPrice != null && unitCost != null && quantity != null) {
            profit = unitPrice.subtract(unitCost)
                    .multiply(BigDecimal.valueOf(quantity));
        }
    }
}
