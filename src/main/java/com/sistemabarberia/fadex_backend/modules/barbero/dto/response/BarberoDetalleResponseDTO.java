package com.sistemabarberia.fadex_backend.modules.barbero.dto.response;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
@Data
@Builder
public class BarberoDetalleResponseDTO {
    private Integer barberoId;
    private String nombre;
    private String apellido;
    private String telefono;
    private String email;
    private Integer experiencia;
    private LocalDate fechaIngreso;
    private boolean ocupado;
    private BigDecimal sueldo;
    private BigDecimal comision;
    private String descripcion;
    private String fotoUrl;
}
