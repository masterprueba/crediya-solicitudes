package co.com.crediya.solicitudes.sqs.listener.config;

import co.com.crediya.solicitudes.sqs.listener.helper.SQSListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.core.publisher.Mono;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProviderChain;
import software.amazon.awssdk.auth.credentials.ContainerCredentialsProvider;
import software.amazon.awssdk.auth.credentials.EnvironmentVariableCredentialsProvider;
import software.amazon.awssdk.auth.credentials.InstanceProfileCredentialsProvider;
import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.auth.credentials.SystemPropertyCredentialsProvider;
import software.amazon.awssdk.auth.credentials.WebIdentityTokenFileCredentialsProvider;
import software.amazon.awssdk.metrics.MetricPublisher;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sqs.SqsAsyncClient;
import software.amazon.awssdk.services.sqs.model.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import java.net.URI;
import java.util.function.Function;

@Configuration
public class SQSConfig {

    Logger log = LoggerFactory.getLogger(SQSConfig.class);



    @Bean(initMethod = "start")
    public SQSListener sqsListener(SqsAsyncClient client, SQSProperties properties, Function<Message, Mono<Void>> fn) {
        log.info("🔧 Creando SQS Listener bean - Queue: {}", properties.queueUrl());
        return SQSListener.builder()
                .client(client)
                .properties(properties)
                .processor(fn)
                .build()
                .start();
    }

    @Bean
    public SqsAsyncClient configSqs(SQSProperties properties, MetricPublisher publisher) {
        var builder = SqsAsyncClient.builder()
                .region(Region.of(properties.region()))
                .overrideConfiguration(o -> o.addMetricPublisher(publisher))
                .credentialsProvider(getProviderChain());

        var endpoint = resolveEndpoint(properties);
        if (endpoint != null) {
            builder.endpointOverride(endpoint);
        }

        return builder.build();
    }

    private AwsCredentialsProviderChain getProviderChain() {
        return AwsCredentialsProviderChain.builder()
                .addCredentialsProvider(EnvironmentVariableCredentialsProvider.create())
                .addCredentialsProvider(SystemPropertyCredentialsProvider.create())
                .addCredentialsProvider(WebIdentityTokenFileCredentialsProvider.create())
                .addCredentialsProvider(ProfileCredentialsProvider.create())
                .addCredentialsProvider(ContainerCredentialsProvider.builder().build())
                .addCredentialsProvider(InstanceProfileCredentialsProvider.create())
                .build();
    }

    protected URI resolveEndpoint(SQSProperties properties) {
        String endpointValue = properties.endpoint();
        if (endpointValue == null) {
            return null;
        }
        String trimmed = endpointValue.trim();
        if (trimmed.isEmpty()) {
            return null;
        }
        try {
            URI candidate = URI.create(trimmed);
            if (candidate.getScheme() == null || candidate.getScheme().isBlank()) {
                return null;
            }
            return candidate;
        } catch (IllegalArgumentException ex) {
            return null;
        }
    }
}
