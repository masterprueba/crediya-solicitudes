package co.com.crediya.solicitudes.model.auth.gateways;

import co.com.crediya.solicitudes.model.auth.AuthenticatedUser;
import reactor.core.publisher.Mono;

/**
 * Gateway para validar tokens de autenticación con el microservicio de autenticación
 */
public interface AuthValidationRepository {
    
    /**
     * Valida un token JWT y retorna la información del usuario autenticado
     * @param token Token JWT a validar
     * @return Información del usuario autenticado o error si el token es inválido
     */
    Mono<AuthenticatedUser> validateToken(String token);
}
