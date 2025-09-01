package co.com.crediya.solicitudes.api.config;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.http.server.reactive.ServerHttpRequest;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Cors Config Test")
class CorsConfigTest {

    @Test
    @DisplayName("Debe crear CorsWebFilter con la configuración esperada")
    void corsWebFilter_creaConfiguracionCorrecta() {
        // Arrange
        String allowedOrigins = "http://localhost:3000,http://otro.com";
        CorsConfig corsConfig = new CorsConfig();

        // Act
        CorsWebFilter filter = corsConfig.corsWebFilter(allowedOrigins);

        // Obtener la configuración usando un mock de ServerWebExchange
        UrlBasedCorsConfigurationSource source = (UrlBasedCorsConfigurationSource)
                org.springframework.test.util.ReflectionTestUtils.getField(filter, "configSource");

        ServerWebExchange exchange = Mockito.mock(ServerWebExchange.class);
        ServerHttpRequest request = Mockito.mock(ServerHttpRequest.class);
        Mockito.when(exchange.getRequest()).thenReturn(request);
        Mockito.when(request.getPath()).thenReturn(org.springframework.http.server.RequestPath.parse("/usuarios", null));

        assert source != null;
        CorsConfiguration config = source.getCorsConfiguration(exchange);

        assertThat(config).isNotNull();
        assertThat(config.getAllowedOrigins()).containsExactlyInAnyOrder("http://localhost:3000", "http://otro.com");
        assertThat(config.getAllowedMethods()).containsExactlyInAnyOrder("POST", "GET");
        assertThat(config.getAllowedHeaders()).containsExactlyInAnyOrder("Authorization", "Content-Type", "Accept");
        assertThat(config.getAllowCredentials()).isTrue();
    }
}
