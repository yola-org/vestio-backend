package org.app.vestiobackend.config.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;
import org.springframework.util.unit.DataSize;

import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;

@ConfigurationProperties(prefix = "app.image-upload")
public record ImageUploadProperties(
        @DefaultValue("10MB") DataSize maxFileSize,
        @DefaultValue({"jpg", "jpeg", "png", "webp"}) Set<String> allowedExtensions
) {
    public ImageUploadProperties {
        if (maxFileSize.toBytes() <= 0) {
            throw new IllegalArgumentException("Maximum image file size must be positive");
        }
        allowedExtensions = allowedExtensions.stream()
                .map(extension -> extension.strip().toLowerCase(Locale.ROOT))
                .collect(Collectors.toUnmodifiableSet());
        if (allowedExtensions.isEmpty() || allowedExtensions.stream().anyMatch(extension -> !extension.matches("[a-z0-9]+"))) {
            throw new IllegalArgumentException("Allowed image extensions must be listed without a leading dot");
        }
    }
}
