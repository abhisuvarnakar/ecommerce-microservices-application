package com.cs.ecommerce.inventoryservice.repository;

import com.cs.ecommerce.inventoryservice.entities.StockReservation;
import com.cs.ecommerce.inventoryservice.enums.StockReservationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface StockReservationRepository extends JpaRepository<StockReservation, Long> {

    List<StockReservation> findByStatusAndExpiresAtBefore(StockReservationStatus status,
                                                          LocalDateTime dateTime);

    @Query("SELECT sr FROM StockReservation sr WHERE sr.productId = :productId " +
            "AND sr.status = :status")
    List<StockReservation> findByProductIdAndStatus
            (@Param("productId") Long productId, @Param("status") StockReservationStatus status);
}
