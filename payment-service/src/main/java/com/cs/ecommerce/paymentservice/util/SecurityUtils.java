package com.cs.ecommerce.paymentservice.util;

import com.cs.ecommerce.paymentservice.exceptions.PaymentProcessingException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;

import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

@Slf4j
public class SecurityUtils {

    private static final String ENCRYPTION_PREFIX = "ENC:";

    public static String encryptJsonValues(String json) {
        try {
            Map<String, Object> map = new ObjectMapper().readValue(json, Map.class);
            Map<String, Object> encryptedMap = new HashMap<>();

            for (Map.Entry<String, Object> entry : map.entrySet()) {
                if (entry.getValue() instanceof String) {
                    encryptedMap.put(entry.getKey(),
                            ENCRYPTION_PREFIX + Base64.getEncoder().encodeToString(entry.getValue()
                                    .toString().getBytes()));
                } else {
                    encryptedMap.put(entry.getKey(), entry.getValue());
                }
            }

            return new ObjectMapper().writeValueAsString(encryptedMap);
        } catch (Exception e) {
            log.error("Error encrypting JSON values", e);
            throw new PaymentProcessingException("Failed to encrypt payment details", e);
        }
    }

    public static String decryptJsonValues(String encryptedJson) {
        try {
            Map<String, Object> map = new ObjectMapper().readValue(encryptedJson, Map.class);
            Map<String, Object> decryptedMap = new HashMap<>();

            for (Map.Entry<String, Object> entry : map.entrySet()) {
                if (entry.getValue() instanceof String
                        && ((String) entry.getValue()).startsWith(ENCRYPTION_PREFIX)) {
                    String encryptedValue =
                            ((String) entry.getValue()).substring(ENCRYPTION_PREFIX.length());
                    decryptedMap.put(entry.getKey(),
                            new String(Base64.getDecoder().decode(encryptedValue)));
                } else {
                    decryptedMap.put(entry.getKey(), entry.getValue());
                }
            }

            return new ObjectMapper().writeValueAsString(decryptedMap);
        } catch (Exception e) {
            log.error("Error decrypting JSON values", e);
            throw new PaymentProcessingException("Failed to decrypt payment details", e);
        }
    }
}
