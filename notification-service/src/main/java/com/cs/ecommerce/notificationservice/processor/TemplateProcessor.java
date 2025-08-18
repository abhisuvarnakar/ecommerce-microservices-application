package com.cs.ecommerce.notificationservice.processor;

import freemarker.template.Configuration;
import freemarker.template.DefaultObjectWrapper;
import freemarker.template.Template;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;

import java.io.StringWriter;
import java.util.Map;

@Component
@Slf4j
public class TemplateProcessor {

    private final Configuration freemarkerConfig;

    public TemplateProcessor(FreeMarkerConfigurer freeMarkerConfigurer) {
        this.freemarkerConfig = freeMarkerConfigurer.getConfiguration();
        this.freemarkerConfig.setObjectWrapper(new DefaultObjectWrapper(Configuration.VERSION_2_3_31));
    }

    public String processTemplate(String templateName, Map<String, Object> data) throws Exception {
        try {
            Template template = freemarkerConfig.getTemplate("email/" + templateName + ".ftl");
            StringWriter out = new StringWriter();
            template.process(data, out);
            return out.toString();
        } catch (Exception e) {
            log.error("Template processing failed for {}", templateName, e);
            throw e;
        }
    }
}
