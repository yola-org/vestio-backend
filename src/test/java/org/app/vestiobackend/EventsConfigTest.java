package org.app.vestiobackend;

import io.awspring.cloud.parameterstore.ParameterStorePropertySource;
import org.springframework.boot.env.YamlPropertySourceLoader;
import org.springframework.core.io.FileSystemResource;
import software.amazon.awssdk.services.ssm.SsmClient;
import software.amazon.awssdk.services.ssm.model.GetParametersByPathRequest;
import software.amazon.awssdk.services.ssm.model.GetParametersByPathResponse;
import software.amazon.awssdk.services.ssm.model.Parameter;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import org.app.vestiobackend.config.EventsConfig;
import com.vestio.common.publisher.AppSyncEventsPublisher;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.json.JsonMapper;

import static org.assertj.core.api.Assertions.assertThat;

class EventsConfigTest {

    private final ApplicationContextRunner runner = new ApplicationContextRunner()
            .withUserConfiguration(EventsConfig.class)
            .withBean(ObjectMapper.class, () -> JsonMapper.builder().build())
            .withPropertyValues("aws.region=ca-central-1");

    @Test
    void createsSingletonPublisherFromResolvedProperties() {
        runner.withPropertyValues("APPSYNC_EVENTS_HTTP_ENDPOINT=https://example.appsync-api.ca-central-1.amazonaws.com/event")
                .run(context -> {
                    assertThat(context).hasSingleBean(AppSyncEventsPublisher.class);
                    assertThat(context.getBean(AppSyncEventsPublisher.class))
                            .isSameAs(context.getBean(AppSyncEventsPublisher.class));
                });
    }

    @Test
    void failsAtStartupWhenEndpointIsMissing() {
        runner.run(context -> assertThat(context).hasFailed());
    }
    @Test
    void localYamlImportsDevParameterPathAndResolvesPublisher() throws Exception {
        var loader = new YamlPropertySourceLoader();
        var base = loader.load("base", new FileSystemResource("src/main/resources/application.yaml")).getFirst();
        var local = loader.load("local", new FileSystemResource("src/main/resources/application-local.yaml")).getFirst();
        String location = (String) local.getProperty("spring.config.import[0]");
        assertThat(location).startsWith("aws-parameterstore:");

        var ssm = mock(SsmClient.class);
        when(ssm.getParametersByPath(any(GetParametersByPathRequest.class))).thenAnswer(invocation -> {
            GetParametersByPathRequest request = invocation.getArgument(0);
            assertThat(request.path()).isEqualTo("/DEV/");
            return GetParametersByPathResponse.builder().parameters(
                    Parameter.builder().name("/DEV/APPSYNC_EVENTS_HTTP_ENDPOINT")
                            .value("https://example.appsync-api.ca-central-1.amazonaws.com/event").build(),
                    Parameter.builder().name("/DEV/APPSYNC_EVENTS_REGION").value("ca-central-1").build()).build();
        });
        var parameters = new ParameterStorePropertySource(location.substring("aws-parameterstore:".length()), ssm);
        parameters.init();

        runner.withInitializer(context -> {
            var sources = context.getEnvironment().getPropertySources();
            sources.addLast(base);
            sources.addFirst(local);
            sources.addFirst(parameters);
        }).run(context -> {
            assertThat(context).hasNotFailed().hasSingleBean(AppSyncEventsPublisher.class);
            assertThat(context.getEnvironment().getProperty("spring.cloud.aws.parameterstore.enabled", Boolean.class)).isTrue();
            assertThat(context.getEnvironment().getProperty("spring.cloud.aws.parameterstore.region")).isNotBlank();
            assertThat(context.getEnvironment().getProperty("APPSYNC_EVENTS_HTTP_ENDPOINT"))
                    .isEqualTo("https://example.appsync-api.ca-central-1.amazonaws.com/event");
        });
    }

}
