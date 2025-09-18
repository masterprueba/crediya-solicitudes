package co.com.crediya.solicitudes.r2dbc.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import lombok.Data;

@Table("tipo_prestamo")
@Data
public class TipoPrestamoEntity {
    @Id
    @Column("id_tipo_prestamo") private String id;
    @Column("nombre")               private String nombre;
    @Column("tasa_interes")         private Double tasaInteres;
    @Column("monto_minimo")         private Double montoMinimo;
    @Column("monto_maximo")         private Double montoMaximo;
    @Column("validacion_automatica") private Boolean validacionAutomatica;
}
