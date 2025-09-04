package co.com.crediya.solicitudes.r2dbc;

import co.com.crediya.solicitudes.model.solicitud.Estado;
import co.com.crediya.solicitudes.model.solicitud.SolicitudResumen;
import co.com.crediya.solicitudes.model.solicitud.gateways.SolicitudResumenRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class SolicitudResumenAdapter implements SolicitudResumenRepository {

    private final SolicitudResumenR2dbcRepository repository;
    private final DatabaseClient db;

    private static final Logger log = LoggerFactory.getLogger(SolicitudResumenAdapter.class);

    @Override
    public Flux<SolicitudResumen> listarBase(Set<Estado> estados, int page, int size, String filtroTipo) {

        log.info("Listando solicitudes con estados: {}, página: {}, tamaño: {}, filtroTipo: {}", estados, page, size, filtroTipo);

        String estadosIn = "('RECHAZADA','PENDIENTE_REVISION','REVISION_MANUAL')";

        if (estados != null || !estados.isEmpty()) {
            estadosIn =  "("+estados.stream().map(estado -> "'" + estado.name() + "'").reduce((a, b) -> a + "," + b).orElse("")+")";
        }

        log.debug("Estados para consulta SQL: {}", estadosIn);
        StringBuilder query = new StringBuilder("SELECT s.id_solicitud, s.monto, s.plazo, tp.nombre AS tipo_prestamo, tp.tasa_interes, es.nombre AS estado, s.email , dm.deuda_mensual " +
                "FROM solicitud s " +
                "JOIN tipo_prestamo tp ON s.id_tipo_prestamo = tp.id_tipo_prestamo " +
                "JOIN estados es ON es.id_estado = s.id_estado " +
                "LEFT JOIN (\n" +
                "     SELECT ss.email,\n" +
                "            SUM( (ss.monto * ((tp2.tasa_interes/100)/12) * POW(1 + ((tp2.tasa_interes/100)/12), ss.plazo))\n" +
                "                        / (POW(1 + ((tp2.tasa_interes/100)/12), ss.plazo) - 1)\n" +
                "            ) AS deuda_mensual\n" +
                "     FROM solicitud ss\n" +
                "     JOIN tipo_prestamo tp2 ON tp2.id_tipo_prestamo = ss.id_tipo_prestamo\n" +
                "       JOIN estados es2 ON es2.id_estado = ss.id_estado\n" +
                "     WHERE es2.nombre = 'APROBADA'\n" +
                "     GROUP BY ss.email\n" +
                "  ) dm ON dm.email = s.email "+
                "WHERE es.nombre IN " + estadosIn + " ");

        log.debug("Consulta SQL base: {}", query.toString());

        if (filtroTipo != null && !filtroTipo.isEmpty()) {
            query.append(" AND tp.nombre = ?");
        }
        query.append(" LIMIT ? OFFSET ? ");

        log.info("Consulta SQL final con filtros: {}", query.toString());

        var data = db.sql(query.toString());
        int paramIndex = 0;
        if (filtroTipo != null && !filtroTipo.isEmpty()) {
            log.info("1 paramindex {}", paramIndex);
            data = data.bind(paramIndex++, filtroTipo);

        }
        log.info("2 paramindex {}", paramIndex);
        data = data.bind(paramIndex++, size);
        log.info("3 paramindex {}", paramIndex);
        data = data.bind(paramIndex, (page-1) * size);

        return data.map((row, meta) -> new SolicitudResumen(
                UUID.fromString(Objects.requireNonNull(row.get("id_solicitud", String.class))),
                null,
                row.get("monto", BigDecimal.class),
                row.get("plazo", Integer.class),
                row.get("tipo_prestamo", String.class),
                row.get("tasa_interes", BigDecimal.class),
                Estado.valueOf(row.get("estado", String.class)),
                null,
                row.get("email", String.class),
               null,
                BigDecimal.ZERO
        )).all();
    }

    @Override
    public Mono<Long> contar(Set<Estado> estados, String filtroTipo) {
        String inEstados = "('PENDIENTE_REVISION','RECHAZADA','REVISION_MANUAL')";
        if (estados != null && !estados.isEmpty()) {
            inEstados = "(" + estados.stream().map(e -> "'" + e.name() + "'").reduce((a,b)->a+","+b).orElse("") + ")";
        }
        var sb = new StringBuilder("""
      SELECT COUNT(*) AS total
      FROM solicitud s
      JOIN tipo_prestamo tp ON tp.id_tipo_prestamo = s.id_tipo_prestamo
      JOIN estados es ON es.id_estado = s.id_estado 
      WHERE es.nombre IN %s
    """.formatted(inEstados));
        if (filtroTipo != null && !filtroTipo.isBlank()) {
            sb.append(" AND tp.nombre = :tipo ");
        }
        var spec = db.sql(sb.toString());
        if (filtroTipo != null && !filtroTipo.isBlank()) {
            spec = spec.bind("tipo", filtroTipo);
        }
        return spec.map((row, meta) -> row.get("total", Long.class)).one();
    }
}
