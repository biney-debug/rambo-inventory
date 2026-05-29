package com.rambo.repository;

import com.rambo.dto.TopProductDTO;
import com.rambo.entity.Sale;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface SaleRepository extends JpaRepository<Sale, Long> {

    // Eager-load product to avoid N+1 on list endpoints
    @Query(value = "SELECT s FROM Sale s JOIN FETCH s.product ORDER BY s.soldAt DESC",
           countQuery = "SELECT COUNT(s) FROM Sale s")
    Page<Sale> findAllWithProduct(Pageable pageable);

    @Query("SELECT s FROM Sale s JOIN FETCH s.product WHERE s.id = :id")
    Optional<Sale> findByIdWithProduct(@Param("id") Long id);

    // Sales for a specific product, most recent first
    @Query("SELECT s FROM Sale s JOIN FETCH s.product WHERE s.product.id = :productId ORDER BY s.soldAt DESC")
    List<Sale> findByProductIdOrderBySoldAtDesc(@Param("productId") Long productId);

    // Sales within a date range
    List<Sale> findBySoldAtBetweenOrderBySoldAtDesc(LocalDateTime from, LocalDateTime to);

    // Sum of profit across all sales
    @Query("SELECT COALESCE(SUM(s.profit), 0) FROM Sale s")
    BigDecimal sumTotalProfit();

    // Sum of total revenue across all sales
    @Query("SELECT COALESCE(SUM(s.totalAmount), 0) FROM Sale s")
    BigDecimal sumTotalRevenue();

    // Top 5 most profitable products (by accumulated profit)
    @Query("""
        SELECT new com.rambo.dto.TopProductDTO(
            p.id,
            p.name,
            SUM(s.quantity),
            SUM(s.totalAmount),
            SUM(s.profit),
            CASE WHEN SUM(s.totalAmount) > 0
                 THEN (SUM(s.profit) / SUM(s.totalAmount)) * 100
                 ELSE 0 END
        )
        FROM Sale s
        JOIN s.product p
        GROUP BY p.id, p.name
        ORDER BY SUM(s.profit) DESC
        LIMIT 5
        """)
    List<TopProductDTO> findTopProfitableProducts();

    // Profit for a specific time period
    @Query("""
        SELECT COALESCE(SUM(s.profit), 0)
        FROM Sale s
        WHERE s.soldAt BETWEEN :from AND :to
        """)
    BigDecimal sumProfitByPeriod(@Param("from") LocalDateTime from,
                                  @Param("to")   LocalDateTime to);

    // Total number of transactions
    @Query("SELECT COUNT(s) FROM Sale s")
    Long countTotalTransactions();

    void deleteByProductId(Long productId);
}
