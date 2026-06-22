package com.sistemabarberia.fadex_backend.modules.reclamo.dto.response;

import com.sistemabarberia.fadex_backend.modules.reclamo.entity.enums.TipoAdjuntoReclamo;
import lombok.*;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReclamoAdjuntoResponse {
    private Long idAdjunto;
    private TipoAdjuntoReclamo tipoAdjunto;
    private String nombreOriginal;
    private String urlArchivo;
    private String mimeType;
    private LocalDateTime fechaSubida;
}