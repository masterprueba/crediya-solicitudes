package co.com.crediya.solicitudes.consumer.config;

import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.client.reactive.ClientHttpConnector;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;

import static io.netty.channel.ChannelOption.CONNECT_TIMEOUT_MILLIS;
import static java.util.concurrent.TimeUnit.MILLISECONDS;

@Configuration
public class AuthServiceConfig {

    private final String authServiceUrl;
    private final int authServiceTimeout;

    public AuthServiceConfig(@Value("${adapter.auth-service.url}") String authServiceUrl,
                            @Value("${adapter.auth-service.timeout}") int authServiceTimeout) {
        this.authServiceUrl = authServiceUrl;
        this.authServiceTimeout = authServiceTimeout;
    }

    @Bean("authWebClient")
    public WebClient authWebClient(WebClient.Builder builder) {
        return builder
            .baseUrl(authServiceUrl)
            .defaultHeader(HttpHeaders.CONTENT_TYPE, "application/json")
            .clientConnector(getAuthClientHttpConnector())
            .build();
    }

    private ClientHttpConnector getAuthClientHttpConnector() {
        return new ReactorClientHttpConnector(HttpClient.create()
                .compress(true)
                .keepAlive(true)
                .option(CONNECT_TIMEOUT_MILLIS, authServiceTimeout)
                .doOnConnected(connection -> {
                    connection.addHandlerLast(new ReadTimeoutHandler(authServiceTimeout, MILLISECONDS));
                    connection.addHandlerLast(new WriteTimeoutHandler(authServiceTimeout, MILLISECONDS));
                }));
    }
}
