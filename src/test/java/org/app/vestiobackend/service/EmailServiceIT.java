package org.app.vestiobackend.service;

import com.resend.Resend;
import com.resend.core.exception.ResendException;
import com.resend.core.net.RequestOptions;
import com.resend.services.emails.Emails;
import com.resend.services.emails.model.CreateEmailOptions;
import org.app.vestiobackend.config.properties.EmailProperties;
import org.app.vestiobackend.config.EmailConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.support.TransactionTemplate;

import java.net.URI;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringJUnitConfig(EmailServiceIT.Config.class)
class EmailServiceIT {
    @Autowired ApplicationEventPublisher events;
    @Autowired PlatformTransactionManager transactionManager;
    @Autowired Resend resend;
    @Autowired ObjectProvider<Resend> resendProvider;
    @Autowired Emails emails;
    @Autowired EmailTemplate template;

    private final AccountCreated account = new AccountCreated(UUID.randomUUID(), "jane@example.com", "Jane <&>");

    @BeforeEach
    void setup() {
        reset(resend, emails);
        when(resend.emails()).thenReturn(emails);
    }

    @Test
    void passwordEmailsUseSharedLayoutAndHaveNoPasswordContent() throws Exception {
        var reset = template.passwordReset("012345");
        var changed = template.passwordChanged("https://vestio.example.com/");
        for (var message : List.of(reset, changed)) {
            assertTrue(message.html().startsWith("<!doctype html>"));
            assertTrue(message.html().contains("vestio<span"));
            assertFalse(message.html().contains("{{content}}"));
            assertFalse(message.text().isBlank());
        }
        assertTrue(reset.html().contains("012345"));
        assertTrue(changed.html().contains("https://vestio.example.com/login"));
        var event = new PasswordResetPinCreated(UUID.randomUUID(), account.email(), "012345");
        new EmailService(resendProvider, properties(true, "test-key"), template).sendPasswordReset(event);
        verify(emails).send(any(CreateEmailOptions.class), any(RequestOptions.class));
    }

    @Test
    void sendsVerificationPinOnlyAfterCommit() throws Exception {
        var pin = new VerificationPinCreated(UUID.randomUUID(), "jane@example.com", "012345");
        new TransactionTemplate(transactionManager).executeWithoutResult(status -> {
            events.publishEvent(pin);
            verifyNoInteractions(emails);
        });
        var email = ArgumentCaptor.forClass(CreateEmailOptions.class);
        var options = ArgumentCaptor.forClass(RequestOptions.class);
        verify(emails).send(email.capture(), options.capture());
        assertTrue(email.getValue().getText().contains("012345"));
        assertTrue(email.getValue().getHtml().contains("012345"));
        assertEquals("verification/" + pin.id(), options.getValue().getIdempotencyKey());
    }

    @Test
    void sendsPersonalizedMultipartEmailOnlyAfterCommit() throws Exception {
        new TransactionTemplate(transactionManager).executeWithoutResult(status -> {
            events.publishEvent(account);
            verifyNoInteractions(emails);
        });

        var email = ArgumentCaptor.forClass(CreateEmailOptions.class);
        var options = ArgumentCaptor.forClass(RequestOptions.class);
        verify(emails).send(email.capture(), options.capture());
        assertEquals(List.of(account.email()), email.getValue().getTo());
        assertEquals("Vestio <welcome@example.com>", email.getValue().getFrom());
        assertTrue(email.getValue().getHtml().contains("Jane &lt;&amp;&gt;"));
        assertTrue(email.getValue().getText().contains(account.firstName()));
        assertTrue(email.getValue().getHtml().contains("https://vestio.example.com"));
        assertEquals("welcome/" + account.userId(), options.getValue().getIdempotencyKey());
    }

