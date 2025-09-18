package co.com.crediya.solicitudes.sqs.sender.helper;

import co.com.crediya.solicitudes.model.solicitud.DecisionSolicitud;
import co.com.crediya.solicitudes.model.solicitud.Solicitud;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import reactor.util.Logger;
import reactor.util.Loggers;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public class CorreoUtils {

    private static final Logger log = Loggers.getLogger(CorreoUtils.class);

    public static String construirCuerpoManual(Solicitud solicitud) {
        return String.format(
                "<h2>Solicitud de crédito </h2>" +
                        "<p>Hola</p>" +
                        "<p>Tu solicitud de crédito ha sido: <strong>%s</strong>.</p>" +
                        "Nos pondremos en contacto contigo a la brevedad posible.</p>" +
                        "<p>Gracias por tu paciencia.</p>",
                solicitud.getEstado()
        );
    }

    public static String construirCorreo(String asunto, String cuerpo, Solicitud solicitud) {
        return String.format(
                "{\"to\": \"%s\", \"subject\": \"%s\", \"body\": \"%s\"}",
                solicitud.getEmail(),
                asunto,
                cuerpo.replace("\"", "\\\"").replace("\n", "").replace("\r", "")
        );
    }

    public static Mono<String> construirCuerpoAutomaticoAprobadoReactivo(DecisionSolicitud desicion, BigDecimal montoSolicitado) {
        return Mono.fromCallable(() -> {
            log.info("Plan de pagos {}", desicion.getPlanPago());
            String encabezado = String.format(
                    "<h2>Resultado de tu evaluación de crédito</h2>" +
                            "<p><strong>Hola </strong></p>" +
                            "<p>Decisión: <strong>%s</strong> Prestamo Aprobado: <strong>%s</strong></p>" +
                            "<ul>" +
                            "<li>Capacidad máxima (35%%): <b>%s</b></li>" +
                            "<li>Deuda mensual actual: <b>%s</b></li>" +
                            "<li>Capacidad disponible: <b>%s</b></li>" +
                            "<li>Cuota préstamo nuevo: <b>%s</b></li>" +
                            "</ul>",
                    desicion.getDecision(),
                    montoSolicitado != null ? String.format("%.2f", montoSolicitado) : "N/A",
                    desicion.getCapacidadMax() != null ? String.format("%.2f", desicion.getCapacidadMax()) : "N/A",
                    desicion.getDeudaMensualActual() != null ? String.format("%.2f", desicion.getDeudaMensualActual()) : "N/A",
                    desicion.getCapacidadDisponible() != null ? String.format("%.2f", desicion.getCapacidadDisponible()) : "N/A",
                    desicion.getCuotaPrestamoNuevo() != null ? String.format("%.2f", desicion.getCuotaPrestamoNuevo()) : "N/A"
            );

            StringBuilder filasTabla = new StringBuilder();
            ObjectMapper objectMapper = new ObjectMapper();

            try {
                Map<String, Object> planPagoMap = objectMapper.readValue(desicion.getPlanPago(), new TypeReference<Map<String, Object>>() {});
                List<Map<String, Object>> cuotas = (List<Map<String, Object>>) planPagoMap.get("cuotas");

                for (Map<String, Object> cuota : cuotas) {
                    filasTabla.append(String.format(
                            "<tr><td>%d</td><td>%.2f</td><td>%.2f</td><td>%.2f</td><td>%.2f</td></tr>",
                            cuota.get("numero"),
                            ((Number) cuota.get("cuota")).doubleValue(),
                            ((Number) cuota.get("interes")).doubleValue(),
                            ((Number) cuota.get("capital")).doubleValue(),
                            ((Number) cuota.get("saldo")).doubleValue()
                    ));
                }

                BigDecimal totalIntereses = new BigDecimal(planPagoMap.get("totalIntereses").toString());

                String tabla = String.format(
                        "<h3>Plan de pagos</h3>" +
                                "<table border=\"1\" cellpadding=\"6\" cellspacing=\"0\" style=\"border-collapse:collapse\">" +
                                "<thead>" +
                                "<tr><th>#</th><th>Cuota</th><th>Interés</th><th>Capital</th><th>Saldo</th></tr>" +
                                "</thead>" +
                                "<tbody>%s</tbody>" +
                                "</table>" +
                                "<p>Total de intereses: <b>%.2f</b></p>",
                        filasTabla.toString(),
                        totalIntereses
                );

                return encabezado + tabla +
                        "<p style=\"color:#6b7280;font-size:12px\">Este mensaje fue generado automáticamente. No respondas a este correo.</p>";

            } catch (Exception e) {
                log.error("Error al procesar el plan de pagos: {}", e.getMessage(), e);
                return encabezado + "<p>Error al generar el plan de pagos.</p>";
            }
        }).subscribeOn(Schedulers.boundedElastic()); // Ejecuta en un hilo no bloqueante
    }

    public static Mono<String> construirCuerpoAutomaticoRechazadaReactivo(DecisionSolicitud desicion) {
        return Mono.fromCallable(() -> String.format(
                "<h2>Resultado de tu evaluación de crédito</h2>" +
                        "<p><strong>Hola </strong></p>" +
                        "<p>Tu solicitud de crédito ha sido: <strong>%s</strong>.</p>" +
                        "<p>Lamentablemente, no cumples con los requisitos necesarios para aprobar el préstamo en este momento.</p>" +
                        "<p>Te invitamos a revisar tu información financiera y considerar aplicar nuevamente en el futuro.</p>" +
                        "<p>Gracias por tu interés en nuestros servicios.</p>" +
                        "<p style=\"color:#6b7280;font-size:12px\">Este mensaje fue generado automáticamente. No respondas a este correo.</p>",
                desicion.getDecision()
        )).subscribeOn(Schedulers.boundedElastic());
    }
}