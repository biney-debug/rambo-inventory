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

    // Relationship with Product (many sales can belong to the same product)
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(nullable = false)
    private Integer quantity;

    // Unit price at the time of the sale (may differ from current price)
    @Column(name = "unit_price", nullable = false, precision = 10, scale = 2)
    private BigDecimal unitPrice;

    // Unit cost at the time of the sale (snapshot of purchase price)
    @Column(name = "unit_cost", nullable = false, precision = 10, scale = 2)
    private BigDecimal unitCost;

    // total = unit_price * quantity
    @Column(name = "total_amount", nullable = false, precision = 12, scale = 2)
    private BigDecimal totalAmount;

    // profit = (unit_price - unit_cost) * quantity
    @Column(name = "profit", nullable = false, precision = 12, scale = 2)
    private BigDecimal profit;

    // Customer is optional
    @Column(length = 150)
    private String customer;

    // Additional notes (optional)
    @Column(length = 300)
    private String notes;

    @Column(name = "sold_at", updatable = false)
    private LocalDateTime soldAt;

    @PrePersist
    protected void onCreate() {
        soldAt = LocalDateTime.now();
        calculateTotals();
    }

    // Automatically computes totals before persisting
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
