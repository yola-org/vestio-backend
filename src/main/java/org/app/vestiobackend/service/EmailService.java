package org.app.vestiobackend.service;

import com.resend.Resend;
import com.resend.core.net.RequestOptions;
import com.resend.services.emails.model.CreateEmailOptions;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.app.vestiobackend.config.properties.EmailProperties;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.transaction.event.TransactionalEventListener;

import java.time.Duration;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailService {
    private final ObjectProvider<Resend> resend;
    private final EmailProperties properties;
    private final EmailTemplate template;

    @TransactionalEventListener
    public void sendVerification(VerificationPinCreated event) {
        if (!properties.enabled() || properties.apiKey() == null || properties.apiKey().isBlank()) {
            log.warn("Verification email skipped for PIN {}: email delivery is not configured", event.id());
            return;
        }
        try {
            var message = template.verification(event.pin());
            var email = CreateEmailOptions.builder().from(properties.from()).to(event.email())
                    .subject(message.subject())
                    .text(message.text())
                    .html(message.html())
                    .build();
            resend.getObject().emails().send(email, RequestOptions.builder().setIdempotencyKey("verification/" + event.id()).timeout(Duration.ofSeconds(5)).build());
        } catch (Exception failure) {
            log.error("Verification email failed for PIN {} ({})", event.id(), failure.getClass().getSimpleName());
        }
    }

    /** Only committed activations send welcome mail. Delivery failure must not fail activation. */
    @TransactionalEventListener
    public void sendWelcome(AccountCreated account) {
        if (!properties.enabled()) return;
        if (properties.apiKey() == null || properties.apiKey().isBlank()) {
            log.warn("Welcome email skipped for account {}: RESEND_API_KEY is not configured", account.userId());
            return;
        }
        try {
            var message = template.welcome(account.firstName(), properties.siteUrl().toString());
            CreateEmailOptions email = CreateEmailOptions.builder()
                    .from(properties.from())
                    .to(account.email())
                    .subject(message.subject())
                    .html(message.html())
                    .text(message.text())
                    .build();
            resend.getObject().emails().send(email, RequestOptions.builder()
                    .setIdempotencyKey("welcome/" + account.userId())
                    .timeout(Duration.ofSeconds(5))
                    .build());
            log.info("Welcome email accepted for account {}", account.userId());
        } catch (Exception failure) {
            // Provider exception messages may include request content; log only the type and account ID.
            log.error("Welcome email failed for account {} ({})", account.userId(), failure.getClass().getSimpleName());
        }
    }
}
