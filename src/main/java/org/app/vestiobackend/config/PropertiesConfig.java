package org.app.vestiobackend.config;

import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.annotation.Configuration;

@Configuration(proxyBeanMethods = false)
@ConfigurationPropertiesScan(basePackages = "org.app.vestiobackend.config.properties")
public class PropertiesConfig {
}