package com.sistemabarberia.fadex_backend.modules.reclamo.dto;

import lombok.*;


@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReclamoResumen {
    private long abiertos;
    private long enProceso;
    private long resueltos;
    private long cerrados;
    private long anulados;
    private long total;
}
