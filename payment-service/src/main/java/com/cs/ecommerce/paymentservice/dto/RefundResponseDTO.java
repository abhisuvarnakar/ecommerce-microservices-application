package com.cs.ecommerce.paymentservice.dto;

import com.cs.ecommerce.paymentservice.enums.PaymentStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RefundResponseDTO {
    private Long refundId;
    private PaymentStatus status;
    private LocalDateTime timestamp;
}
