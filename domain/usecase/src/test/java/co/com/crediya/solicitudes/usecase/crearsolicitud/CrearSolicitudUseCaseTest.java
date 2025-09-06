package co.com.crediya.solicitudes.usecase.crearsolicitud;

import co.com.crediya.solicitudes.model.cliente.Cliente;
import co.com.crediya.solicitudes.model.cliente.gateways.ClienteRepository;
import co.com.crediya.solicitudes.model.solicitud.Estado;
import co.com.crediya.solicitudes.model.solicitud.Solicitud;
import co.com.crediya.solicitudes.model.solicitud.gateways.CatalogoPrestamoRepository;
import co.com.crediya.solicitudes.model.solicitud.gateways.SolicitudRepository;
import co.com.crediya.solicitudes.model.cliente.validation.ClienteValidation;
import co.com.crediya.solicitudes.model.cliente.validation.ClienteValidations;
import co.com.crediya.solicitudes.model.solicitud.validation.SolicitudValidation;
import co.com.crediya.solicitudes.model.solicitud.validation.SolicitudValidations;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CrearSolicitudUseCaseTest {

    private SolicitudRepository repo;
    private ClienteRepository clientePort;
    private CatalogoPrestamoRepository catalogoPort;
    private CrearSolicitudUseCase useCase;

    private Solicitud solicitud;

    @BeforeEach
    void setUp() {
        repo = mock(SolicitudRepository.class);
        clientePort = mock(ClienteRepository.class);
        catalogoPort = mock(CatalogoPrestamoRepository.class);
        useCase = new CrearSolicitudUseCase(repo, clientePort, catalogoPort);

        solicitud = Solicitud.builder()
                .monto(new BigDecimal("1000.0"))
                .documentoIdentidad("123456789")
                .email("test@test.com")
                .nombres("Test User")
                .estado(Estado.PENDIENTE_REVISION)
                .tipoPrestamo("PERSONAL")
                .plazoMeses(12)
                .build();
    }

    @Test
    @DisplayName("Debe ejecutar el caso de uso y retornar la solicitud enriquecida y guardada")
    void testEjecutar() {
        Solicitud solicitudValidada = solicitud.toBuilder().build();

        Solicitud solicitudConCliente = solicitudValidada.toBuilder()
                .email("test@test.com")
                .build();
        Solicitud solicitudConDatosCliente = solicitudConCliente.toBuilder()
                .nombres("Test User")
                .documentoIdentidad("123456789")
                .build();
        UUID tipoPrestamoId = UUID.fromString("11111111-1111-1111-1111-111111111111");
        Solicitud solicitudFinal = solicitudConDatosCliente.toBuilder()
                .id(UUID.randomUUID())
                .tipoPrestamoId(tipoPrestamoId)
                .estado(Estado.PENDIENTE_REVISION)
                .created(Instant.now())
                .build();

        // Mock estáticos para las validaciones
        try (MockedStatic<SolicitudValidations> solicitudValidationsMock = Mockito.mockStatic(SolicitudValidations.class);
             MockedStatic<ClienteValidations> clienteValidationsMock = Mockito.mockStatic(ClienteValidations.class)) {

            SolicitudValidation solicitudValidation = mock(SolicitudValidation.class);
            ClienteValidation clienteValidation = mock(ClienteValidation.class);

            solicitudValidationsMock.when(SolicitudValidations::completa).thenReturn(solicitudValidation);
            when(solicitudValidation.validar(any(Solicitud.class))).thenReturn(Mono.just(solicitudValidada));

            clienteValidationsMock.when(() -> ClienteValidations.completa(any(Solicitud.class))).thenReturn(clienteValidation);
            when(clienteValidation.validar(any(String.class))).thenReturn(Mono.just("test@test.com"));

            when(clientePort.obtenerClientePorEmail(any(String.class))).thenReturn(Mono.just(Cliente.builder().build()));
            when(catalogoPort.esTipoValido(anyString())).thenReturn(Mono.just(true));
            when(catalogoPort.obtenerIdPorNombre(anyString())).thenReturn(Mono.just(tipoPrestamoId));
            when(repo.save(any(Solicitud.class))).thenReturn(Mono.just(solicitudFinal));

            Mono<Solicitud> result = useCase.ejecutar(solicitud, "test@test.com");

            StepVerifier.create(result)
                    .assertNext(solicitudCreada -> {
                        // Verifica los datos enriquecidos
                        assert solicitudCreada.getEmail().equals("test@test.com");
                        assert solicitudCreada.getNombres().equals("Test User");
                        assert solicitudCreada.getTipoPrestamoId().equals(tipoPrestamoId);
                        assert solicitudCreada.getEstado() == Estado.PENDIENTE_REVISION;
                        assert solicitudCreada.getId() != null;
                        assert solicitudCreada.getCreated() != null;
                    })
                    .verifyComplete();
        }
    }
}