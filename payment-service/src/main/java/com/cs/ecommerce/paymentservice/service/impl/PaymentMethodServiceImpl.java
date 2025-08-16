package com.cs.ecommerce.paymentservice.service.impl;

import com.cs.ecommerce.paymentservice.dto.AddPaymentMethodResponseDTO;
import com.cs.ecommerce.paymentservice.dto.PaymentMethodRequestDTO;
import com.cs.ecommerce.paymentservice.dto.PaymentMethodResponseDTO;
import com.cs.ecommerce.paymentservice.entities.UserPaymentMethod;
import com.cs.ecommerce.paymentservice.exceptions.PaymentMethodNotFoundException;
import com.cs.ecommerce.paymentservice.exceptions.PaymentProcessingException;
import com.cs.ecommerce.paymentservice.mapper.PaymentMethodMapper;
import com.cs.ecommerce.paymentservice.repositories.UserPaymentMethodRepository;
import com.cs.ecommerce.paymentservice.service.PaymentMethodService;
import com.cs.ecommerce.paymentservice.util.SecurityUtils;
import com.cs.ecommerce.paymentservice.validator.PaymentValidator;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class PaymentMethodServiceImpl implements PaymentMethodService {

    private final UserPaymentMethodRepository userPaymentMethodRepository;
    private final PaymentValidator paymentValidator;
    private final PaymentMethodMapper paymentMethodMapper;

    @Override
    public List<PaymentMethodResponseDTO> getUserPaymentMethods(Long userId) {
        log.debug("Fetching payment methods for user: {}", userId);
        List<UserPaymentMethod> methods = userPaymentMethodRepository.findByUserId(userId);
        return methods.stream()
                .map(paymentMethodMapper::toPaymentMethodResponseDTO)
                .toList();
    }

    @Override
    public AddPaymentMethodResponseDTO addUserPaymentMethod(Long userId,
                                                            PaymentMethodRequestDTO request) {
        log.info("Adding payment method for user: {}", userId);
        paymentValidator.validatePaymentMethodRequest(request);

        UserPaymentMethod method = UserPaymentMethod.builder()
                .userId(userId)
                .paymentMethod(request.getPaymentMethod())
                .paymentDetails(createPaymentDetails(request))
                .build();

        setDefaultStatusIfFirstMethod(userId, method);
        method = userPaymentMethodRepository.save(method);
        return new AddPaymentMethodResponseDTO(method.getId(), "Payment method added successfully");
    }

    @Override
    public void deleteUserPaymentMethod(Long userId, Long methodId) {
        log.info("Deleting payment method: {} for user: {}", methodId, userId);
        UserPaymentMethod method = userPaymentMethodRepository.findByIdAndUserId(methodId, userId)
                .orElseThrow(() -> new PaymentMethodNotFoundException(methodId));
        userPaymentMethodRepository.delete(method);
        handleDefaultMethodDeletion(userId, method);
    }

    @Override
    public void setDefaultPaymentMethod(Long userId, Long methodId) {
        log.info("Setting payment method: {} as default for user: {}", methodId, userId);
        UserPaymentMethod method = userPaymentMethodRepository.findByIdAndUserId(methodId, userId)
                .orElseThrow(() -> new PaymentMethodNotFoundException(methodId));

        userPaymentMethodRepository.findByUserIdAndIsDefaultTrue(userId)
                .ifPresent(currentDefault -> {
                    currentDefault.setIsDefault(false);
                    userPaymentMethodRepository.save(currentDefault);
                });

        method.setIsDefault(true);
        userPaymentMethodRepository.save(method);
    }

    private String createPaymentDetails(PaymentMethodRequestDTO request) {
        try {
            Map<String, Object> details = new HashMap<>();

            switch (request.getPaymentMethod()) {
                case CREDIT_CARD:
                case DEBIT_CARD:
                    details.put("card_number", request.getCardDetails().getCardNumber());
                    details.put("expiry_date", request.getCardDetails().getExpiryDate());
                    details.put("card_holder_name", request.getCardDetails().getCardHolderName());
                    details.put("cvv", request.getCardDetails().getCvv());
                    break;

                case UPI:
                    details.put("upi_id", request.getUpiId());
                    details.put("bank_name", request.getBankName());
                    break;

                case BANK_TRANSFER:
                    details.put("account_number", request.getAccountNumber());
                    details.put("ifsc_code", request.getIfscCode());
                    details.put("account_holder", request.getAccountHolderName());
                    break;

                case CASH_ON_DELIVERY:
                    break;
            }

            return SecurityUtils.encryptJsonValues(new ObjectMapper().writeValueAsString(details));
        } catch (Exception e) {
            log.error("Error creating payment details", e);
            throw new PaymentProcessingException("Failed to create payment method details", e);
        }
    }

    private void setDefaultStatusIfFirstMethod(Long userId, UserPaymentMethod method) {
        long methodCount = userPaymentMethodRepository.countByUserId(userId);
        method.setIsDefault(methodCount == 0);
    }

    private void handleDefaultMethodDeletion(Long userId, UserPaymentMethod deletedMethod) {
        if (deletedMethod.getIsDefault()) {
            userPaymentMethodRepository.findFirstByUserId(userId)
                    .ifPresent(newDefault -> {
                        newDefault.setIsDefault(true);
                        userPaymentMethodRepository.save(newDefault);
                        log.info("Set new default payment method: {} for user: {}",
                                newDefault.getId(), userId);
                    });
        }
    }
}
