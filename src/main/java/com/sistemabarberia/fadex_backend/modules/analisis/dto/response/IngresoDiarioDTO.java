package com.sistemabarberia.fadex_backend.modules.analisis.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class IngresoDiarioDTO {
    private LocalDate fecha;
    private BigDecimal total;
}
