package co.com.crediya.solicitudes.model.solicitud.validation;

import co.com.crediya.solicitudes.model.exceptions.DomainException;
import co.com.crediya.solicitudes.model.solicitud.Estado;
import co.com.crediya.solicitudes.model.solicitud.Solicitud;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.stream.Stream;

public class CambiarEstadoSolicitudValidations {

    private CambiarEstadoSolicitudValidations() {
        throw new DomainException("CambiarEstado class");
    }

    public static CambiarEstadoSolicitudValidation validarEstado() {
        return (solicitud, nuevoEstado) -> {
            Estado estado  =Arrays.stream(Estado.values()).filter(e -> e.name().equals(nuevoEstado)).findFirst()
                    .orElse(null);
            if(estado == null) {
                return Mono.error(new DomainException("El esttado es inválido"));
            }
            return Mono.just(estado.name());
        };
    }

    public static CambiarEstadoSolicitudValidation validarYaEstaAprobadaoRechazada() {
        return (solicitud, nuevoEstado) -> {
           if(nuevoEstado.equals("APROBADA") && solicitud.getEstado().name().equals("APROBADA")) {
               return Mono.error(new DomainException("La solicitud ya se encuentra aprobada"));
           }
           if(nuevoEstado.equals("RECHAZADA") && solicitud.getEstado().name().equals("RECHAZADA")) {
               return Mono.error(new DomainException("La solicitud ya se encuentra rechazada"));
           }
            return Mono.just(nuevoEstado);
        };
    }

    public static CambiarEstadoSolicitudValidation completa() {
        return validarEstado().and(validarYaEstaAprobadaoRechazada());
    }
}
