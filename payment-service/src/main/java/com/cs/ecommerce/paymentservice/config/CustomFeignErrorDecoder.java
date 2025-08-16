package com.cs.ecommerce.paymentservice.config;

import com.cs.ecommerce.sharedmodules.exceptions.BusinessException;
import com.cs.ecommerce.sharedmodules.exceptions.ResourceNotFoundException;
import com.cs.ecommerce.sharedmodules.exceptions.ServiceException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import feign.Response;
import feign.Util;
import feign.codec.ErrorDecoder;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Slf4j
public class CustomFeignErrorDecoder implements ErrorDecoder {

    @Override
    public Exception decode(String methodKey, Response response) {
        String bodyString = null;
        try {
            if (response.body() != null) {
                bodyString = Util.toString(response.body().asReader(StandardCharsets.UTF_8));
            }
        } catch (IOException e) {
            log.error("Failed to read Feign error response body", e);
        }

        log.error("Feign call failed: Method Key: {}, Status: {}, Reason: {}, Headers: {}, Body: {}",
                methodKey,
                response.status(),
                response.reason(),
                response.headers(),
                bodyString
        );

        String errorMessage = "Unknown error";
        if (bodyString != null && !bodyString.isEmpty()) {
            try {
                ObjectMapper mapper = new ObjectMapper();
                JsonNode jsonNode = mapper.readTree(bodyString);
                if (jsonNode.has("message")) {
                    errorMessage = jsonNode.get("message").asText();
                }
            } catch (Exception e) {
                log.warn("Failed to parse error message from body: {}", bodyString, e);
            }
        }

        return switch (response.status()) {
            case 400 -> new BusinessException(errorMessage);
            case 404 -> new ResourceNotFoundException(errorMessage);
            default -> new ServiceException(errorMessage);
        };
    }
}

