package org.app.vestiobackend.config;

import com.resend.Resend;
import org.app.vestiobackend.config.properties.EmailProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;

import java.time.Duration;

@Configuration(proxyBeanMethods = false)
public class EmailConfig {
    @Bean
    @Lazy
    public Resend resend(EmailProperties properties) {
        return Resend.builder()
                .apiKey(properties.apiKey())
                .connectTimeout(Duration.ofSeconds(3))
                .readTimeout(Duration.ofSeconds(5))
                .build();
    }
}
