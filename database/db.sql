CREATE TABLE crediya.estados (
  id_estado TINYINT UNSIGNED PRIMARY KEY,
  nombre VARCHAR(50) NOT NULL UNIQUE
) ENGINE=InnoDB;

INSERT INTO crediya.estados (id_estado, nombre) VALUES
  (1,'PENDIENTE_REVISION'), (2,'APROBADA'), (3,'RECHAZADA'),(4,'REVISION_MANUAL');

CREATE TABLE crediya.tipo_prestamo (
  id_tipo_prestamo VARCHAR(36) PRIMARY KEY,
  nombre VARCHAR(100) NOT NULL UNIQUE,
  tasa_interes double,
  monto_minimo double,
  monto_maximo double,
  validacion_automatica integer
) ENGINE=InnoDB;

-- transaccional
CREATE TABLE crediya.solicitud (
  id_solicitud VARCHAR(36) PRIMARY KEY,
  documento_cliente VARCHAR(50) NOT NULL,
  email VARCHAR(255),
  monto DECIMAL(12,2) NOT NULL CHECK (monto > 0),
  plazo INT NOT NULL CHECK (plazo > 0),
  id_estado TINYINT UNSIGNED NOT NULL DEFAULT 1,
  id_tipo_prestamo VARCHAR(36) NOT NULL,
  creada_en TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (id_estado) REFERENCES estados(id_estado),
  FOREIGN KEY (id_tipo_prestamo) REFERENCES tipo_prestamo(id_tipo_prestamo)
) ENGINE=InnoDB;