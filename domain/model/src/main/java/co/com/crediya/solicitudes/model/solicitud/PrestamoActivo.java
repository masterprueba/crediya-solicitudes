package co.com.crediya.solicitudes.model.solicitud;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class PrestamoActivo {
    private BigDecimal monto;
    private Integer plazoMeses;
    private Double tasaAnualPct;
}
