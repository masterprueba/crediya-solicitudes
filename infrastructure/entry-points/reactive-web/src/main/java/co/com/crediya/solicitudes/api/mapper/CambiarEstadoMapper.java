package co.com.crediya.solicitudes.api.mapper;

import co.com.crediya.solicitudes.api.dto.CambiarEstadoResponse;
import co.com.crediya.solicitudes.model.solicitud.Solicitud;
import org.springframework.stereotype.Component;

@Component
public class CambiarEstadoMapper {
    public CambiarEstadoResponse toResponse(Solicitud solicitud) {
        return new CambiarEstadoResponse(
                solicitud.getId() != null ? solicitud.getId().toString() : null,
                solicitud.getEstado() != null ? solicitud.getEstado().name() : null,
                solicitud.getMonto(),
                solicitud.getEmail()
        );
    }
}
