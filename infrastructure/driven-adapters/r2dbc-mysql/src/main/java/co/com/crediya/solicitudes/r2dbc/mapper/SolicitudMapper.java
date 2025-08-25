package co.com.crediya.solicitudes.r2dbc.mapper;

import co.com.crediya.solicitudes.model.solicitud.Estado;
import co.com.crediya.solicitudes.model.solicitud.Solicitud;
import co.com.crediya.solicitudes.r2dbc.entity.SolicitudEntity;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class SolicitudMapper {
    
    public SolicitudEntity toEntity(Solicitud solicitud) {
        if (solicitud == null) {
            return null;
        }
        
        SolicitudEntity entity = new SolicitudEntity();
        entity.setId(solicitud.getId() != null ? solicitud.getId().toString() : null);
        entity.setEmail(solicitud.getEmail());
        entity.setMonto(solicitud.getMonto());
        entity.setPlazoMeses(solicitud.getPlazoMeses());
        entity.setTipoPrestamoId(solicitud.getTipoPrestamoId() != null ? solicitud.getTipoPrestamoId().toString() : null);
        entity.setIdEstado(estadoToInteger(solicitud.getEstado()));
        entity.setCreated(solicitud.getCreated());
        
        return entity;
    }
    
    public Solicitud toDomain(SolicitudEntity entity) {
        if (entity == null) {
            return null;
        }
        
        return Solicitud.builder()
                .id(entity.getId() != null ? UUID.fromString(entity.getId()) : null)
                .email(entity.getEmail())
                .monto(entity.getMonto())
                .plazoMeses(entity.getPlazoMeses())
                .tipoPrestamoId(entity.getTipoPrestamoId() != null ? UUID.fromString(entity.getTipoPrestamoId()) : null)
                .estado(integerToEstado(entity.getIdEstado()))
                .created(entity.getCreated())
                .build();
    }
    
    private Integer estadoToInteger(Estado estado) {
        if (estado == null) {
            return EstadoDbMapper.ID_PENDIENTE; // valor por defecto
        }
        return EstadoDbMapper.toDb(estado);
    }
    
    private Estado integerToEstado(Integer idEstado) {
        if (idEstado == null) {
            return Estado.PENDIENTE_REVISION; // valor por defecto
        }
        return EstadoDbMapper.toDomain(idEstado);
    }
}