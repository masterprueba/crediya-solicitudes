package co.com.crediya.solicitudes.consumer;

import co.com.crediya.solicitudes.model.solicitud.gateways.ClienteRepository;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
@Slf4j
public class ClienteRestAdapter implements ClienteRepository {
    private final WebClient client;

    @Override
    @CircuitBreaker(name = "clienteByDocumento", fallbackMethod = "existeClientePorDocumentoFallback")
    public Mono<Boolean> existeClientePorDocumento(String documento) {
        return client
                .get()
                .uri("/{documento}", documento)
                .exchangeToMono(this::mapExistsResponse);
    }

    private Mono<Boolean> mapExistsResponse(ClientResponse response) {
        if (response.statusCode().is2xxSuccessful()) {
            return Mono.just(true);
        }
        if (response.statusCode().value() == 404) {
            return Mono.just(false);
        }
        return response.createException().flatMap(Mono::error);
    }

    private Mono<Boolean> existeClientePorDocumentoFallback(String documento, Throwable ex) {
        log.warn("Fallback activado para clienteByDocumento. documento={}, error={}", documento, ex.toString());
        return Mono.just(false);
    }
}


