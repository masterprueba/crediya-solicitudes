package co.com.crediya.solicitudes.consumer;

import co.com.crediya.solicitudes.consumer.dto.ValidateTokenRequest;
import co.com.crediya.solicitudes.consumer.dto.ValidateTokenResponse;
import co.com.crediya.solicitudes.model.auth.AuthenticatedUser;
import co.com.crediya.solicitudes.model.auth.gateways.AuthValidationRepository;
import co.com.crediya.solicitudes.model.exceptions.DomainException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

@Service
public class AuthValidationRestAdapter implements AuthValidationRepository {
    
    private final WebClient authWebClient;
    private final Logger log = LoggerFactory.getLogger(AuthValidationRestAdapter.class);

    public AuthValidationRestAdapter(@Qualifier("authWebClient") WebClient authWebClient) {
        this.authWebClient = authWebClient;
    }

    @Override
    public Mono<AuthenticatedUser> validateToken(String token) {
        log.info("Validando token con el microservicio de autenticación");
        
        return authWebClient
            .post()
            .uri("/auth/validate")
            .bodyValue(new ValidateTokenRequest(token))
            .retrieve()
            .bodyToMono(ValidateTokenResponse.class)
            .map(response -> AuthenticatedUser.builder()
                .userId(response.userId())
                .email(response.email())
                .role(response.role())
                .token(token)
                .build())
            .doOnNext(user -> log.info("Token válido para usuario: {}, rol: {}", user.getEmail(), user.getRole()))
            .onErrorMap(WebClientResponseException.Unauthorized.class, ex -> {
                log.warn("Token inválido o expirado: {}", ex.getMessage());
                return new DomainException("token_invalido");
            })
            .onErrorMap(WebClientResponseException.class, ex -> {
                if (ex.getStatusCode() == HttpStatus.UNAUTHORIZED) {
                    return new DomainException("token_invalido");
                }
                log.error("Error comunicándose con el servicio de autenticación: {}", ex.getMessage());
                return new DomainException("servicio_autenticacion_no_disponible");
            });
    }
}
