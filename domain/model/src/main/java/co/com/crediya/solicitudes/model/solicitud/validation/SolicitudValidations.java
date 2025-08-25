package co.com.crediya.solicitudes.model.solicitud.validation;

import java.math.BigDecimal;

import co.com.crediya.solicitudes.model.exceptions.DomainException;
import reactor.core.publisher.Mono;

public class SolicitudValidations {

    public static SolicitudValidation validarMonto() {
        return solicitud -> {
            if (solicitud.getMonto() == null || solicitud.getMonto().compareTo(BigDecimal.ZERO) <= 0) {
                return Mono.error(new DomainException("monto_invalido"));
            }
            return Mono.just(solicitud);
        };
    }

    public static SolicitudValidation validarPlazo() {
        return solicitud -> {
            if (solicitud.getPlazoMeses() == null || solicitud.getPlazoMeses() <= 0) {
                return Mono.error(new DomainException("plazo_invalido"));
            }
            return Mono.just(solicitud);
        };
    }

    public static SolicitudValidation validarTipoPrestamo() {
        return solicitud -> {
            if (solicitud.getTipoPrestamo() == null || solicitud.getTipoPrestamo().isBlank()) {
                return Mono.error(new DomainException("tipo_prestamo_invalido"));
            }
            return Mono.just(solicitud);
        };
    }

    public static SolicitudValidation completa() {
        return validarMonto().and(validarPlazo()).and(validarTipoPrestamo());
    }

}
