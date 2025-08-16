package com.cs.ecommerce.paymentservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CardDetailsDTO {

    @NotBlank(message = "Card number is required")
    @Size(min = 12, max = 19, message = "Card number must be between 12 and 19 digits")
    private String cardNumber;

    @NotBlank(message = "Expiry date is required")
    @Pattern(regexp = "^(0[1-9]|1[0-2])\\/([0-9]{2})$", message = "Expiry date must be in MM/YY " +
            "format")
    private String expiryDate;

    @NotBlank(message = "Card holder name is required")
    private String cardHolderName;

    @NotBlank(message = "CVV is required")
    @Size(min = 3, max = 4, message = "CVV must be 3 or 4 digits")
    private String cvv;
}
