package co.com.crediya.solicitudes.consumer;

import co.com.crediya.solicitudes.consumer.dto.ValidateTokenRequest;
import co.com.crediya.solicitudes.consumer.dto.ValidateTokenResponse;
import co.com.crediya.solicitudes.model.auth.AuthenticatedUser;
import co.com.crediya.solicitudes.model.auth.gateways.AuthValidationRepository;
import co.com.crediya.solicitudes.model.exceptions.DomainException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.time.Duration;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthValidationRestAdapter implements AuthValidationRepository {

    @Qualifier("authWebClient")
    private final WebClient authWebClient;

    @Override
    public Mono<AuthenticatedUser> validateToken(String token) {
        log.debug("Validando token con microservicio de autenticación");
        
        return authWebClient
                .post()
                .uri( "/auth/validate")
                .bodyValue(new ValidateTokenRequest(token))
                .retrieve()
                .bodyToMono(ValidateTokenResponse.class)
                .map(response -> AuthenticatedUser.builder()
                        .userId(response.userId())
                        .email(response.email())
                        .role(response.role())
                        .token(token)
                        .build())
                .doOnNext(user -> log.debug("Token validado exitosamente para usuario: {}", user.getEmail()))
                .retryWhen(Retry.backoff(2, Duration.ofMillis(100))
                        .filter(this::isRetryableError))
                .onErrorResume(WebClientResponseException.Unauthorized.class, e -> {
                    log.warn("Token inválido o expirado: {}", e.getMessage());
                    return Mono.error(new DomainException("TOKEN_INVALIDO", "Token inválido o expirado"));
                })
                .onErrorResume(WebClientResponseException.class, e -> {
                    log.error("Error al validar token con microservicio de autenticación. Status: {}, Mensaje: {}", 
                             e.getStatusCode(), e.getMessage());
                    return Mono.error(new DomainException("ERROR_VALIDACION_TOKEN", 
                                                         "Error interno al validar token"));
                })
                .onErrorResume(Exception.class, e -> {
                    log.error("Error inesperado al validar token: {}", e.getMessage(), e);
                    return Mono.error(new DomainException("ERROR_VALIDACION_TOKEN", 
                                                         "Error interno al validar token"));
                });
    }
    
    private boolean isRetryableError(Throwable throwable) {
        if (throwable instanceof WebClientResponseException webClientException) {
            HttpStatus status = (HttpStatus) webClientException.getStatusCode();
            // Reintentar solo en errores 5xx (errores del servidor)
            return status.is5xxServerError();
        }
        return false;
    }
}
