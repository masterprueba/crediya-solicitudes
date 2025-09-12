package co.com.crediya.solicitudes.usecase.crearsolicitud;

import java.time.Instant;
import java.util.UUID;

import co.com.crediya.solicitudes.model.cliente.gateways.ClienteRepository;
import co.com.crediya.solicitudes.model.exceptions.DomainException;
import co.com.crediya.solicitudes.model.solicitud.Estado;
import co.com.crediya.solicitudes.model.solicitud.Solicitud;
import co.com.crediya.solicitudes.model.solicitud.gateways.CapacidadEndeudamientoRepository;
import co.com.crediya.solicitudes.model.solicitud.gateways.CatalogoPrestamoRepository;
import co.com.crediya.solicitudes.model.solicitud.gateways.SolicitudRepository;
import co.com.crediya.solicitudes.model.cliente.validation.ClienteValidations;
import co.com.crediya.solicitudes.model.solicitud.validation.SolicitudValidations;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;
import reactor.util.Logger;
import reactor.util.Loggers;

@RequiredArgsConstructor
public class CrearSolicitudUseCase {
    private static final Logger log = Loggers.getLogger(CrearSolicitudUseCase.class);
    private final SolicitudRepository repo;
  private final ClienteRepository clientePort;
  private final CatalogoPrestamoRepository catalogoPort;
  private final CapacidadEndeudamientoRepository capacidadEndeudamientoPort;

  public Mono<Solicitud> ejecutar(Solicitud soliciitud, String email) {
        log.info("2. Iniciando creación de solicitud para email: {} con tipo: {}", email, soliciitud.getTipoPrestamo());
        Mono<Solicitud> solicitudEnriquecida = SolicitudValidations.completa()
                .validar(soliciitud)
                .flatMap(soliciitudValida -> ClienteValidations.completa(soliciitudValida)
                        .validar(email)
                        .map(cliente -> soliciitudValida.toBuilder()
                                .email(email)
                                .build()))
                .flatMap(solicitudValidada -> clientePort.obtenerClientePorEmail(email)
                        .switchIfEmpty(Mono.error(new DomainException("cliente no existe o no esta autorizado")))
                        .map(cliente -> solicitudValidada.toBuilder()
                                .nombres(cliente.getUsuario())
                                .salarioBase(cliente.getSalarioBase())
                                .documentoIdentidad(cliente.getDocumentoIdentidad())
                                .nombres(cliente.getUsuario())
                                .build()))
                .flatMap(solicitudValidada -> catalogoPort.obtenerTipoPrestamoPorNombre(solicitudValidada.getTipoPrestamo())
                        .switchIfEmpty(Mono.error(new DomainException("tipo de préstamo no encontrado")))
                        .doOnNext(tipo -> log.info("Tipo de préstamo obtenido: {} - Validación automática: {}", 
                                tipo.getNombre(), tipo.isValidacionAutomatica()))
                        .map(tipo -> {
                                if (tipo.isValidacionAutomatica()) {
                                    log.info("Creando solicitud con validación automática");
                                    return solicitudValidada.toBuilder()
                                            .tipoPrestamoId(tipo.getId())
                                            .estado(Estado.EN_VALIDACION_AUTOMATICA)
                                            .tasaInteres(tipo.getTasaInteres())
                                            .created(Instant.now())
                                            .build();
                                }else {
                                    log.info("Creando solicitud para revisión manual id: {}", tipo.getId());
                                    return solicitudValidada.toBuilder()
                                            .tipoPrestamoId(tipo.getId())
                                            .estado(Estado.PENDIENTE_REVISION)
                                            .tasaInteres(tipo.getTasaInteres())
                                            .created(Instant.now())
                                            .build();
                                }
                        }));

        return solicitudEnriquecida
                .flatMap(solicitud -> {
                    log.info("Guardando solicitud {} en base de datos con tipo de prestamo id: {}", solicitud.getId(), solicitud.getTipoPrestamoId());
                    return repo.save(solicitud);
                })
                .doOnNext(solicitudGuardada -> log.info("Solicitud {} guardada exitosamente con estado: {}", 
                        solicitudGuardada.getId(), solicitudGuardada.getEstado()))
                .flatMap(solicitudGuardada -> {
                    if (Estado.EN_VALIDACION_AUTOMATICA.equals(solicitudGuardada.getEstado())) {
                        log.info("Obteniendo préstamos activos para {} antes de validación", solicitudGuardada.getEmail());
                        
                        // Obtener préstamos activos y agregar eventId
                        return repo.findPrestamosActivosByEmail(solicitudGuardada.getEmail())
                                .collectList()
                                .map(prestamosActivos -> {
                                    UUID eventId = UUID.randomUUID();
                                    log.info("Generado eventId {} para solicitud {}, encontrados {} préstamos activos", 
                                            eventId, solicitudGuardada.getId(), prestamosActivos.size());
                                    
                                    return solicitudGuardada.toBuilder()
                                            .eventId(eventId)
                                            .prestamosActivos(prestamosActivos)
                                            .build();
                                })
                                .flatMap(solicitudCompleta -> {
                                    log.info("Encolando solicitud {} para validación automática de capacidad de endeudamiento", 
                                            solicitudCompleta.getId());
                                    return capacidadEndeudamientoPort.validarCapacidadEndeudamiento(solicitudCompleta)
                                            .doOnSuccess(unused -> log.info("Solicitud {} encolada exitosamente para validación con eventId {}", 
                                                    solicitudCompleta.getId(), solicitudCompleta.getEventId()))
                                            .doOnError(error -> log.error("Error al encolar solicitud {} para validación: {}", 
                                                    solicitudCompleta.getId(), error.getMessage()))
                                            .thenReturn(solicitudCompleta);
                                });
                    }
                    log.info("Solicitud {} no requiere validación automática", solicitudGuardada.getId());
                    return Mono.just(solicitudGuardada);
                })
                .doOnSuccess(solicitudFinal -> log.info("Proceso de creación completado para solicitud {}", 
                        solicitudFinal.getId()))
                .onErrorMap(throwable -> {
                    log.error("Error durante la creación de solicitud para email {}: {}", email, throwable.getMessage());
                    if (throwable instanceof DomainException) {
                        return throwable;
                    }
                    return new DomainException("Error interno del sistema al procesar la solicitud");
                });
    }

}
