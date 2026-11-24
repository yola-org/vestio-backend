package org.app.vestiobackend.service;

import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;
import org.springframework.web.util.HtmlUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/** Shared email layout with trusted HTML fragments and escaped dynamic values. */
@Component
public class EmailTemplate {
    private static final Pattern PLACEHOLDER = Pattern.compile("\\{\\{([a-zA-Z][a-zA-Z0-9]*)}}");
    private final String layout;
    private final String welcome;
    private final String verification;
    private final String passwordReset;
    private final String passwordChanged;

    public EmailTemplate() throws IOException {
        layout = load("layout");
        welcome = load("welcome");
        verification = load("verification");
        passwordReset = load("password-reset");
        passwordChanged = load("password-changed");
    }

    public Message welcome(String firstName, String siteUrl) {
        String name = firstName == null || firstName.isBlank() ? "vous" : firstName.strip();
        String content = interpolate(welcome, Map.of("firstName", escape(name), "siteUrl", escape(siteUrl)));
        String text = "Bonjour " + name + ",\n\n"
                + "Bienvenue chez Vestio. Une nouvelle histoire pour votre dressing.\n\n"
                + "Découvrez des pièces qui vous ressemblent, donnez une seconde vie aux vôtres "
                + "et échangez directement avec la communauté au Maroc.\n\n"
                + "Explorez Vestio : " + siteUrl + "\n\n"
                + "Votre adresse e-mail est confirmée. Vous pouvez vous connecter et profiter de Vestio.\n\n"
                + "À très vite,\nL’équipe Vestio\n";
        return message("Bienvenue chez Vestio — votre dressing a de l’avenir",
                "Votre compte est prêt. Une nouvelle histoire commence pour votre dressing.",
                "BIENVENUE DANS LA COMMUNAUTÉ", "Votre dressing. Une nouvelle histoire.", content, text);
    }

    public Message verification(String pin) {
        String content = interpolate(verification, Map.of("pin", escape(pin)));
        String text = "Votre code de confirmation Vestio : " + pin
                + "\nIl expire dans 10 minutes. Seul le dernier code envoyé est valide."
                + "\nSi vous n’avez pas demandé ce code, vous pouvez ignorer cet e-mail.\n\nL’équipe Vestio\n";
        return message("Vestio — confirmez votre adresse e-mail",
                "Votre code de confirmation expire dans 10 minutes.",
                "CONFIRMATION DE VOTRE COMPTE", "Confirmez votre adresse e-mail", content, text);
    }

    public Message passwordReset(String pin) {
        String content = interpolate(passwordReset, Map.of("pin", escape(pin)));
        String text = "Votre code de réinitialisation Vestio : " + pin
                + "\nIl expire dans 10 minutes. Seul le dernier code envoyé est valide."
                + "\nSi vous n’avez pas demandé ce code, ignorez cet e-mail. Votre mot de passe reste inchangé.\n";
        return message("Vestio — réinitialisez votre mot de passe", "Votre code de réinitialisation expire dans 10 minutes.",
                "SÉCURITÉ DE VOTRE COMPTE", "Réinitialisez votre mot de passe", content, text);
    }

    public Message passwordChanged(String siteUrl) {
        String loginUrl = siteUrl.replaceAll("/+$", "") + "/login";
        String content = interpolate(passwordChanged, Map.of("loginUrl", escape(loginUrl)));
        String text = "Votre mot de passe Vestio a été modifié.\nConnectez-vous avec votre nouveau mot de passe : " + loginUrl
                + "\nSi vous n’êtes pas à l’origine de cette modification, réinitialisez votre mot de passe et contactez-nous.\n";
        return message("Vestio — votre mot de passe a été modifié", "Votre mot de passe Vestio a été modifié.",
                "SÉCURITÉ DE VOTRE COMPTE", "Votre mot de passe a été modifié", content, text);
    }

    private Message message(String subject, String preheader, String eyebrow, String heading, String content, String text) {
        String html = interpolate(layout, Map.of("title", escape(subject), "preheader", escape(preheader),
                "eyebrow", escape(eyebrow), "heading", escape(heading), "content", content));
        return new Message(subject, html, text);
    }

    private static String load(String name) throws IOException {
        return new ClassPathResource("templates/email/" + name + ".html").getContentAsString(StandardCharsets.UTF_8);
    }

    // One pass: values cannot introduce new placeholders, markup, or regex replacements.
    private static String interpolate(String template, Map<String, String> values) {
        return PLACEHOLDER.matcher(template).replaceAll(match -> {
            String value = values.get(match.group(1));
            if (value == null) throw new IllegalArgumentException("Missing email template value: " + match.group(1));
            return Matcher.quoteReplacement(value);
        });
    }

    private static String escape(String value) {
        return HtmlUtils.htmlEscape(value);
    }

    public record Message(String subject, String html, String text) { }
}
