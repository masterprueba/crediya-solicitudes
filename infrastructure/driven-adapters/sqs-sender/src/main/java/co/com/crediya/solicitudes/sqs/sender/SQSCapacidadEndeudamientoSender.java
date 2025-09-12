package co.com.crediya.solicitudes.sqs.sender;

import co.com.crediya.solicitudes.model.solicitud.Solicitud;
import co.com.crediya.solicitudes.model.solicitud.gateways.CapacidadEndeudamientoRepository;
import co.com.crediya.solicitudes.sqs.sender.config.SQSSenderProperties;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import software.amazon.awssdk.services.sqs.SqsAsyncClient;
import java.util.Locale;

@Service
@Log4j2
public class SQSCapacidadEndeudamientoSender extends SQSBaseSender implements CapacidadEndeudamientoRepository {
    private final SQSSenderProperties properties;

    public SQSCapacidadEndeudamientoSender(SqsAsyncClient client, SQSSenderProperties properties) {
        super(client);
        this.properties = properties;
    }

    @Override
    public Mono<Void> validarCapacidadEndeudamiento(Solicitud solicitud) {
        String mensaje = convertirSolicitudACapacidadJson(solicitud);
        return send(mensaje, properties.capacidadEndeudamiento().url())
                .doOnSuccess(messageId -> log.info("Solicitud {} encolada para validación de capacidad de endeudamiento: {}", 
                        solicitud.getId(), messageId))
                .doOnError(error -> log.error("Error al encolar solicitud {} para validación de capacidad: {}", 
                        solicitud.getId(), error.getMessage()))
                .then();
    }

    private String convertirSolicitudACapacidadJson(Solicitud solicitud) {
        StringBuilder prestamosActivosJson = new StringBuilder("[");
        
        if (solicitud.getPrestamosActivos() != null && !solicitud.getPrestamosActivos().isEmpty()) {
            for (int i = 0; i < solicitud.getPrestamosActivos().size(); i++) {
                var prestamo = solicitud.getPrestamosActivos().get(i);
                if (i > 0) prestamosActivosJson.append(",");
                prestamosActivosJson.append(String.format(Locale.US,
                    "{\"monto\": %s, \"plazoMeses\": %d, \"tasaAnualPct\": %.2f}",
                    prestamo.getMonto(),
                    prestamo.getPlazoMeses(),
                    prestamo.getTasaAnualPct()
                ));
            }
        }
        prestamosActivosJson.append("]");
        
        return String.format(Locale.US,
                "{\"eventId\": \"%s\", \"solicitudId\": \"%s\", \"clienteId\": \"%s\", \"monto\": %s, \"plazoMeses\": %d, \"tipoPrestamoId\": \"%s\", \"email\": \"%s\", \"tasaInteres\": %.6f, \"salarioBase\": %s, \"nombres\": \"%s\", \"prestamosActivos\": %s}",
                solicitud.getEventId(),
                solicitud.getId(), 
                solicitud.getDocumentoIdentidad(),
                solicitud.getMonto(),
                solicitud.getPlazoMeses(),
                solicitud.getTipoPrestamoId(),
                solicitud.getEmail(),
                solicitud.getTasaInteres(),
                solicitud.getSalarioBase(),
                solicitud.getNombres(),
                prestamosActivosJson.toString()
        );
    }
}
