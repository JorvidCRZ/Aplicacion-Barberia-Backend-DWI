package com.sistemabarberia.fadex_backend.modules.reclamo.dto.request;

import com.sistemabarberia.fadex_backend.modules.reclamo.entity.enums.TipoProblema;
import com.sistemabarberia.fadex_backend.modules.reclamo.entity.enums.TipoReclamacion;
import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReclamoPublicoRequest {
    @NotBlank(message = "El tipo de documento es obligatorio")
    private String tipoDocumento;

    @NotBlank(message = "El número de documento es obligatorio")
    @Size(min = 8, max = 15)
    private String numeroDocumento;

    @NotBlank(message = "El nombre es obligatorio")
    private String nombres;

    @NotBlank(message = "Los apellidos son obligatorios")
    private String apellidos;

    @NotBlank(message = "El email es obligatorio")
    @Email(message = "Email inválido")
    private String email;

    @NotBlank(message = "El teléfono es obligatorio")
    private String telefono;

    private Integer idVenta;
    private Integer idReserva;

    @NotNull(message = "El tipo de reclamación es obligatorio")
    private TipoReclamacion tipoReclamacion;

    @NotNull(message = "El tipo de problema es obligatorio")
    private TipoProblema tipoProblema;

    @NotBlank(message = "La descripción es obligatoria")
    @Size(min = 5, max = 2000)
    private String descripcion;

    @NotNull(message = "La fecha de ocurrencia es obligatoria")
    private LocalDateTime fechaOcurrencia;

    @Positive(message = "El monto reclamado debe ser mayor a 0")
    private BigDecimal montoReclamado;
}