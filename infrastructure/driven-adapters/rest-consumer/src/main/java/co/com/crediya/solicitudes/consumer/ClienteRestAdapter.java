package co.com.crediya.solicitudes.consumer;

import co.com.crediya.solicitudes.model.solicitud.gateways.ClienteRepository;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
@Slf4j
public class ClienteRestAdapter implements ClienteRepository {
    private final WebClient authWebClient;

    public ClienteRestAdapter(@Qualifier("authWebClient") WebClient authWebClient) {
        this.authWebClient = authWebClient;
    }

    @Override
    @CircuitBreaker(name = "clienteByDocumento", fallbackMethod = "existeClientePorDocumentoFallback")
    public Mono<Boolean> existeClientePorDocumento(String documento) {
        return Mono.just(true);
        // log.info("Consultando cliente por documento. documento={}", documento);
        // return authWebClient
        //         .get()
        //         .uri("/{documento}", documento)
        //         .exchangeToMono(this::mapExistsResponse);
    }

    private Mono<Boolean> mapExistsResponse(ClientResponse response) {
        int statusCode = response.statusCode().value();
        log.info("Respuesta del cliente por documento. status={}", statusCode);
        
        if (response.statusCode().is2xxSuccessful()) {
            log.info("Cliente encontrado exitosamente");
            return Mono.just(true);
        }
        if (statusCode == 404) {
            log.info("Cliente no encontrado (404) - Simulando que existe para pruebas");
            return Mono.just(true); // Temporal: simular que existe
        }
        if (statusCode >= 500) {
            log.error("Error del servidor al consultar cliente. status={}", statusCode);
            return Mono.just(false);
        }
        
        log.warn("Respuesta inesperada del servicio de autenticación. status={}", statusCode);
        return response.createException().flatMap(Mono::error);
    }

    private Mono<Boolean> existeClientePorDocumentoFallback(String documento, Throwable ex) {
        log.warn("Fallback activado para clienteByDocumento. documento={}, error={}", documento, ex.toString());
        return Mono.just(true);
    }
}


