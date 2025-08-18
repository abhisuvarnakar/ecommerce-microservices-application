package com.cs.ecommerce.sharedmodules.dto.payment;

import com.cs.ecommerce.sharedmodules.enums.payment.PaymentMethod;
import com.cs.ecommerce.sharedmodules.enums.payment.PaymentStatus;

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
