package com.cs.ecommerce.inventoryservice.repository;

import com.cs.ecommerce.inventoryservice.entities.Inventory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface InventoryRepository extends JpaRepository<Inventory, Long> {

    Optional<Inventory> findByProductId(Long productId);

    @Query("SELECT i FROM Inventory i WHERE i.availableStock <= i.reorderLevel AND " +
            "(:threshold IS NULL OR i.availableStock <= :threshold)")
    List<Inventory> findLowStockProducts(@Param("threshold") Integer threshold);

}
