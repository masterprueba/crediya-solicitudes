package co.com.crediya.solicitudes.consumer.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {


    @Value("${adapter.auth-service.url}")
    private String authServiceUrl;

    @Bean("authWebClient")
    public WebClient authWebClient(WebClient.Builder builder) {
        return builder.baseUrl(authServiceUrl).build();
    }
}
