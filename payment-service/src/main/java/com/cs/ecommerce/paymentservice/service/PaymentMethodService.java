package com.cs.ecommerce.paymentservice.service;

import com.cs.ecommerce.paymentservice.dto.AddPaymentMethodResponseDTO;
import com.cs.ecommerce.paymentservice.dto.PaymentMethodRequestDTO;
import com.cs.ecommerce.paymentservice.dto.PaymentMethodResponseDTO;

import java.util.List;

public interface PaymentMethodService {

    List<PaymentMethodResponseDTO> getUserPaymentMethods(Long userId);

    AddPaymentMethodResponseDTO addUserPaymentMethod(Long userId, PaymentMethodRequestDTO request);

    void deleteUserPaymentMethod(Long userId, Long methodId);

    void setDefaultPaymentMethod(Long userId, Long methodId);
}
