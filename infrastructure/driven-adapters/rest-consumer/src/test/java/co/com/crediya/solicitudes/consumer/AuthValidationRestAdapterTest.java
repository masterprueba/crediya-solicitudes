package co.com.crediya.solicitudes.consumer;

import co.com.crediya.solicitudes.consumer.dto.ValidateTokenRequest;
import co.com.crediya.solicitudes.consumer.dto.ValidateTokenResponse;
import co.com.crediya.solicitudes.model.exceptions.DomainException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class AuthValidationRestAdapterTest {
    private WebClient mockWebClient;
    private WebClient.RequestBodyUriSpec mockUriSpec;
    private WebClient.RequestHeadersSpec mockHeadersSpec;
    private WebClient.ResponseSpec mockResponseSpec;
    private AuthValidationRestAdapter adapter;

    @BeforeEach
    void setUp() {
        mockWebClient = mock(WebClient.class);
        mockUriSpec = mock(WebClient.RequestBodyUriSpec.class);
        mockHeadersSpec = mock(WebClient.RequestHeadersSpec.class);
        mockResponseSpec = mock(WebClient.ResponseSpec.class);

        when(mockWebClient.post()).thenReturn(mockUriSpec);
        when(mockUriSpec.uri(any(String.class))).thenReturn(mockUriSpec);
        when(mockUriSpec.bodyValue(any(ValidateTokenRequest.class))).thenReturn(mockHeadersSpec);
        when(mockHeadersSpec.retrieve()).thenReturn(mockResponseSpec);

        adapter = new AuthValidationRestAdapter(mockWebClient);
    }

    @Test
    void validateToken_deberiaRetornarUsuarioAutenticadoCuandoTokenEsValido() {
        String token = "tokenValido";
        ValidateTokenResponse response = new ValidateTokenResponse("userId", "correo@dominio.com", "ROL_USUARIO");
        when(mockResponseSpec.bodyToMono(ValidateTokenResponse.class)).thenReturn(Mono.just(response));

        StepVerifier.create(adapter.validateToken(token))
                .expectNextMatches(user ->
                        user.getUserId().equals("userId") &&
                                user.getEmail().equals("correo@dominio.com") &&
                                user.getRole().equals("ROL_USUARIO") &&
                                user.getToken().equals(token)
                )
                .verifyComplete();
    }

    @Test
    void validateToken_deberiaRetornarDomainExceptionCuandoTokenEsInvalido() {
        String token = "tokenInvalido";
        WebClientResponseException unauthorized = WebClientResponseException.create(
                HttpStatus.UNAUTHORIZED.value(), "Unauthorized", null, null, null);

        when(mockResponseSpec.bodyToMono(ValidateTokenResponse.class)).thenReturn(Mono.error(unauthorized));

        StepVerifier.create(adapter.validateToken(token))
                .expectErrorMatches(error -> error instanceof DomainException &&
                        error.getMessage().equals("token_invalido"))
                .verify();
    }

}