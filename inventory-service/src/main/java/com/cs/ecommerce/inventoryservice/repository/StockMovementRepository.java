package com.cs.ecommerce.inventoryservice.repository;

import com.cs.ecommerce.inventoryservice.entities.StockMovement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface StockMovementRepository extends JpaRepository<StockMovement, Long> {

    @Query(value = "SELECT * FROM t_stock_movement WHERE product_id = :productId " +
            "AND (CAST(:startDate AS TIMESTAMP) IS NULL " +
            "         OR created_at >= CAST(:startDate AS TIMESTAMP)) " +
            "AND (CAST(:endDate AS TIMESTAMP) IS NULL " +
            "         OR created_at <= CAST(:endDate AS TIMESTAMP)) " +
            "ORDER BY created_at DESC",
            nativeQuery = true)
    List<StockMovement> findMovementsByProductAndDateRange(
            @Param("productId") Long productId,
            @Param("startDate") Timestamp startDate,
            @Param("endDate") Timestamp endDate);

}
