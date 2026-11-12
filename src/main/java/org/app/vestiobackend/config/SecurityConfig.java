package org.app.vestiobackend.config;

import org.app.vestiobackend.config.filters.LocalMockAuthFilter;
import org.app.vestiobackend.security.ApiAuthenticationInterceptor;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.oauth2.server.resource.web.authentication.BearerTokenAuthenticationFilter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.config.Customizer;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.servlet.handler.MappedInterceptor;

import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private static final String API_PATH = "/api/**";

    private final LocalMockAuthFilter localMockAuthFilter;

    public SecurityConfig(ObjectProvider<LocalMockAuthFilter> localMockAuthFilterProvider) {
        this.localMockAuthFilter = localMockAuthFilterProvider.getIfAvailable();
    }

    @Bean
    @Profile("!local")
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(AbstractHttpConfigurer::disable)
            .authorizeHttpRequests(authorize -> authorize
                .requestMatchers("/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html").permitAll()
                // MVC checks the policy on the handler it selects for the request.
                .requestMatchers(API_PATH).permitAll()
                .anyRequest().authenticated()
            )
            .oauth2ResourceServer(oauth2 -> oauth2.jwt(Customizer.withDefaults()));
        return http.build();
    }

    @Bean
    @Profile("local")
    public SecurityFilterChain localSecurityFilterChain(HttpSecurity http) throws Exception {
        http
            .cors(Customizer.withDefaults())
            .csrf(AbstractHttpConfigurer::disable)
            .authorizeHttpRequests(authorize -> authorize
                .requestMatchers("/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html").permitAll()
                // MVC checks the policy on the handler it selects for the request.
                .requestMatchers(API_PATH).permitAll()
                .anyRequest().authenticated()
            )
            .oauth2ResourceServer(oauth2 -> oauth2.jwt(Customizer.withDefaults()));

        if (localMockAuthFilter != null) {
            http.addFilterBefore(localMockAuthFilter, BearerTokenAuthenticationFilter.class);
        }

        return http.build();
    }

    @Bean
    public MappedInterceptor apiAuthenticationInterceptor() {
        return new MappedInterceptor(new String[]{API_PATH}, new ApiAuthenticationInterceptor());
    }

    @Bean
    @Profile("local")
    public CorsConfigurationSource localCorsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of("http://localhost:3000"));
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("Authorization", "Content-Type"));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration(API_PATH, configuration);
        return source;
    }
}
