package co.com.crediya.solicitudes.model.solicitud;
import lombok.Builder;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class Solicitud {
    private UUID id;
    private String email;
    private String nombres;
    private String documentoIdentidad;
    private BigDecimal monto;
    private Integer plazoMeses;
    private UUID tipoPrestamoId;
    private String tipoPrestamo; // Campo temporal para compatibilidad
    private Estado estado;
    private Instant created;
}
