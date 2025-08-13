package com.cs.ecommerce.notificationservice.templates;

import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Optional;
import java.util.Set;

@Component
public class NotificationTemplateStore {

    private final Map<String, String> templates = Map.of(
            "ORDER_CONFIRMATION", "Hello {{name}}, your order {{orderId}} has been confirmed.",
            "PASSWORD_RESET", "Hi {{name}}, click here to reset your password: {{resetLink}}",
            "PROMOTION", "Dear {{name}}, check out our latest promotions!"
    );

    public Set<String> getAvailableTemplates() {
        return templates.keySet();
    }

    public Optional<String> getTemplate(String templateName) {
        return Optional.ofNullable(templates.get(templateName));
    }
}
