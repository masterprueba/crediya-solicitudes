package co.com.crediya.solicitudes.api;

import co.com.crediya.solicitudes.api.dto.CrearSolicitudRequest;
import co.com.crediya.solicitudes.model.solicitud.Estado;
import co.com.crediya.solicitudes.model.solicitud.Solicitud;
import co.com.crediya.solicitudes.usecase.crearsolicitud.CrearSolicitudUseCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

class RouterRestUnitTest {

    @Mock
    private CrearSolicitudUseCase useCase;

    private WebTestClient client;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        var handler = new CrearSolicitudHandler(useCase);
        var router = new RouterRest().routerFunction(handler);
        client = WebTestClient.bindToRouterFunction(router).build();
    }

    @Test
    void post_solicitud_crea201() {
        var salida = Solicitud.builder()
                .id(UUID.randomUUID())
                .email("juan.perez@example.com")
                .monto(new BigDecimal("5000000"))
                .plazoMeses(24)
                .tipoPrestamo("LIBRE_INVERSION")
                .tipoPrestamoId(UUID.randomUUID())
                .estado(Estado.PENDIENTE_REVISION)
                .created(Instant.now())
                .build();
        when(useCase.ejecutar(any(Solicitud.class))).thenReturn(Mono.just(salida));

        var req = new CrearSolicitudRequest("juan.perez@example.com", new BigDecimal("5000000"), 24, "LIBRE_INVERSION");
        client.post().uri("/solicitud")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(req)
                .exchange()
                .expectStatus().isCreated()
                .expectBody()
                .jsonPath("$.id").isEqualTo(salida.getId().toString());
    }
}
