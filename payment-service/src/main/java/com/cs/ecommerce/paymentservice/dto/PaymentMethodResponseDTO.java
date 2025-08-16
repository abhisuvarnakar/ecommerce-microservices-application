package com.cs.ecommerce.paymentservice.dto;

import com.cs.ecommerce.paymentservice.enums.PaymentMethod;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PaymentMethodResponseDTO {
    private Long methodId;
    private PaymentMethod paymentMethod;
    private String maskedDetails;
    private String additionalInfo;
    private boolean isDefault;
}
