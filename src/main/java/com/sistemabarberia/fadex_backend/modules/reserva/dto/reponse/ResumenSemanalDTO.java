package com.sistemabarberia.fadex_backend.modules.reserva.dto.reponse;

import lombok.*;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ResumenSemanalDTO {

    private List<DiaSemana> dias;

    @Getter
    @Setter
    @AllArgsConstructor
    public static class DiaSemana {
        private String fecha;
        private long atendidos;
        private long cancelados;
    }
}