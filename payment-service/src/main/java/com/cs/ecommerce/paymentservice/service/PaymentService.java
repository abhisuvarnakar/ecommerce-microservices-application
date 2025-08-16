package com.cs.ecommerce.paymentservice.service;

import com.cs.ecommerce.paymentservice.dto.*;

import java.util.List;

public interface PaymentService {

    PaymentResponseDTO processPayment(PaymentRequestDTO paymentRequest, Long userId);

    PaymentDetailsDTO getPaymentDetails(Long paymentId);

    List<PaymentDetailsDTO> getPaymentsForOrder(Long orderId);

    RefundResponseDTO processRefund(Long paymentId, RefundRequestDTO refundRequest, Long userId);
}
