package co.com.crediya.solicitudes.r2dbc.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import lombok.Data;

import java.math.BigDecimal;
import java.time.Instant;

@Table("solicitud")
@Data
public class SolicitudEntity {
    @Id 
    @Column("id_solicitud")       
    private String id;
    @Column("email")                   private String email;
    @Column("monto")                   private BigDecimal monto;
    @Column("plazo")                   private Integer plazoMeses;
    @Column("id_estado")               private Integer idEstado;          // FK estados.id_estado
    @Column("id_tipo_prestamo")        private String tipoPrestamoId;       // FK tipo_prestamo.id_tipo_prestamo
    @Column("created")               private Instant created;
}