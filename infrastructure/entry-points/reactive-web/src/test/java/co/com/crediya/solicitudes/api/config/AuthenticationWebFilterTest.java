package co.com.crediya.solicitudes.api.config;

import co.com.crediya.solicitudes.model.auth.AuthenticatedUser;
import co.com.crediya.solicitudes.model.auth.gateways.AuthValidationRepository;
import co.com.crediya.solicitudes.model.exceptions.DomainException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.RequestPath;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.mock.http.server.reactive.MockServerHttpResponse;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@DisplayName("AuthenticationWebFilter Test")
class AuthenticationWebFilterTest {

    private AuthValidationRepository authValidationRepository;
    private AuthenticationWebFilter filter;
    private WebFilterChain chain;

    @BeforeEach
    void setUp() {
        authValidationRepository = mock(AuthValidationRepository.class);
        filter = new AuthenticationWebFilter(authValidationRepository);
        chain = mock(WebFilterChain.class);
    }

    @Test
    @DisplayName("Debe permitir acceso a endpoint público")
    void permiteEndpointPublico() {
        ServerWebExchange exchange = mockExchange("/actuator/health", null);
        when(chain.filter(exchange)).thenReturn(Mono.empty());

        StepVerifier.create(filter.filter(exchange, chain))
                .verifyComplete();

        verify(chain).filter(exchange);
        verifyNoInteractions(authValidationRepository);
    }

    @Test
    @DisplayName("Debe retornar 401 si falta Authorization")
    void retorna401SiFaltaAuthorization() {
        ServerWebExchange exchange = mockExchange("/api/privado", null);
        MockServerHttpResponse response = (MockServerHttpResponse) exchange.getResponse();

        StepVerifier.create(filter.filter(exchange, chain))
                .verifyComplete();

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        assertThat(response.getBodyAsString().block()).contains("NO_AUTORIZADO");
    }

    @Test
    @DisplayName("Debe retornar 401 si Authorization no es Bearer")
    void retorna401SiAuthorizationNoEsBearer() {
        ServerWebExchange exchange = mockExchange("/api/privado", "Basic abc");
        MockServerHttpResponse response = (MockServerHttpResponse) exchange.getResponse();

        StepVerifier.create(filter.filter(exchange, chain))
                .verifyComplete();

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    @DisplayName("Debe autenticar correctamente si el token es válido")
    void autenticaSiTokenValido() {
        String token = "tokenValido";
        ServerWebExchange exchange = mockExchange("/api/privado", "Bearer " + token);
        AuthenticatedUser user = AuthenticatedUser.builder()
                .userId("123")
                .email("test@test.com")
                .role("CLIENTE")
                .token(token)
                .build();
        when(authValidationRepository.validateToken(token)).thenReturn(Mono.just(user));

        final AtomicReference<AuthenticatedUser> userInContext = new AtomicReference<>();
        when(chain.filter(any(ServerWebExchange.class)))
                .thenReturn(Mono.deferContextual(contextView -> {
                    userInContext.set(contextView.get(AuthenticationWebFilter.USER_CONTEXT_KEY));
                    return Mono.empty();
                }));

        StepVerifier.create(filter.filter(exchange, chain))
                .verifyComplete();

        assertThat(userInContext.get()).isEqualTo(user);
        verify(chain).filter(exchange);
    }

    @Test
    @DisplayName("Debe retornar 401 si el token es inválido")
    void retorna401SiTokenInvalido() {
        String token = "tokenInvalido";
        ServerWebExchange exchange = mockExchange("/api/privado", "Bearer " + token);
        when(authValidationRepository.validateToken(token)).thenReturn(Mono.error(new DomainException("Token inválido")));
        MockServerHttpResponse response = (MockServerHttpResponse) exchange.getResponse();

        StepVerifier.create(filter.filter(exchange, chain))
                .verifyComplete();

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    private ServerWebExchange mockExchange(String path, String authHeader) {
        ServerWebExchange exchange = mock(ServerWebExchange.class);
        ServerHttpRequest request = mock(ServerHttpRequest.class);
        MockServerHttpResponse response = new MockServerHttpResponse();
        // Usa un Map real y siempre el mismo
        Map<String, Object> attributes = new HashMap<>();

        when(exchange.getRequest()).thenReturn(request);
        when(exchange.getResponse()).thenReturn(response);
        // Siempre retorna el mismo Map de atributos
        when(exchange.getAttributes()).thenReturn(attributes);
        when(request.getPath()).thenReturn(RequestPath.parse(path, null));
        HttpHeaders headers = new HttpHeaders();
        if (authHeader != null) headers.add(HttpHeaders.AUTHORIZATION, authHeader);
        when(request.getHeaders()).thenReturn(headers);

        return exchange;
    }
}
