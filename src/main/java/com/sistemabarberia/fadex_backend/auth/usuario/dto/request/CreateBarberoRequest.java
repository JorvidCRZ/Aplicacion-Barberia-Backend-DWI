package com.sistemabarberia.fadex_backend.auth.usuario.dto.request;

import lombok.Data;
import lombok.EqualsAndHashCode;
import java.math.BigDecimal;


@Data
@EqualsAndHashCode(callSuper = true)
public class CreateBarberoRequest extends CreateUsuarioRequest {
    private Integer experiencia;
    private BigDecimal sueldo;
    private BigDecimal comision;
    private String descripcion;
    private String fotoUrl;
}
