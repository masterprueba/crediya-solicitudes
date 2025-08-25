package co.com.crediya.solicitudes.usecase.crearsolicitud;

import co.com.crediya.solicitudes.model.cliente.Cliente;
import co.com.crediya.solicitudes.model.cliente.gateways.ClienteRepository;
import co.com.crediya.solicitudes.model.exceptions.DomainException;
import co.com.crediya.solicitudes.model.solicitud.Estado;
import co.com.crediya.solicitudes.model.solicitud.Solicitud;
import co.com.crediya.solicitudes.model.solicitud.gateways.CatalogoPrestamoRepository;
import co.com.crediya.solicitudes.model.solicitud.gateways.SolicitudRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CrearSolicitudUseCaseTest {

    @Mock
    private SolicitudRepository solicitudRepository;
    @Mock
    private ClienteRepository clienteRepository;
    @Mock
    private CatalogoPrestamoRepository catalogoPrestamoRepository;

    @InjectMocks
    private CrearSolicitudUseCase useCase;

    private Solicitud solicitudBase;

    @BeforeEach
    void setUp() {
        solicitudBase = Solicitud.builder()
                .email("juan.perez@example.com")
                .monto(new BigDecimal("5000000"))
                .plazoMeses(24)
                .tipoPrestamo("LIBRE_INVERSION")
                .estado(Estado.PENDIENTE_REVISION)
                .build();
    }

    @Test
    void ejecutar_debeCrearSolicitud_ok() {
        var cliente = Cliente.builder()
                .usuario("Juan Perez")
                .email("juan.perez@example.com")
                .documento_identidad("123456789")
                .build();

        when(clienteRepository.obtenerClientePorEmail(solicitudBase.getEmail()))
                .thenReturn(Mono.just(cliente));
        when(catalogoPrestamoRepository.esTipoValido("LIBRE_INVERSION"))
                .thenReturn(Mono.just(true));
        when(catalogoPrestamoRepository.obtenerIdPorNombre("LIBRE_INVERSION"))
                .thenReturn(Mono.just(UUID.randomUUID()));
        when(solicitudRepository.save(any(Solicitud.class)))
                .thenAnswer(inv -> Mono.just((Solicitud) inv.getArgument(0)));

        StepVerifier.create(useCase.ejecutar(solicitudBase))
                .assertNext(sol -> {
                    // Enriquecida con datos del cliente y tipoPrestamoId asignado
                    assert sol.getNombres().equals("Juan Perez");
                    assert sol.getDocumento_identidad().equals("123456789");
                    assert sol.getId() != null;
                    assert sol.getTipoPrestamoId() != null;
                })
                .verifyComplete();
    }

    @Test
    void ejecutar_clienteNoExiste_debeFallar() {
        when(clienteRepository.obtenerClientePorEmail(solicitudBase.getEmail()))
                .thenReturn(Mono.empty());

        StepVerifier.create(useCase.ejecutar(solicitudBase))
                .expectErrorMatches(ex -> ex instanceof DomainException &&
                        ex.getMessage().contains("cliente_no_existe"))
                .verify();
    }

    @Test
    void ejecutar_tipoPrestamoInvalido_debeFallar() {
        var cliente = Cliente.builder()
                .usuario("Juan Perez")
                .email("juan.perez@example.com")
                .documento_identidad("123456789")
                .build();
        when(clienteRepository.obtenerClientePorEmail(solicitudBase.getEmail()))
                .thenReturn(Mono.just(cliente));
        when(catalogoPrestamoRepository.esTipoValido("LIBRE_INVERSION"))
                .thenReturn(Mono.just(false));

        StepVerifier.create(useCase.ejecutar(solicitudBase))
                .expectErrorMatches(ex -> ex instanceof DomainException &&
                        ex.getMessage().contains("tipo_prestamo_invalido"))
                .verify();
    }

    @Test
    void ejecutar_validacionesMontoPlazoTipo_debeFallar() {
        var invalida = solicitudBase.toBuilder()
                .monto(BigDecimal.ZERO)
                .build();
        StepVerifier.create(useCase.ejecutar(invalida))
                .expectErrorMatches(ex -> ex instanceof DomainException && ex.getMessage().contains("monto_invalido"))
                .verify();

        invalida = solicitudBase.toBuilder().monto(new BigDecimal("1")).plazoMeses(0).build();
        StepVerifier.create(useCase.ejecutar(invalida))
                .expectErrorMatches(ex -> ex instanceof DomainException && ex.getMessage().contains("plazo_invalido"))
                .verify();

        invalida = solicitudBase.toBuilder().plazoMeses(12).tipoPrestamo(" ").build();
        StepVerifier.create(useCase.ejecutar(invalida))
                .expectErrorMatches(ex -> ex instanceof DomainException && ex.getMessage().contains("tipo_prestamo_invalido"))
                .verify();
    }
}
