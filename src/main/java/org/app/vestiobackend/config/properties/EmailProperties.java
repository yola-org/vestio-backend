package org.app.vestiobackend.config.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.net.URI;

@ConfigurationProperties(prefix = "app.email")
public record EmailProperties(boolean enabled, String apiKey, String from, URI siteUrl) {
    public EmailProperties {
        if (siteUrl == null || !"https".equalsIgnoreCase(siteUrl.getScheme()) || siteUrl.getHost() == null) {
            throw new IllegalArgumentException("app.email.site-url must be an absolute HTTPS URL");
        }
    }
}
