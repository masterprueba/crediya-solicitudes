package co.com.crediya.solicitudes.model.auth.gateways;

import co.com.crediya.solicitudes.model.auth.AuthenticatedUser;
import reactor.core.publisher.Mono;

/**
 * Gateway para obtener información de autenticación del contexto actual
 * Sin contaminar el dominio con detalles de infraestructura
 */
public interface AuthenticationContextProvider {
    
    /**
     * Obtiene el usuario autenticado del contexto actual
     */
    Mono<AuthenticatedUser> getAuthenticatedUser();
    
    /**
     * Obtiene el token de autenticación del contexto actual
     */
    Mono<String> getToken();
}
