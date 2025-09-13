package co.com.crediya.solicitudes.usecase.crearsolicitud;

import co.com.crediya.solicitudes.model.cliente.Cliente;
import co.com.crediya.solicitudes.model.cliente.gateways.ClienteRepository;
import co.com.crediya.solicitudes.model.exceptions.DomainException;
import co.com.crediya.solicitudes.model.solicitud.PrestamoActivo;
import co.com.crediya.solicitudes.model.solicitud.Solicitud;
import co.com.crediya.solicitudes.model.solicitud.TipoPrestamo;
import co.com.crediya.solicitudes.model.solicitud.gateways.CapacidadEndeudamientoRepository;
import co.com.crediya.solicitudes.model.solicitud.gateways.CatalogoPrestamoRepository;
import co.com.crediya.solicitudes.model.solicitud.gateways.SolicitudRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@DisplayName("CrearSolicitudUseCase Test")
class CrearSolicitudUseCaseTest {

    @Mock
    private SolicitudRepository solicitudRepository;
    @Mock
    private ClienteRepository clienteRepository;
    @Mock
    private CatalogoPrestamoRepository catalogoPrestamoRepository;
    @Mock
    private CapacidadEndeudamientoRepository capacidadEndeudamientoRepository;

    @InjectMocks
    private CrearSolicitudUseCase crearSolicitudUseCase;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("Crear solicitud con validación automática - Exitoso")
    void crearSolicitudConValidacionAutomatica() {
        Solicitud solicitud = Solicitud.builder()
                .monto(BigDecimal.valueOf(5000))
                .plazoMeses(12)
                .email("test@test.com")
                .tipoPrestamo("LIBRE_INVERSION")
                .build();
        String email = "test@test.com";

        Cliente cliente = Cliente.builder().email("test@test.com").usuario("Test User").salarioBase(BigDecimal.valueOf(10000)).documentoIdentidad("12345").build();
        TipoPrestamo tipoPrestamo = TipoPrestamo.builder().nombre("LIBRE_INVERSION").validacionAutomatica(true).tasaInteres(18.5).build();
        Solicitud savedSolicitud = solicitud.toBuilder().email("test@test.com").build();

        when(clienteRepository.obtenerClientePorEmail(email)).thenReturn(Mono.just(cliente));
        when(catalogoPrestamoRepository.obtenerTipoPrestamoPorNombre(anyString())).thenReturn(Mono.just(tipoPrestamo));
        when(solicitudRepository.save(any(Solicitud.class))).thenReturn(Mono.just(savedSolicitud));
        when(solicitudRepository.findPrestamosActivosByEmail(anyString())).thenReturn(Flux.empty());
        when(capacidadEndeudamientoRepository.validarCapacidadEndeudamiento(any(Solicitud.class))).thenReturn(Mono.empty());

        StepVerifier.create(crearSolicitudUseCase.ejecutar(solicitud, email))
                .expectNextMatches(s -> "test@test.com".equals(s.getEmail()))
                .verifyComplete();
    }

    @Test
    @DisplayName("Crear solicitud para revisión manual - Exitoso")
    void crearSolicitudParaRevisionManual() {
        Solicitud solicitud = Solicitud.builder()
                .monto(BigDecimal.valueOf(5000))
                .plazoMeses(12)
                .email("test@test.com")
                .tipoPrestamo("LIBRANZA")
                .build();
        String email = "test@test.com";

        Cliente cliente = Cliente.builder().usuario("Test User").salarioBase(BigDecimal.valueOf(10000)).documentoIdentidad("12345").build();
        TipoPrestamo tipoPrestamo = TipoPrestamo.builder().nombre("LIBRANZA").validacionAutomatica(false).tasaInteres(18.5).build();
        Solicitud savedSolicitud = solicitud.toBuilder().email("test@test.com").build();

        when(clienteRepository.obtenerClientePorEmail(anyString())).thenReturn(Mono.just(cliente));
        when(catalogoPrestamoRepository.obtenerTipoPrestamoPorNombre(anyString())).thenReturn(Mono.just(tipoPrestamo));
        when(solicitudRepository.save(any(Solicitud.class))).thenReturn(Mono.just(savedSolicitud));

        StepVerifier.create(crearSolicitudUseCase.ejecutar(solicitud, email))
                .expectNextMatches(s -> "test@test.com".equals(s.getEmail()))
                .verifyComplete();
    }

    @Test
    @DisplayName("Crear solicitud - Falla por cliente no encontrado")
    void crearSolicitudFallaClienteNoEncontrado() {
        Solicitud solicitud = Solicitud.builder().monto(BigDecimal.valueOf(5000)).plazoMeses(12).tipoPrestamo("LIBRE_INVERSION").build();
        String email = "test@test.com";

        when(clienteRepository.obtenerClientePorEmail(anyString())).thenReturn(Mono.empty());

        StepVerifier.create(crearSolicitudUseCase.ejecutar(solicitud, email))
                .expectErrorMatches(throwable -> throwable instanceof DomainException && "cliente_no_autorizado_para_esta_solicitud".equals(throwable.getMessage()))
                .verify();
    }

