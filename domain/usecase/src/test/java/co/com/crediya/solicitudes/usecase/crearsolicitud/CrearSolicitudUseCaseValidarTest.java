package co.com.crediya.solicitudes.usecase.crearsolicitud;

import co.com.crediya.solicitudes.model.exceptions.DomainException;
import co.com.crediya.solicitudes.model.solicitud.Estado;
import co.com.crediya.solicitudes.model.solicitud.Solicitud;
import co.com.crediya.solicitudes.model.solicitud.gateways.CatalogoPrestamoRepository;
import co.com.crediya.solicitudes.model.solicitud.gateways.SolicitudRepository;
import co.com.crediya.solicitudes.model.cliente.gateways.ClienteRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.lang.reflect.Method;
import java.math.BigDecimal;

@ExtendWith(MockitoExtension.class)
class CrearSolicitudUseCaseValidarTest {

    @Mock private SolicitudRepository repo;
    @Mock private ClienteRepository clientePort;
    @Mock private CatalogoPrestamoRepository catalogoPort;

    @InjectMocks private CrearSolicitudUseCase useCase;

    private Solicitud baseValida;
    private Method validarMethod;

    @BeforeEach
    void setUp() throws Exception {
        baseValida = Solicitud.builder()
                .email("test@example.com")
                .monto(new BigDecimal("1000"))
                .plazoMeses(12)
                .tipoPrestamo("LIBRE_INVERSION")
                .estado(Estado.PENDIENTE_REVISION)
                .build();

        validarMethod = CrearSolicitudUseCase.class.getDeclaredMethod("validar", Solicitud.class);
        validarMethod.setAccessible(true);
    }

    @SuppressWarnings("unchecked")
    private Mono<Solicitud> invokeValidar(Solicitud s) throws Exception {
        return (Mono<Solicitud>) validarMethod.invoke(useCase, s);
    }

    @Test
    void validar_ok() throws Exception {
        StepVerifier.create(invokeValidar(baseValida))
                .expectNextMatches(sol -> sol.getEmail().equals("test@example.com")
                        && sol.getMonto().compareTo(new BigDecimal("1000")) == 0
                        && sol.getPlazoMeses().equals(12)
                        && sol.getTipoPrestamo().equals("LIBRE_INVERSION"))
                .verifyComplete();
    }

    @Test
    void validar_monto_null() throws Exception {
        var invalida = baseValida.toBuilder().monto(null).build();
        StepVerifier.create(invokeValidar(invalida))
                .expectErrorMatches(ex -> ex instanceof DomainException && ex.getMessage().contains("monto_invalido"))
                .verify();
    }

    @Test
    void validar_monto_cero() throws Exception {
        var invalida = baseValida.toBuilder().monto(BigDecimal.ZERO).build();
        StepVerifier.create(invokeValidar(invalida))
                .expectErrorMatches(ex -> ex instanceof DomainException && ex.getMessage().contains("monto_invalido"))
                .verify();
    }

    @Test
    void validar_plazo_null() throws Exception {
        var invalida = baseValida.toBuilder().plazoMeses(null).build();
        StepVerifier.create(invokeValidar(invalida))
                .expectErrorMatches(ex -> ex instanceof DomainException && ex.getMessage().contains("plazo_invalido"))
                .verify();
    }

    @Test
    void validar_plazo_cero() throws Exception {
        var invalida = baseValida.toBuilder().plazoMeses(0).build();
        StepVerifier.create(invokeValidar(invalida))
                .expectErrorMatches(ex -> ex instanceof DomainException && ex.getMessage().contains("plazo_invalido"))
                .verify();
    }

    @Test
    void validar_tipo_null() throws Exception {
        var invalida = baseValida.toBuilder().tipoPrestamo(null).build();
        StepVerifier.create(invokeValidar(invalida))
                .expectErrorMatches(ex -> ex instanceof DomainException && ex.getMessage().contains("tipo_requerido"))
                .verify();
    }

    @Test
    void validar_tipo_blanco() throws Exception {
        var invalida = baseValida.toBuilder().tipoPrestamo(" ").build();
        StepVerifier.create(invokeValidar(invalida))
                .expectErrorMatches(ex -> ex instanceof DomainException && ex.getMessage().contains("tipo_requerido"))
                .verify();
    }
}


