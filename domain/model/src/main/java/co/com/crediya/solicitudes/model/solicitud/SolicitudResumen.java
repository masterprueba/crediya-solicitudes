package co.com.crediya.solicitudes.model.solicitud;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@AllArgsConstructor
public class SolicitudResumen {

    private UUID id;
    private String documentoCliente;
    private BigDecimal monto;
    private Integer plazoMeses;
    private String tipoPrestamo;
    private BigDecimal tasaInteres;                   // % anual del tipo
    private Estado estado;
    private String nombreCompleto;
    private String email;
    private BigDecimal salarioBase;
    private BigDecimal deudaTotalMensualAprobadas;
}
