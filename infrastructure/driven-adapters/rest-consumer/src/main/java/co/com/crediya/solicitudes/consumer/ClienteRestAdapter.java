package co.com.crediya.solicitudes.consumer;

import co.com.crediya.solicitudes.model.auth.gateways.AuthenticationContextProvider;
import co.com.crediya.solicitudes.model.cliente.Cliente;
import co.com.crediya.solicitudes.model.cliente.gateways.ClienteRepository;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.Map;

@Service
public class ClienteRestAdapter implements ClienteRepository {
    private final WebClient authWebClient;
    private final AuthenticationContextProvider authContextProvider;

    private final Logger log = LoggerFactory.getLogger(ClienteRestAdapter.class);

    public ClienteRestAdapter(@Qualifier("authWebClient") WebClient authWebClient,
                             AuthenticationContextProvider authContextProvider) {
        this.authWebClient = authWebClient;
        this.authContextProvider = authContextProvider;
    }

    @Override
    @CircuitBreaker(name = "clienteByEmail", fallbackMethod = "obtenerClientePorEmailFallback")
    public Mono<Cliente> obtenerClientePorEmail(String email) {
        log.info("Consultando cliente por email. email={}", email);
        
        return authContextProvider.getToken()
                .flatMap(token -> authWebClient
                        .get()
                        .uri("/cliente?email={email}", email)
                        .header("Authorization", "Bearer " + token)
                        .retrieve()
                        .bodyToMono(Map.class)
                        .map(data -> new Cliente(
                                (String) data.get("usuario"),
                                (String) data.get("email"),
                                (String) data.get("documento_identidad"),
                                BigDecimal.valueOf(((Number) data.get("salario_base")).doubleValue())
                        ))
                        .doOnNext(cliente -> log.info("obtenerClientePorEmail respuesta: {}", cliente))
                        .onErrorResume(WebClientResponseException.NotFound.class, e -> {
                            log.warn("Cliente no encontrado (404) para el email: {}  mensaje: {}", email, e.getMessage());
                            return Mono.empty();
                        })
                );
    }

    private Mono<Cliente> obtenerClientePorEmailFallback(String email, Throwable ex) {
        log.warn("Fallback activado para obtenerClientePorEmail. email={}, error={}", email, ex.getMessage());
        return Mono.empty();
    }
}


