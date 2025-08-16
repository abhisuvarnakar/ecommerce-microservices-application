package com.cs.ecommerce.paymentservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AddPaymentMethodResponseDTO {
    private Long methodId;
    private String message;
}
