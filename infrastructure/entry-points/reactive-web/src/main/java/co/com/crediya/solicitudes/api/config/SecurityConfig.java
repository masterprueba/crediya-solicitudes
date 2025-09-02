package co.com.crediya.solicitudes.api.config;

import co.com.crediya.solicitudes.model.auth.AuthenticatedUser;
import co.com.crediya.solicitudes.model.auth.gateways.AuthValidationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.authentication.AuthenticationWebFilter;
import org.springframework.security.web.server.authentication.ServerAuthenticationConverter;
import org.springframework.security.web.server.authorization.ServerAccessDeniedHandler;
import reactor.core.publisher.Mono;

import java.util.Collections;

@Configuration
@EnableWebFluxSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final AuthValidationRepository authValidationRepository;

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {

        var authManager = reactiveJwtAuthManager();
        var converter = bearerConverter();

        var jwtWebFilter = new AuthenticationWebFilter(authManager);
        jwtWebFilter.setServerAuthenticationConverter(converter);

        return http.csrf(ServerHttpSecurity.CsrfSpec::disable)
                .httpBasic(ServerHttpSecurity.HttpBasicSpec::disable)
                .formLogin(ServerHttpSecurity.FormLoginSpec::disable)
                .authorizeExchange(ex -> ex
                        .pathMatchers("/actuator/**").permitAll()
                        .pathMatchers("/webjars/**").permitAll()
                        .pathMatchers("/v3/api-docs/**").permitAll()
                        .pathMatchers("/swagger-ui.html").permitAll()
                        .pathMatchers("/swagger-ui/**").permitAll()
                        .pathMatchers("/solicitudes/**").hasRole("CLIENTE")
                        .anyExchange().authenticated()
                )
                .exceptionHandling(ex -> ex.accessDeniedHandler(customAccessDeniedHandler()))
                .addFilterAt(jwtWebFilter, SecurityWebFiltersOrder.AUTHENTICATION)
                .build();
    }

    @Bean
    public ReactiveAuthenticationManager reactiveJwtAuthManager() {
        return authentication -> {
            String token = (String) authentication.getCredentials();
            return authValidationRepository.validateToken(token)
                .flatMap(user -> {
                    var authorities = Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + user.getRole()));
                    var authToken = new UsernamePasswordAuthenticationToken(user, token, authorities);
                    return Mono.just(authToken);
                });
        };
    }

    @Bean
    public ServerAuthenticationConverter bearerConverter() {
        return exchange -> Mono.justOrEmpty(exchange.getRequest().getHeaders().getFirst("Authorization"))
                .filter(h -> h.startsWith("Bearer "))
                .map(h -> h.substring(7))
                .map(token -> new UsernamePasswordAuthenticationToken(null, token));
    }

    @Bean
    public ServerAccessDeniedHandler customAccessDeniedHandler() {
        return (exchange, denied) -> {
            var response = exchange.getResponse();
            response.setStatusCode(HttpStatus.FORBIDDEN);
            response.getHeaders().setContentType(MediaType.APPLICATION_JSON);
            String body = """
                {
                  "status": 403,
                  "codigo": "ACCESO_DENEGADO",
                  "mensaje": "Acceso denegado: no tienes permisos suficientes para este recurso.",
                  "ruta": "%s"
                }
                """.formatted(exchange.getRequest().getPath());
            var buffer = response.bufferFactory().wrap(body.getBytes());
            return response.writeWith(Mono.just(buffer));
        };
    }

    public static Mono<AuthenticatedUser> getAuthenticatedUser() {
        return Mono.deferContextual(Mono::just)
            .flatMap(context -> {
                var authentication = context.get(org.springframework.security.core.Authentication.class);
                if (authentication.getPrincipal() instanceof AuthenticatedUser authenticatedUser) {

                    return Mono.just(authenticatedUser);
                }
                return Mono.empty();
            });
    }
}
