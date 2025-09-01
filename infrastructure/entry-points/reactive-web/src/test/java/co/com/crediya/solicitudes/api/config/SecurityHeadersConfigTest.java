package co.com.crediya.solicitudes.api.config;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.mock.http.server.reactive.MockServerHttpResponse;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@DisplayName("Security Headers Config Test")
class SecurityHeadersConfigTest {

    @Test
    @DisplayName("Debe agregar los headers de seguridad esperados")
    void agregaHeadersDeSeguridad() {
        // Arrange
        SecurityHeadersConfig filter = new SecurityHeadersConfig();
        ServerWebExchange exchange = mock(ServerWebExchange.class);
        MockServerHttpResponse response = new MockServerHttpResponse();
        WebFilterChain chain = mock(WebFilterChain.class);

        when(exchange.getResponse()).thenReturn(response);
        when(chain.filter(exchange)).thenReturn(Mono.empty());

        // Act & Assert
        StepVerifier.create(filter.filter(exchange, chain))
                .verifyComplete();

        HttpHeaders headers = response.getHeaders();
        assertThat(headers.getFirst("Content-Security-Policy"))
                .isEqualTo("default-src 'self'; frame-ancestors 'self'; form-action 'self'");
        assertThat(headers.getFirst("Strict-Transport-Security"))
                .isEqualTo("max-age=31536000;");
        assertThat(headers.getFirst("X-Content-Type-Options"))
                .isEqualTo("nosniff");
        assertThat(headers.getFirst("Server"))
                .isEmpty();
        assertThat(headers.getFirst("Cache-Control"))
                .isEqualTo("no-store");
        assertThat(headers.getFirst("Pragma"))
                .isEqualTo("no-cache");
        assertThat(headers.getFirst("Referrer-Policy"))
                .isEqualTo("strict-origin-when-cross-origin");
    }
}