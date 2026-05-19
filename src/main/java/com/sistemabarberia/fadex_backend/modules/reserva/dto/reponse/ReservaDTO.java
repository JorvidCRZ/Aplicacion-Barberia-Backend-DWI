package com.sistemabarberia.fadex_backend.modules.reserva.dto.reponse;

import com.sistemabarberia.fadex_backend.modules.reserva.entity.EstadoReserva;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReservaDTO {

    private Integer id;
    private String nombreCliente;
    private String telefonoCliente;
    private String nombreBarbero;         // ← nuevo (columna Barbero de la tabla)
    private List<String> servicios;
    private String servicioResumen;
    private LocalDate fecha;
    private Integer duracionMinutos;      // ← nuevo (suma de cortes)
    private BigDecimal totalPrecio;       // ← nuevo (suma de precios)
    private LocalTime horaInicio;
    private LocalTime horaFin;
    private EstadoReserva estado;
    private String tipoReserva;

}
