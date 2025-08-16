package com.cs.ecommerce.paymentservice.dto;

import com.cs.ecommerce.paymentservice.enums.PaymentStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentResponseDTO {
    private Long paymentId;
    private PaymentStatus status;
    private String transactionId;
    private LocalDateTime timestamp;
}
