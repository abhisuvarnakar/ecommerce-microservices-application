package com.cs.ecommerce.paymentservice.service.impl;

import com.cs.ecommerce.paymentservice.dto.*;
import com.cs.ecommerce.paymentservice.entities.Payment;
import com.cs.ecommerce.paymentservice.entities.Refund;
import com.cs.ecommerce.paymentservice.entities.UserPaymentMethod;
import com.cs.ecommerce.paymentservice.enums.PaymentStatus;
import com.cs.ecommerce.paymentservice.exceptions.PaymentNotFoundException;
import com.cs.ecommerce.paymentservice.exceptions.PaymentProcessingException;
import com.cs.ecommerce.paymentservice.repositories.PaymentRepository;
import com.cs.ecommerce.paymentservice.repositories.RefundRepository;
import com.cs.ecommerce.paymentservice.repositories.UserPaymentMethodRepository;
import com.cs.ecommerce.paymentservice.service.PaymentGatewayService;
import com.cs.ecommerce.paymentservice.service.PaymentService;
import com.cs.ecommerce.paymentservice.validator.PaymentValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;
    private final RefundRepository refundRepository;
    private final UserPaymentMethodRepository userPaymentMethodRepository;
    private final PaymentValidator paymentValidator;
    private final PaymentGatewayService paymentGatewayService;
    private final ModelMapper modelMapper;

    @Override
    public PaymentResponseDTO processPayment(PaymentRequestDTO paymentRequest, Long userId) {
        paymentValidator.validateOrder(paymentRequest.getOrderId(), userId);
        List<UserPaymentMethod> userPaymentMethods =
                userPaymentMethodRepository.findByUserId(userId);
        if (userPaymentMethods.isEmpty()) {
            throw new PaymentProcessingException(
                    "No payment methods found for user. Please add a payment method first.");
        }

        boolean hasRequestedMethod = userPaymentMethods.stream()
                .anyMatch(m -> m.getPaymentMethod() == paymentRequest.getPaymentMethod());
        if (!hasRequestedMethod) {
            throw new PaymentProcessingException(
                    "Requested payment method not found for user. Please add this payment method first.");
        }
        Payment payment = createPaymentRecord(paymentRequest,
                paymentGatewayService.processPayment(paymentRequest));

        // TODO: Implement Kafka event publishing
        // kafkaEventPublisher.publishPaymentEvent(payment, gatewayResponse);

        return toPaymentResponseDTO(payment);
    }

    @Override
    public PaymentDetailsDTO getPaymentDetails(Long paymentId) {
        log.debug("Fetching payment details for paymentId: {}", paymentId);
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new PaymentNotFoundException(paymentId));
        return toPaymentDetailsDTO(payment);
    }

    @Override
    public List<PaymentDetailsDTO> getPaymentsForOrder(Long orderId) {
        log.debug("Fetching payments for order: {}", orderId);
        List<Payment> payments = paymentRepository.findByOrderId(orderId);
        return payments.stream().map(this::toPaymentDetailsDTO).toList();
    }

    @Override
    public RefundResponseDTO processRefund(Long paymentId, RefundRequestDTO refundRequest,
                                           Long userId) {
        log.info("Processing refund for payment: {} by user: {}", paymentId, userId);
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new PaymentNotFoundException(paymentId));
        paymentValidator.validateRefundRequest(payment, refundRequest);
        Refund refund = createRefundRecord(payment, refundRequest,
                paymentGatewayService.processRefund(
                payment, refundRequest), userId);

        // TODO: Implement Kafka event publishing
        // kafkaEventPublisher.publishRefundEvent(refund);

        return toRefundResponseDTO(refund);
    }

    private Payment createPaymentRecord(PaymentRequestDTO paymentRequest,
                                        PaymentGatewayService.PaymentGatewayResponse paymentGatewayResponse) {
        Payment payment = Payment.builder()
                .orderId(paymentRequest.getOrderId())
                .amount(paymentRequest.getAmount())
                .paymentMethod(paymentRequest.getPaymentMethod())
                .status("SUCCESS".equalsIgnoreCase(paymentGatewayResponse.getStatus()) ?
                        PaymentStatus.COMPLETED : PaymentStatus.FAILED)
                .gatewayTransactionId(paymentGatewayResponse.getTransactionId())
                .gatewayName(paymentGatewayResponse.getGatewayName())
                .gatewayResponse(paymentGatewayResponse.getRawResponse())
                .processedAt(LocalDateTime.now())
                .build();
        return paymentRepository.save(payment);
    }

    private Refund createRefundRecord(Payment payment, RefundRequestDTO refundRequest,
                                      PaymentGatewayService.PaymentGatewayResponse paymentGatewayResponse, Long userId) {
        Refund refund = Refund.builder()
                .payment(payment)
                .orderId(payment.getOrderId())
                .amount(refundRequest.getAmount())
                .reason(refundRequest.getReason())
                .status("SUCCESS".equalsIgnoreCase(paymentGatewayResponse.getStatus()) ?
                        PaymentStatus.REFUNDED : PaymentStatus.FAILED)
                .gatewayRefundId(paymentGatewayResponse.getTransactionId())
                .processedAt(LocalDateTime.now())
                .createUserId(userId)
                .build();
        refund = refundRepository.save(refund);
        updatePaymentStatusForRefund(payment, refund.getAmount());

        return refund;
    }

    private void updatePaymentStatusForRefund(Payment payment, BigDecimal refundAmount) {
        if (refundAmount.compareTo(payment.getAmount()) == 0) {
            payment.setStatus(PaymentStatus.REFUNDED);
        } else {
            payment.setStatus(PaymentStatus.PARTIAL_REFUND);
        }
        paymentRepository.save(payment);
    }

    private PaymentResponseDTO toPaymentResponseDTO(Payment payment) {
        return PaymentResponseDTO.builder()
                .paymentId(payment.getId())
                .status(payment.getStatus())
                .transactionId(payment.getGatewayTransactionId())
                .timestamp(payment.getProcessedAt())
                .build();
    }

    private PaymentDetailsDTO toPaymentDetailsDTO(Payment payment) {
        PaymentDetailsDTO paymentDetailsDTO = modelMapper.map(payment, PaymentDetailsDTO.class);
        paymentDetailsDTO.setPaymentId(payment.getId());
        paymentDetailsDTO.setTimestamp(payment.getProcessedAt());
        return paymentDetailsDTO;
    }

    private RefundResponseDTO toRefundResponseDTO(Refund refund) {
        return RefundResponseDTO.builder()
                .refundId(refund.getId())
                .status(refund.getStatus())
                .timestamp(refund.getProcessedAt())
                .build();
    }
}
