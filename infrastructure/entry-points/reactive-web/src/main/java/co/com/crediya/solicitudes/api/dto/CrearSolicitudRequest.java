package co.com.crediya.solicitudes.api.dto;

import java.math.BigDecimal;

public record CrearSolicitudRequest(
String email,
BigDecimal monto,
Integer plazo_meses,
String tipo_prestamo) {

}
