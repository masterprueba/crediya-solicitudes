package co.com.crediya.solicitudes.sqs.listener.dto;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;
import com.fasterxml.jackson.databind.JsonNode;

public record CapacidadDecisionRequest(
  UUID eventId,
  UUID solicitudId,
  String decision,                  // "APROBADA" | "RECHAZADA" | "REVISION_MANUAL"
  BigDecimal capacidadMax,
  BigDecimal deudaMensualActual,
  BigDecimal capacidadDisponible,
  BigDecimal cuotaPrestamoNuevo,
  JsonNode planPago,
  Instant decidedAt,
  String correlationId
) {

}
