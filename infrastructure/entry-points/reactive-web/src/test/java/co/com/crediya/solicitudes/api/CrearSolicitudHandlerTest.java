package co.com.crediya.solicitudes.api;

import co.com.crediya.solicitudes.api.dto.CrearSolicitudRequest;
import co.com.crediya.solicitudes.model.solicitud.Estado;
import co.com.crediya.solicitudes.model.solicitud.Solicitud;
import co.com.crediya.solicitudes.usecase.crearsolicitud.CrearSolicitudUseCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CrearSolicitudHandlerTest {

    @Mock
    private CrearSolicitudUseCase useCase;

    @InjectMocks
    private CrearSolicitudHandler handler;

    private WebTestClient webTestClient;

    @BeforeEach
    void setUp() {
        RouterRest router = new RouterRest();
        webTestClient = WebTestClient.bindToRouterFunction(router.routerFunction(handler)).build();
    }

    @Test
    void crear_ok_retorna201() {
        UUID id = UUID.randomUUID();
        Solicitud salida = Solicitud.builder()
                .id(id)
                .email("juan.perez@example.com")
                .monto(new BigDecimal("5000000"))
                .plazoMeses(24)
                .tipoPrestamo("LIBRE_INVERSION")
                .tipoPrestamoId(UUID.randomUUID())
                .estado(Estado.PENDIENTE_REVISION)
                .created(Instant.now())
                .build();

        when(useCase.ejecutar(any(Solicitud.class))).thenReturn(Mono.just(salida));

        CrearSolicitudRequest req = new CrearSolicitudRequest(
                "juan.perez@example.com", new BigDecimal("5000000"), 24, "LIBRE_INVERSION");

        webTestClient.post()
                .uri("/solicitud")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(req)
                .exchange()
                .expectStatus().isCreated()
                .expectHeader().valueMatches("Location", ".*/api/v1/solicitud/" + id + "$")
                .expectBody()
                .jsonPath("$.id").isEqualTo(id.toString());
    }

    @Test
    void crear_errorDominio_retorna400() {
        when(useCase.ejecutar(any(Solicitud.class)))
                .thenReturn(Mono.error(new co.com.crediya.solicitudes.model.exceptions.DomainException("cliente_no_existe")));

        CrearSolicitudRequest req = new CrearSolicitudRequest(
                "alguien@example.com", new BigDecimal("1000"), 12, "LIBRE_INVERSION");

        webTestClient.post()
                .uri("/solicitud")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(req)
                .exchange()
                .expectStatus().is5xxServerError();
    }

    @Test
    void crear_bodyInvalido_retorna5xx() {
        CrearSolicitudRequest req = new CrearSolicitudRequest(
                "juan.perez@example.com", new BigDecimal("-1"), 0, " ");

        when(useCase.ejecutar(any(Solicitud.class)))
                .thenReturn(Mono.error(new co.com.crediya.solicitudes.model.exceptions.DomainException("monto_invalido")));

        webTestClient.post()
                .uri("/solicitud")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(req)
                .exchange()
                .expectStatus().is5xxServerError();
    }
}
