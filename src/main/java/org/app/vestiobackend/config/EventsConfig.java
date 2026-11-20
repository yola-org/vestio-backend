package org.app.vestiobackend.config;

import com.vestio.common.publisher.AppSyncEventsPublisher;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import tools.jackson.databind.ObjectMapper;

import java.net.URI;
import java.net.http.HttpClient;
import java.time.Duration;

@Configuration
public class EventsConfig {

    @Bean
    public HttpClient appSyncEventsHttpClient() {
        return HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(5)).build();
    }

    // Singleton service bean. Local imports dev parameters; deployments supply env vars.
    @Bean
    public AppSyncEventsPublisher appSyncEventsPublisher(
            @Value("${APPSYNC_EVENTS_HTTP_ENDPOINT}") URI endpoint,
            @Value("${APPSYNC_EVENTS_REGION:${aws.region}}") String region,
            ObjectMapper objectMapper,
            HttpClient appSyncEventsHttpClient) {
        return new AppSyncEventsPublisher(endpoint, region, objectMapper,
                DefaultCredentialsProvider.builder().build(), appSyncEventsHttpClient);
    }
}
