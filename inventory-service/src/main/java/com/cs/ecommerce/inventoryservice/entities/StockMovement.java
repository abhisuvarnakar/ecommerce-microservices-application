package com.cs.ecommerce.inventoryservice.entities;

import com.cs.ecommerce.inventoryservice.enums.StockMovementType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "t_stock_movement", indexes = {
        @Index(name = "idx_stock_movement01", columnList = "product_id")
})
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class StockMovement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long productId;

    @Enumerated(EnumType.STRING)
    @Column(length = 20, nullable = false)
    private StockMovementType movementType;

    @Column(nullable = false)
    private Integer quantity;

    private String reason;

    private LocalDateTime createdAt;

    private Long createUserId;

    @PrePersist
    public void prePersist() {
        createdAt = LocalDateTime.now();
    }
}
