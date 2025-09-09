package co.com.crediya.solicitudes.model.solicitud.validation;

import co.com.crediya.solicitudes.model.exceptions.DomainException;
import reactor.core.publisher.Mono;

public class CambiarEstadoSolicitudValidations {

    private CambiarEstadoSolicitudValidations() {
        throw new DomainException("CambiarEstado class");
    }

    public static CambiarEstadoSolicitudValidation validarEstadoAprobadooRechazado() {
        return (solicitud, nuevoEstado) -> {
           if( nuevoEstado.equals("APROBADA") || nuevoEstado.equals("RECHAZADA")) {
               return Mono.just(nuevoEstado);
            }
            return Mono.error(new DomainException("estado_invalido"));
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
        return validarEstadoAprobadooRechazado().and(validarYaEstaAprobadaoRechazada());
    }
}
