package com.sistemabarberia.fadex_backend.modules.reserva.dto.Response;

import com.sistemabarberia.fadex_backend.modules.reserva.entity.EstadoReserva;
import com.sistemabarberia.fadex_backend.modules.reserva.entity.TipoReserva;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CitaBarberoResponseDTO {

    private Long idReserva;
    private String nombreCliente;
    private String apellidoCliente;
    private String telefonoCliente;
    private LocalDate fecha;
    private LocalTime horaInicio;
    private EstadoReserva estado;
    private TipoReserva tipoReserva;
    private List<DetalleReservaDTO> servicios;


}
