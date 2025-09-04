package co.com.crediya.solicitudes.consumer;

import co.com.crediya.solicitudes.model.cliente.Cliente;
import co.com.crediya.solicitudes.model.cliente.ClienteToken;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ClienteRestAdapterTest {

    private WebClient mockWebClient;
    private WebClient.ResponseSpec mockResponseSpec;
    private ClienteRestAdapter adapter;

    private WebClient.RequestHeadersUriSpec mockUriSpec;
    private WebClient.RequestHeadersSpec mockHeadersSpec;

    @BeforeEach
    void setUp() {
        mockWebClient = mock(WebClient.class);
        mockUriSpec = mock(WebClient.RequestHeadersUriSpec.class);
        mockHeadersSpec = mock(WebClient.RequestHeadersSpec.class);
        mockResponseSpec = mock(WebClient.ResponseSpec.class);

        when(mockWebClient.get()).thenReturn(mockUriSpec);
        when(mockUriSpec.uri(anyString(), any(Object[].class))).thenReturn(mockHeadersSpec);
        when(mockHeadersSpec.header(anyString(), anyString())).thenReturn(mockHeadersSpec);
        when(mockHeadersSpec.retrieve()).thenReturn(mockResponseSpec);

        adapter = new ClienteRestAdapter(mockWebClient);
    }

    @Test
    void obtenerClientePorEmail_deberiaRetornarClienteCuandoExiste() {
        ClienteToken token = new ClienteToken("id123", "correo@dominio.com", "ROL_USUARIO", "token123");

        // Crea un mapa que simula la respuesta JSON
        Map<String, Object> responseMap = Map.of(
                "usuario", "John carlos",
                "email", "correo@dominio.com",
                "documento_identidad", "12345",
                "salario_base", new BigDecimal("50000")
        );

        // Configura el mock para que responda a bodyToMono(Map.class)
        when(mockResponseSpec.bodyToMono(Map.class)).thenReturn(Mono.just(responseMap));

        StepVerifier.create(adapter.obtenerClientePorEmail(token))
                .expectNextMatches(c -> c.getEmail().equals("correo@dominio.com"))
                .verifyComplete();
    }

    @Test
    void obtenerClientePorEmail_deberiaRetornarVacioCuandoNoExiste() {
        ClienteToken token = new ClienteToken("123", "test@test.com", "ASESOR", "token123");
        WebClientResponseException.NotFound notFound = (WebClientResponseException.NotFound) WebClientResponseException.create(
                404, "Not Found", null, null, null);

        // Ajusta el mock para que use Map.class
        when(mockResponseSpec.bodyToMono(Map.class)).thenReturn(Mono.error(notFound));

        StepVerifier.create(adapter.obtenerClientePorEmail(token))
                .verifyComplete();
    }

    @Test
    void obtenerClientePorEmail_deberiaLlamarFallbackDirectamente() throws Exception {
        ClienteToken token = new ClienteToken("123", "test@test.com", "ASESOR", "token123");
        Throwable exception = new RuntimeException("Error de prueba");

        // Usar reflexión para invocar el método privado de fallback
        Method fallbackMethod = ClienteRestAdapter.class.getDeclaredMethod("obtenerClientePorEmailFallback", ClienteToken.class, Throwable.class);
        fallbackMethod.setAccessible(true);

        @SuppressWarnings("unchecked")
        Mono<Cliente> fallbackMono = (Mono<Cliente>) fallbackMethod.invoke(adapter, token, exception);

        StepVerifier.create(fallbackMono)
                .verifyComplete();
    }
}