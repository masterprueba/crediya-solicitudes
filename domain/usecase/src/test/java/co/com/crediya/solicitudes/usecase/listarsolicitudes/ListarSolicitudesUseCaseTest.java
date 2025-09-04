package co.com.crediya.solicitudes.usecase.listarsolicitudes;

import co.com.crediya.solicitudes.model.cliente.ClienteToken;
import co.com.crediya.solicitudes.model.cliente.Cliente;
import co.com.crediya.solicitudes.model.cliente.gateways.ClienteRepository;
import co.com.crediya.solicitudes.model.solicitud.Estado;
import co.com.crediya.solicitudes.model.solicitud.SolicitudResumen;
import co.com.crediya.solicitudes.model.solicitud.gateways.SolicitudResumenRepository;
import co.com.crediya.solicitudes.model.util.Pagina;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;
import java.util.Set;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;

class ListarSolicitudesUseCaseTest {

    SolicitudResumenRepository solicitudResumenRepository;
    ClienteRepository clienteRepository;
    ListarSolicitudesUseCase useCase;

    @BeforeEach
    void setUp() {
        solicitudResumenRepository = Mockito.mock(SolicitudResumenRepository.class);
        clienteRepository = Mockito.mock(ClienteRepository.class);
        useCase = new ListarSolicitudesUseCase(solicitudResumenRepository, clienteRepository);
    }

    @Test
    void listarSolicitudes_retornaPaginaCorrecta() {
        var estados = Set.of(Estado.PENDIENTE_REVISION, Estado.RECHAZADA, Estado.REVISION_MANUAL);
        var base = new SolicitudResumen(
                UUID.randomUUID(), "123", BigDecimal.TEN, 12, "PERSONAL", BigDecimal.ONE,
                Estado.PENDIENTE_REVISION, "usuario", "email@test.com", BigDecimal.valueOf(1000), BigDecimal.ZERO
        );
        var cliente = Cliente.builder()
                .usuario("usuario")
                .email("email@test.com")
                .salarioBase(BigDecimal.valueOf(1000))
                .documentoIdentidad("123")
                .build();

        Mockito.when(solicitudResumenRepository.listarBase(eq(estados), eq(0), eq(10), eq("PERSONAL")))
                .thenReturn(Flux.just(base));
        Mockito.when(clienteRepository.obtenerClientePorEmail(any(ClienteToken.class)))
                .thenReturn(Mono.just(cliente));
        Mockito.when(solicitudResumenRepository.contar(eq(estados), eq("PERSONAL")))
                .thenReturn(Mono.just(1L));

        var clienteToken = ClienteToken.builder().email("email@test.com").token("token").build();

        Mono<Pagina<SolicitudResumen>> result = useCase.listarSolicitudes(0, 10, "PERSONAL", clienteToken);

        StepVerifier.create(result)
                .expectNextMatches(pagina ->
                        pagina.contenido().size() == 1 &&
                                pagina.page() == 0 &&
                                pagina.size() == 10 &&
                                pagina.totalElements() == 1L &&
                                pagina.totalPages() == 1 &&
                                !pagina.hasNext()
                )
                .verifyComplete();
    }
}