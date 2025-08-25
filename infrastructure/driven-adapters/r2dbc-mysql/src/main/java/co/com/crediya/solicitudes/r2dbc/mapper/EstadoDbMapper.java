package co.com.crediya.solicitudes.r2dbc.mapper;

import co.com.crediya.solicitudes.model.solicitud.Estado;

public final class EstadoDbMapper {
    public static final int ID_PENDIENTE = 1, ID_APROBADA = 2, ID_RECHAZADA = 3;
  
    public static int toDb(Estado e) {
      return switch (e) {
        case PENDIENTE_REVISION -> ID_PENDIENTE;
        case APROBADA           -> ID_APROBADA;
        case RECHAZADA          -> ID_RECHAZADA;
      };
    }
    public static Estado toDomain(int id) {
      return switch (id) {
        case ID_PENDIENTE -> Estado.PENDIENTE_REVISION;
        case ID_APROBADA  -> Estado.APROBADA;
        case ID_RECHAZADA -> Estado.RECHAZADA;
        default -> throw new IllegalArgumentException("estado_id_desconocido:"+id);
      };
    }
  }