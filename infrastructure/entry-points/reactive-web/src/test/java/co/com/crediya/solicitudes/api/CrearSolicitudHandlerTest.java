package co.com.crediya.solicitudes.api;

import co.com.crediya.solicitudes.model.auth.AuthenticatedUser;
import co.com.crediya.solicitudes.usecase.crearsolicitud.CrearSolicitudUseCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;
import java.security.Principal;
import java.util.UUID;

@ExtendWith(MockitoExtension.class)
@DisplayName("CrearSolicitudHandler Test")
class CrearSolicitudHandlerTest {


    private CrearSolicitudUseCase useCase;
    private CrearSolicitudHandler handler;

    @BeforeEach
    void setUp() {
        useCase = org.mockito.Mockito.mock(CrearSolicitudUseCase.class);
        handler = new CrearSolicitudHandler(useCase);
    }

    @Test
    @DisplayName("Debe crear una instancia de CrearSolicitudHandler")
    void debeCrearInstanciaDeCrearSolicitudHandler() {
        org.junit.jupiter.api.Assertions.assertNotNull(handler);
    }

    @Test
    @DisplayName("Debe crear una solicitud y retornar ServerResponse con status 201")
    void crearDebeRetornar201YLocation() {
        // Arrange
        var requestDto = new co.com.crediya.solicitudes.api.dto.CrearSolicitudRequest(
                "test@correo.com", new BigDecimal("10000.0"), 12, "PERSONAL"
        );
        var solicitud = co.com.crediya.solicitudes.model.solicitud.Solicitud.builder()
                .id(UUID.randomUUID())
                .email(requestDto.email())
                .monto(requestDto.monto())
                .plazoMeses(requestDto.plazo_meses())
                .tipoPrestamo(requestDto.tipo_prestamo())
                .estado(co.com.crediya.solicitudes.model.solicitud.Estado.PENDIENTE_REVISION)
                .created(java.time.Instant.now())
                .build();

        var serverRequest = org.mockito.Mockito.mock(ServerRequest.class);
        var uriBuilder = org.mockito.Mockito.mock(org.springframework.web.util.UriBuilder.class);
        java.net.URI location = java.net.URI.create("/api/v1/solicitud/" + solicitud.getId());

        // Mock del principal
        var authenticatedUser = new AuthenticatedUser("userId", "test@correo.com", "USER", "token");
        var authentication = org.mockito.Mockito.mock(Authentication.class);
        Mono<Principal> principalMono = Mono.just(authentication);

        org.mockito.Mockito.when(authentication.getPrincipal()).thenReturn(authenticatedUser);
        // Cambia thenReturn por thenAnswer para evitar problemas con genéricos
        org.mockito.Mockito.when(serverRequest.principal()).thenAnswer(invocation -> principalMono);

        // Mocks de la solicitud
        org.mockito.Mockito.when(serverRequest.bodyToMono(co.com.crediya.solicitudes.api.dto.CrearSolicitudRequest.class))
                .thenReturn(Mono.just(requestDto));
        org.mockito.Mockito.when(serverRequest.path()).thenReturn("/api/v1/solicitud");
        org.mockito.Mockito.when(serverRequest.uriBuilder()).thenReturn(uriBuilder);
        org.mockito.Mockito.when(uriBuilder.path(org.mockito.ArgumentMatchers.anyString())).thenReturn(uriBuilder);
        org.mockito.Mockito.when(uriBuilder.build(solicitud.getId())).thenReturn(location);

        // Mock del caso de uso
        org.mockito.Mockito.when(useCase.ejecutar(org.mockito.ArgumentMatchers.any(co.com.crediya.solicitudes.model.solicitud.Solicitud.class), org.mockito.ArgumentMatchers.any(String.class)))
                .thenReturn(Mono.just(solicitud));

        // Act
        Mono<ServerResponse> responseMono = handler.crear(serverRequest);

        // Assert
        StepVerifier.create(responseMono)
                .assertNext(response -> {
                    org.junit.jupiter.api.Assertions.assertEquals(org.springframework.http.HttpStatus.CREATED, response.statusCode());
                    org.junit.jupiter.api.Assertions.assertEquals(location, response.headers().getLocation());
                })
                .verifyComplete();
    }
}