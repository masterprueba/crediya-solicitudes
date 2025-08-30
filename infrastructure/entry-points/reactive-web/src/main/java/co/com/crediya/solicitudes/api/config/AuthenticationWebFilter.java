package co.com.crediya.solicitudes.api.config;

import co.com.crediya.solicitudes.model.auth.AuthenticatedUser;
import co.com.crediya.solicitudes.model.auth.gateways.AuthValidationRepository;
import co.com.crediya.solicitudes.model.exceptions.DomainException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

@Component
@Order(-100)
public class AuthenticationWebFilter implements WebFilter {

    private static final Logger log = LoggerFactory.getLogger(AuthenticationWebFilter.class);
    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String BEARER_PREFIX = "Bearer ";
    private static final String USER_CONTEXT_KEY = "authenticated.user";

    private final AuthValidationRepository authValidationRepository;

    public AuthenticationWebFilter(AuthValidationRepository authValidationRepository) {
        this.authValidationRepository = authValidationRepository;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        String path = exchange.getRequest().getPath().toString();

        if (isPublicEndpoint(path)) {
            log.debug("Endpoint público, omitiendo validación: {}", path);
            return chain.filter(exchange);
        }

        String authHeader = exchange.getRequest().getHeaders().getFirst(AUTHORIZATION_HEADER);
        
        if (authHeader == null || !authHeader.startsWith(BEARER_PREFIX)) {
            log.warn("Token de autorización faltante o inválido para: {}", path);
            return unauthorizedResponse(exchange);
        }

        String token = authHeader.substring(BEARER_PREFIX.length());

        return authValidationRepository.validateToken(token)
            .flatMap(user -> {
                log.info("Usuario autenticado: {} con rol: {}", user.getEmail(), user.getRole());
                exchange.getAttributes().put(USER_CONTEXT_KEY, user);
                return chain.filter(exchange);
            })
            .onErrorResume(DomainException.class, ex -> {
                log.warn("Error de autenticación: {}", ex.getMessage());
                return unauthorizedResponse(exchange);
            })
            .onErrorResume(throwable -> {
                log.error("Error inesperado durante la autenticación: {}", throwable.getMessage());
                return unauthorizedResponse(exchange);
            });
    }

    private boolean isPublicEndpoint(String path) {
        return path.startsWith("/actuator/") || 
               path.startsWith("/health") ||
               path.startsWith("/webjars/") ||
               path.startsWith("/v3/api-docs") ||
               path.startsWith("/swagger-ui");
    }

    private Mono<Void> unauthorizedResponse(ServerWebExchange exchange) {
        exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
        exchange.getResponse().getHeaders().add(HttpHeaders.CONTENT_TYPE, "application/json");
        
        String body = """
            {
              "status": 401,
              "codigo": "NO_AUTORIZADO",
              "mensaje": "Token de autenticación requerido o inválido",
              "ruta": "%s"
            }
            """.formatted(exchange.getRequest().getPath());
        
        var buffer = exchange.getResponse().bufferFactory().wrap(body.getBytes());
        return exchange.getResponse().writeWith(Mono.just(buffer));
    }

    public static AuthenticatedUser getAuthenticatedUser(ServerWebExchange exchange) {
        return (AuthenticatedUser) exchange.getAttributes().get(USER_CONTEXT_KEY);
    }
}
