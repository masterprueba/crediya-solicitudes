package co.com.crediya.solicitudes.consumer;

import co.com.crediya.solicitudes.model.cliente.ClienteToken;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class ClienteRestAdapterTest {

    private WebClient mockWebClient;
    private WebClient.RequestHeadersUriSpec mockUriSpec;
    private WebClient.RequestHeadersSpec mockHeadersSpec;
    private WebClient.ResponseSpec mockResponseSpec;
    private ClienteRestAdapter adapter;

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
        Map<String, Object> responseMap = Map.of(
                "usuario", "usuario1",
                "email", "correo@dominio.com",
                "documento_identidad", "123456",
                "salario_base", 1500.0
        );
        when(mockResponseSpec.bodyToMono(Map.class)).thenReturn(Mono.just(responseMap));

        ClienteToken token = ClienteToken.builder().email("correo@dominio.com").token("tokenValido").build();

        StepVerifier.create(adapter.obtenerClientePorEmail(token))
                .expectNextMatches(cliente ->
                        cliente.getUsuario().equals("usuario1") &&
                                cliente.getEmail().equals("correo@dominio.com") &&
                                cliente.getDocumentoIdentidad().equals("123456") &&
                                cliente.getSalarioBase().equals(BigDecimal.valueOf(1500.0))
                )
                .verifyComplete();
    }

    @Test
    void obtenerClientePorEmail_deberiaRetornarMonoEmptyCuandoNoExiste() {
        WebClientResponseException notFound = WebClientResponseException.create(
                404, "Not Found", null, null, null
        );
        when(mockResponseSpec.bodyToMono(Map.class)).thenReturn(Mono.error(notFound));

        ClienteToken token = ClienteToken.builder().email("noexiste@dominio.com").token("tokenValido").build();

        StepVerifier.create(adapter.obtenerClientePorEmail(token))
                .expectComplete()
                .verify();
    }
}