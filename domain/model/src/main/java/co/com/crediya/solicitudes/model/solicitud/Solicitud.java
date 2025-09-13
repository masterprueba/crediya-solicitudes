package co.com.crediya.solicitudes.model.solicitud;
import lombok.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
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
    private String tipoPrestamo;
    private Estado estado;
    private Instant created;
    private Double tasaInteres;
    private BigDecimal salarioBase;
    private UUID eventId;
    private List<PrestamoActivo> prestamosActivos;
}
