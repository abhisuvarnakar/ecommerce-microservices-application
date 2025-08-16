package com.cs.ecommerce.paymentservice.mapper;

import com.cs.ecommerce.paymentservice.dto.PaymentMethodResponseDTO;
import com.cs.ecommerce.paymentservice.entities.UserPaymentMethod;
import com.cs.ecommerce.paymentservice.exceptions.PaymentProcessingException;
import com.cs.ecommerce.paymentservice.util.SecurityUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class PaymentMethodMapper {

    private final ObjectMapper objectMapper;

    public PaymentMethodResponseDTO toPaymentMethodResponseDTO(UserPaymentMethod method) {
        try {
            String decryptedDetails = SecurityUtils.decryptJsonValues(method.getPaymentDetails());
            Map<String, Object> details = objectMapper.readValue(decryptedDetails, Map.class);
            PaymentMethodResponseDTO response = PaymentMethodResponseDTO.builder()
                    .methodId(method.getId())
                    .paymentMethod(method.getPaymentMethod())
                    .isDefault(method.getIsDefault())
                    .build();

            switch (method.getPaymentMethod()) {
                case DEBIT_CARD:
                case CREDIT_CARD:
                    String cardNumber = (String) details.get("card_number");
                    response.setMaskedDetails("**** **** **** "
                            + cardNumber.substring(cardNumber.length() - 4));
                    response.setAdditionalInfo("Expires: " + details.get("expiry_date"));
                    break;

                case UPI:
                    String upiId = (String) details.get("upi_id");
                    response.setMaskedDetails(maskUpiId(upiId));
                    response.setAdditionalInfo("Bank: " + details.get("bank_name"));
                    break;

                case BANK_TRANSFER:
                    String accountNumber = (String) details.get("account_number");
                    response.setMaskedDetails("Account: ****" + accountNumber.substring(accountNumber.length() - 4));
                    response.setAdditionalInfo("IFSC: " + details.get("ifsc_code"));
                    break;

                case CASH_ON_DELIVERY:
                    response.setMaskedDetails("Cash on Delivery");
                    response.setAdditionalInfo("");
                    break;
            }

            return response;
        } catch (Exception e) {
            throw new PaymentProcessingException("Failed to parse payment method details", e);
        }
    }

    private String maskUpiId(String upiId) {
        String[] parts = upiId.split("@");
        if (parts.length == 2) {
            return parts[0].substring(0, 2) + "***@" + parts[1];
        }
        return upiId;
    }
}