    @Test
    void doesNotSendOnRollbackOrWithoutTransaction() {
        new TransactionTemplate(transactionManager).executeWithoutResult(status -> {
            events.publishEvent(account);
            status.setRollbackOnly();
        });
        events.publishEvent(account);
        verifyNoInteractions(emails);
    }

    @Test
    void providerFailureDoesNotFailTheCommittedSignup() throws Exception {
        when(emails.send(any(CreateEmailOptions.class), any(RequestOptions.class)))
                .thenThrow(new ResendException(503, "provider unavailable"));
        assertDoesNotThrow(() -> new TransactionTemplate(transactionManager)
                .executeWithoutResult(status -> events.publishEvent(account)));
        verify(emails).send(any(CreateEmailOptions.class), any(RequestOptions.class));
    }

    @Test
    void disabledOrMissingCredentialsNeverCallResend() {
        new EmailService(resendProvider, properties(false, "test-key"), template).sendWelcome(account);
        new EmailService(resendProvider, properties(true, ""), template).sendWelcome(account);
        verifyNoInteractions(resend, emails);
    }

    @Test
    void missingCredentialsDoNotPreventApplicationStartup() {
        new ApplicationContextRunner()
                .withUserConfiguration(EmailConfig.class, EmailService.class)
                .withBean(EmailProperties.class, () -> properties(true, ""))
                .withBean(EmailTemplate.class, () -> template)
                .run(context -> {
                    assertNull(context.getStartupFailure());
                    context.getBean(EmailService.class).sendWelcome(account);
                    assertFalse(context.getBeanFactory().containsSingleton("resend"));
                });
    }

    @Test
    void bothEmailsUseSharedHtmlLayoutAndKeepDynamicValuesLiteral() {
        var welcome = template.welcome("Jane $1 \\ {{content}}", "https://vestio.example.com");
        var verification = template.verification("012345");
        for (var message : List.of(welcome, verification)) {
            assertTrue(message.html().startsWith("<!doctype html>"));
            assertTrue(message.html().contains("vestio<span"));
            assertTrue(message.html().contains("La mode circule. Les histoires continuent."));
            assertFalse(message.html().contains("{{heading}}"));
            assertFalse(message.html().contains("{{preheader}}"));
            assertFalse(message.text().isBlank());
        }
        assertTrue(welcome.html().contains("Jane $1 \\ {{content}}"));
        assertTrue(verification.html().contains("012345"));
        assertFalse(verification.html().contains("Explorer Vestio"));
        assertFalse(template.verification("<script>{{content}}</script>").html().contains("<script>"));
    }

    @Test
    void escapesNamesAndLinksWithoutInterpretingUserPlaceholders() {
        String html = template.welcome("<script>{{siteUrl}}</script>", "https://vestio.example.com/?a=1&b=2").html();
        assertFalse(html.contains("<script>"));
        assertTrue(html.contains("&lt;script&gt;{{siteUrl}}&lt;/script&gt;"));
        assertTrue(html.contains("?a=1&amp;b=2"));
        assertFalse(template.welcome(null, "https://vestio.example.com").html().contains("{{firstName}}"));
    }

    private static EmailProperties properties(boolean enabled, String key) {
        return new EmailProperties(enabled, key, "Vestio <welcome@example.com>", URI.create("https://vestio.example.com"));
    }

    @Configuration(proxyBeanMethods = false)
    @EnableTransactionManagement
    static class Config {
        @Bean Resend resend() { return mock(Resend.class); }
        @Bean Emails emails() { return mock(Emails.class); }
        @Bean EmailTemplate template() throws Exception { return new EmailTemplate(); }
        @Bean EmailService emailService(ObjectProvider<Resend> resend, EmailTemplate template) {
            return new EmailService(resend, properties(true, "test-key"), template);
        }
        @Bean PlatformTransactionManager transactionManager() {
            return new DataSourceTransactionManager(new DriverManagerDataSource("jdbc:h2:mem:emailtest", "sa", ""));
        }
    }
}
