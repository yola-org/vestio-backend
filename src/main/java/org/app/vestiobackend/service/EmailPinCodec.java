package org.app.vestiobackend.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.HexFormat;
import java.util.Locale;
import java.util.UUID;

@Component
public class EmailPinCodec {
    private static final SecureRandom RANDOM = new SecureRandom();
    @Value("${cognito.client-secret}") private String secret;

    public String generate() {
        return String.format(Locale.ROOT, "%06d", RANDOM.nextInt(1_000_000));
    }

    public String hash(UUID id, String pin) {
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
            return HexFormat.of().formatHex(mac.doFinal((id + ":" + pin).getBytes(StandardCharsets.UTF_8)));
        } catch (GeneralSecurityException failure) {
            throw new IllegalStateException("Cannot hash email PIN", failure);
        }
    }

    public boolean matches(UUID id, String hash, String pin) {
        return MessageDigest.isEqual(hash.getBytes(StandardCharsets.UTF_8), hash(id, pin).getBytes(StandardCharsets.UTF_8));
    }
}
