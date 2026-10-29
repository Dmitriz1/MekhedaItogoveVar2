package ru.local.parser.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "parser")
@Data
public class ParserProperties {
    private int threads = 8;
    private int timeoutMs = 10000;
    private long politeDelayMs = 300;
    private String userAgent = "...";
    private Selectors selectors = new Selectors();

    @Data
    public static class Selectors {
        private String productCardLink;
        private String title;
        private String priceAttr;
        private String description;
        private String date;
        private String priceText;
    }
}