    @Test
    @DisplayName("Crear solicitud - Falla por tipo de préstamo no encontrado")
    void crearSolicitudFallaTipoPrestamoNoEncontrado() {
        Solicitud solicitud = Solicitud.builder().email("test@test.com").monto(BigDecimal.valueOf(5000)).plazoMeses(12).tipoPrestamo("NO_EXISTE").build();
        String email = "test@test.com";

        Cliente cliente = Cliente.builder().usuario("Test User").salarioBase(BigDecimal.valueOf(10000)).documentoIdentidad("12345").build();

        when(clienteRepository.obtenerClientePorEmail(anyString())).thenReturn(Mono.just(cliente));
        when(catalogoPrestamoRepository.obtenerTipoPrestamoPorNombre(anyString())).thenReturn(Mono.empty());

        StepVerifier.create(crearSolicitudUseCase.ejecutar(solicitud, email))
                .expectErrorMatches(throwable -> throwable instanceof DomainException && "tipo de préstamo no encontrado".equals(throwable.getMessage()))
                .verify();
    }

    @Test
    @DisplayName("Crear solicitud - Error al encolar para validación")
    void crearSolicitudErrorAlEncolar() {
        Solicitud solicitud = Solicitud.builder().email("test@test.com").monto(BigDecimal.valueOf(5000)).plazoMeses(12).tipoPrestamo("LIBRE_INVERSION").build();
        String email = "test@test.com";

        Cliente cliente = Cliente.builder().email(email).usuario("Test User").salarioBase(BigDecimal.valueOf(10000)).documentoIdentidad("12345").build();
        TipoPrestamo tipoPrestamo = TipoPrestamo.builder().nombre("LIBRE_INVERSION").validacionAutomatica(true).tasaInteres(18.5).build();
        Solicitud savedSolicitud = solicitud.toBuilder()
                .email("test@test.com")
                .estado(co.com.crediya.solicitudes.model.solicitud.Estado.EN_VALIDACION_AUTOMATICA)
                .build();

        when(clienteRepository.obtenerClientePorEmail(anyString())).thenReturn(Mono.just(cliente));
        when(catalogoPrestamoRepository.obtenerTipoPrestamoPorNombre(anyString())).thenReturn(Mono.just(tipoPrestamo));
        when(solicitudRepository.save(any(Solicitud.class))).thenReturn(Mono.just(savedSolicitud));
        when(solicitudRepository.findPrestamosActivosByEmail(anyString())).thenReturn(Flux.empty());
        when(capacidadEndeudamientoRepository.validarCapacidadEndeudamiento(any(Solicitud.class))).thenReturn(Mono.error(new RuntimeException("Error de encolamiento")));

        StepVerifier.create(crearSolicitudUseCase.ejecutar(solicitud, email))
                .expectErrorMatches(throwable -> throwable instanceof DomainException && "Error interno del sistema al procesar la solicitud".equals(throwable.getMessage()))
                .verify();
    }

    @Test
    @DisplayName("Crear solicitud con prestamos activos")
    void crearSolicitudConPrestamosActivos() {
        Solicitud solicitud = Solicitud.builder().email("test@test.com").monto(BigDecimal.valueOf(5000)).plazoMeses(12).tipoPrestamo("LIBRE_INVERSION").build();
        String email = "test@test.com";
        PrestamoActivo prestamoActivo = PrestamoActivo.builder().build();

        Cliente cliente = Cliente.builder().usuario("Test User").salarioBase(BigDecimal.valueOf(10000)).documentoIdentidad("12345").build();
        TipoPrestamo tipoPrestamo = TipoPrestamo.builder().nombre("LIBRE_INVERSION").validacionAutomatica(true).tasaInteres(18.5).build();
        Solicitud savedSolicitud = solicitud.toBuilder().email("test@test.com").build();

        when(clienteRepository.obtenerClientePorEmail(anyString())).thenReturn(Mono.just(cliente));
        when(catalogoPrestamoRepository.obtenerTipoPrestamoPorNombre(anyString())).thenReturn(Mono.just(tipoPrestamo));
        when(solicitudRepository.save(any(Solicitud.class))).thenReturn(Mono.just(savedSolicitud));
        when(solicitudRepository.findPrestamosActivosByEmail(anyString())).thenReturn(Flux.just(prestamoActivo));
        when(capacidadEndeudamientoRepository.validarCapacidadEndeudamiento(any(Solicitud.class))).thenReturn(Mono.empty());

        StepVerifier.create(crearSolicitudUseCase.ejecutar(solicitud, email))
                .expectNextMatches(s -> "test@test.com".equals(s.getEmail()))
                .verifyComplete();
    }
}