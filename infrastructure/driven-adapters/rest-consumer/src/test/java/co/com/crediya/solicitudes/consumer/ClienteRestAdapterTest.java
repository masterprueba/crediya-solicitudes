package co.com.crediya.solicitudes.consumer;

import co.com.crediya.solicitudes.model.cliente.Cliente;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.Mockito.*;

class ClienteRestAdapterTest {

    private ClienteRestAdapter adapter;

    @Mock
    private WebClient webClient;
    @Mock
    private WebClient.RequestHeadersUriSpec requestSpec;
    @Mock
    private WebClient.RequestHeadersSpec headersSpec;
    @Mock
    private WebClient.ResponseSpec responseSpec;

    @BeforeEach
    void init() {
        MockitoAnnotations.openMocks(this);
        adapter = new ClienteRestAdapter(webClient);
    }

    @Test
    void obtenerClientePorEmail_ok() {
        Cliente cliente = Cliente.builder().usuario("Juan").email("a@b.com").documento_identidad("1").build();

        when(webClient.get()).thenReturn(requestSpec);
        when(requestSpec.uri("/cliente?email={email}", "a@b.com")).thenReturn(headersSpec);
        when(headersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(Cliente.class)).thenReturn(Mono.just(cliente));

        StepVerifier.create(adapter.obtenerClientePorEmail("a@b.com"))
                .expectNext(cliente)
                .verifyComplete();

        verify(webClient).get();
    }

    @Test
    void obtenerClientePorEmail_404_empty() {
        when(webClient.get()).thenReturn(requestSpec);
        when(requestSpec.uri("/cliente?email={email}", "x@x.com")).thenReturn(headersSpec);
        when(headersSpec.retrieve()).thenReturn(responseSpec);
        WebClientResponseException.NotFound notFound = mock(WebClientResponseException.NotFound.class);
        when(responseSpec.bodyToMono(Cliente.class)).thenReturn(Mono.error(notFound));

        StepVerifier.create(adapter.obtenerClientePorEmail("x@x.com"))
                .verifyComplete();
    }

    @Test
    void obtenerClientePorEmail_500_error() {
        when(webClient.get()).thenReturn(requestSpec);
        when(requestSpec.uri("/cliente?email={email}", "err@x.com")).thenReturn(headersSpec);
        when(headersSpec.retrieve()).thenReturn(responseSpec);
        WebClientResponseException.InternalServerError ise = mock(WebClientResponseException.InternalServerError.class);
        when(responseSpec.bodyToMono(Cliente.class)).thenReturn(Mono.error(ise));

        StepVerifier.create(adapter.obtenerClientePorEmail("err@x.com"))
                .expectError(WebClientResponseException.InternalServerError.class)
                .verify();
    }
}
