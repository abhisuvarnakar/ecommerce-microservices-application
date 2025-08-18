package com.cs.ecommerce.paymentservice.entities;

import com.cs.ecommerce.sharedmodules.enums.payment.PaymentStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "t_refund")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Refund {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "payment_id", nullable = false)
    private Payment payment;

    @Column(nullable = false)
    private Long orderId;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal amount;

    private String reason;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentStatus status = PaymentStatus.PENDING;

    private String gatewayRefundId;

    private LocalDateTime processedAt;

    @Column(updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private Long createUserId;

    @PrePersist
    public void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

}
