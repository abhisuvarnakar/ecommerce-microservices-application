package com.cs.ecommerce.inventoryservice.entities;

import com.cs.ecommerce.inventoryservice.enums.StockReservationStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "t_stock_reservation", indexes = {
        @Index(name = "idx_stock_reservation01", columnList = "product_id"),
        @Index(name = "idx_stock_reservation02", columnList = "expires_at")
})
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class StockReservation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long productId;

    private Long orderId;

    @Column(nullable = false)
    private Integer quantity;

    @Column(nullable = false)
    private LocalDateTime expiresAt;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private StockReservationStatus status = StockReservationStatus.ACTIVE;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist() {
        createdAt = LocalDateTime.now();
    }
}
