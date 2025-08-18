package com.cs.ecommerce.notificationservice.templates;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.ResourcePatternUtils;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class NotificationTemplateStore {

    public Set<String> getAvailableTemplates() {
        try {
            Resource[] resources =
                    ResourcePatternUtils.getResourcePatternResolver(new DefaultResourceLoader())
                            .getResources("classpath:/templates/email/*.ftl");

            return Arrays.stream(resources)
                    .map(resource -> {
                        String filename = resource.getFilename() != null ?
                                resource.getFilename() : "";
                        return filename.substring(0, filename.lastIndexOf("."));
                    })
                    .collect(Collectors.toSet());

        } catch (IOException e) {
            throw new RuntimeException("Failed to list templates", e);
        }
    }

    public String getTemplateContent(String templateName) throws IOException {
        Resource resource = new ClassPathResource("/templates/email/" + templateName + ".ftl");
        try (InputStream inputStream = resource.getInputStream()) {
            return new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
        }
    }

}
