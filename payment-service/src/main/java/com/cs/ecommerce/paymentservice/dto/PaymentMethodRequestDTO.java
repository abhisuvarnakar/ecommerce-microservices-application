package com.cs.ecommerce.paymentservice.dto;

import com.cs.ecommerce.paymentservice.enums.PaymentMethod;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PaymentMethodRequestDTO {

    @NotNull(message = "Payment method is required")
    private PaymentMethod paymentMethod;

    @Valid
    private CardDetailsDTO cardDetails;

    // UPI fields
    private String upiId;
    private String bankName;

    // Bank transfer fields
    private String accountNumber;
    private String ifscCode;
    private String accountHolderName;
}
