package co.com.crediya.solicitudes.model.solicitud.validation;

import co.com.crediya.solicitudes.model.exceptions.DomainException;
import co.com.crediya.solicitudes.model.solicitud.Estado;
import reactor.core.publisher.Mono;

import java.util.Arrays;

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

    public static CambiarEstadoSolicitudValidation completa() {
        return validarEstado();
    }
}
