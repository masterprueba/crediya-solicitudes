package co.com.crediya.solicitudes.api;

import co.com.crediya.solicitudes.usecase.crearsolicitud.CrearSolicitudUseCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.UUID;

@ExtendWith(MockitoExtension.class)
@DisplayName("CrearSolicitudHandler Test")
class CrearSolicitudHandlerTest {


    private CrearSolicitudUseCase useCase;

    @BeforeEach
    void setUp() {
        useCase = org.mockito.Mockito.mock(CrearSolicitudUseCase.class);
    }

    @Test
    @DisplayName("Debe crear una instancia de CrearSolicitudHandler")
    void debeCrearInstanciaDeCrearSolicitudHandler() {
        CrearSolicitudHandler handler = new CrearSolicitudHandler(useCase);
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

        var serverRequest = org.mockito.Mockito.mock(org.springframework.web.reactive.function.server.ServerRequest.class);
        var uriBuilder = org.mockito.Mockito.mock(org.springframework.web.util.UriBuilder.class);
        java.net.URI location = java.net.URI.create("/api/v1/solicitud/" + solicitud.getId());


        org.mockito.Mockito.when(serverRequest.bodyToMono(co.com.crediya.solicitudes.api.dto.CrearSolicitudRequest.class))
                .thenReturn(reactor.core.publisher.Mono.just(requestDto));
        org.mockito.Mockito.when(serverRequest.path()).thenReturn("/api/v1/solicitud");
        org.mockito.Mockito.when(serverRequest.uriBuilder()).thenReturn(uriBuilder);
        org.mockito.Mockito.when(uriBuilder.path(org.mockito.ArgumentMatchers.anyString())).thenReturn(uriBuilder);
        org.mockito.Mockito.when(uriBuilder.build(org.mockito.ArgumentMatchers.any(java.util.UUID.class))).thenReturn(location);


        try (var mockedStatic = org.mockito.Mockito.mockStatic(co.com.crediya.solicitudes.api.config.AuthenticationWebFilter.class)) {
            mockedStatic.when(co.com.crediya.solicitudes.api.config.AuthenticationWebFilter::getAuthenticatedUser)
                    .thenReturn(reactor.core.publisher.Mono.just(new co.com.crediya.solicitudes.model.auth.AuthenticatedUser("userId", "test@correo.com", "USER", "token")));

            org.mockito.Mockito.when(useCase.ejecutar(org.mockito.Mockito.any(), org.mockito.Mockito.any()))
                    .thenReturn(reactor.core.publisher.Mono.just(solicitud));

            var handler = new CrearSolicitudHandler(useCase);

            // Act
            var responseMono = handler.crear(serverRequest);

            // Assert
            var response = responseMono.block();
            org.junit.jupiter.api.Assertions.assertNotNull(response);
            org.junit.jupiter.api.Assertions.assertEquals(org.springframework.http.HttpStatus.CREATED.value(), response.statusCode().value());
            org.junit.jupiter.api.Assertions.assertEquals(location, response.headers().getLocation());
        }
    }


}
