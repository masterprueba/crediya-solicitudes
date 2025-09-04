package co.com.crediya.solicitudes.consumer;

import co.com.crediya.solicitudes.model.cliente.Cliente;
import co.com.crediya.solicitudes.model.cliente.ClienteToken;
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

    private final Logger log = LoggerFactory.getLogger(ClienteRestAdapter.class);

    public ClienteRestAdapter(@Qualifier("authWebClient") WebClient authWebClient) {
        this.authWebClient = authWebClient;
    }

    @Override
    @CircuitBreaker(name = "clienteByEmail", fallbackMethod = "obtenerClientePorEmailFallback")
    public Mono<Cliente> obtenerClientePorEmail(ClienteToken clienteToken) {
        log.info("Consultando cliente por email. email={}", clienteToken.getEmail());
        return authWebClient
                .get()
                .uri("/cliente?email={email}", clienteToken.getEmail())
                .header("Authorization", "Bearer " + clienteToken.getToken())
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
                    log.warn("Cliente no encontrado (404) para el email: {}  mensaje: {}", clienteToken.getEmail(),e.getMessage());
                    return Mono.empty();
                });
    }

    private Mono<Cliente> obtenerClientePorEmailFallback(ClienteToken clienteToken, Throwable ex) {
        log.warn("Fallback activado para obtenerClientePorEmail. email={}, error={}", clienteToken.getEmail(), ex.getMessage());
        return Mono.empty();
    }
}


