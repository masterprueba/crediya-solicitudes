package co.com.crediya.solicitudes.sqs.sender.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "adapter.sqs")
public record SQSSenderProperties(
     String region,
     String endpoint,
     QueueConfig notificaciones,
     QueueConfig reporteCambiosEstado,
     QueueConfig capacidadEndeudamiento) {
     
     public record QueueConfig(String url) {}
}
