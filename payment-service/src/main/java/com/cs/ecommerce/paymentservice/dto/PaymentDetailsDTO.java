package com.cs.ecommerce.paymentservice.dto;

import com.cs.ecommerce.paymentservice.enums.PaymentMethod;
import com.cs.ecommerce.paymentservice.enums.PaymentStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PaymentDetailsDTO {
    private Long paymentId;
    private Long orderId;
    private BigDecimal amount;
    private PaymentStatus status;
    private PaymentMethod paymentMethod;
    private LocalDateTime timestamp;
}
