package com.sistemabarberia.fadex_backend.modules.analisis.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor

public class RendimientoBarberoDTO {
    private String nombre;
    private Long totalReservas;
}
