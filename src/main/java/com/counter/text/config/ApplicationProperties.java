package com.counter.text.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "text-counter")
public class ApplicationProperties {
    private String uploadDirectory;
    private String uploadFleName;
    private String downloadDirectory;
    private String downloadFleName;
}