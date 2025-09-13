package co.com.crediya.solicitudes.model.solicitud;

import lombok.AllArgsConstructor;
import lombok.Builder;
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
@Builder
public class DecisionSolicitud {
    private UUID eventId;
    private UUID solicitudId;
    private Estado decision;
    private BigDecimal capacidadMax;
    private BigDecimal deudaMensualActual;
    private BigDecimal capacidadDisponible;
    private BigDecimal cuotaPrestamoNuevo;
    private String planPago;
    private Instant decidedAt;
}


