package com.counter.text;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@Slf4j
@SpringBootApplication
@ConfigurationPropertiesScan
public class TextCounterSpringBootApplication {
    public static void main(String[] args) {
        SpringApplication.run(TextCounterSpringBootApplication.class, args);
    }
}
