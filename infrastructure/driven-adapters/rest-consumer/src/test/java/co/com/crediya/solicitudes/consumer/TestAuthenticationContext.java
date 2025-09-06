package co.com.crediya.solicitudes.consumer;

import co.com.crediya.solicitudes.model.auth.AuthenticatedUser;
import reactor.util.context.Context;

/**
 * Clase utilitaria para tests que necesitan establecer contexto de autenticación
 */
public class TestAuthenticationContext {
    
    public static final String AUTH_USER_KEY = "authenticated_user";
    public static final String TOKEN_KEY = "auth_token";
    
    public static Context withAuthentication(AuthenticatedUser user) {
        return Context.of(AUTH_USER_KEY, user, TOKEN_KEY, user.getToken());
    }
}
