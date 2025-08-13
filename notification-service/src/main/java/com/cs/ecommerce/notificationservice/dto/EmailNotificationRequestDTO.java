package com.cs.ecommerce.notificationservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EmailNotificationRequestDTO {

    @NotBlank(message = "Recipient email (to) must not be blank")
    private String to;

    @NotBlank(message = "Subject must not be blank")
    private String subject;

    @NotBlank(message = "Template must not be blank")
    private String template;

    @NotNull(message = "Data map must not be null")
    private Map<String, Object> data;

}
