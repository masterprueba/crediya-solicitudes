package co.com.crediya.solicitudes.api.context;

import co.com.crediya.solicitudes.model.auth.AuthenticatedUser;
import co.com.crediya.solicitudes.model.auth.gateways.AuthenticationContextProvider;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import reactor.util.context.Context;

/**
 * Implementación de AuthenticationContextProvider usando Reactor Context
 * Mantiene los detalles de infraestructura fuera del dominio
 */
@Component
public class ReactiveAuthenticationContextProvider implements AuthenticationContextProvider {
    
    public static final String AUTH_USER_KEY = "authenticated_user";
    public static final String TOKEN_KEY = "auth_token";
    
    @Override
    public Mono<AuthenticatedUser> getAuthenticatedUser() {
        return Mono.deferContextual(contextView -> {
            if (contextView.hasKey(AUTH_USER_KEY)) {
                return Mono.just(contextView.get(AUTH_USER_KEY));
            }
            return Mono.empty();
        });
    }
    
    @Override
    public Mono<String> getToken() {
        return getAuthenticatedUser()
                .map(AuthenticatedUser::getToken)
                .switchIfEmpty(Mono.deferContextual(contextView -> {
                    if (contextView.hasKey(TOKEN_KEY)) {
                        return Mono.just(contextView.get(TOKEN_KEY));
                    }
                    return Mono.empty();
                }));
    }
    

    public static Context withAuthentication(AuthenticatedUser user) {
        return Context.of(AUTH_USER_KEY, user, TOKEN_KEY, user.getToken());
    }
}
