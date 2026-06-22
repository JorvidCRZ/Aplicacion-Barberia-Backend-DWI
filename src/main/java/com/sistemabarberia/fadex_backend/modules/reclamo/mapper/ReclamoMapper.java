package com.sistemabarberia.fadex_backend.modules.reclamo.mapper;

import com.sistemabarberia.fadex_backend.modules.reclamo.dto.response.ReclamoAdjuntoResponse;
import com.sistemabarberia.fadex_backend.modules.reclamo.dto.response.ReclamoResponse;
import com.sistemabarberia.fadex_backend.modules.reclamo.entity.Reclamo;
import com.sistemabarberia.fadex_backend.modules.reclamo.entity.ReclamoAdjunto;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

@Component
public class ReclamoMapper {
    public ReclamoResponse toResponse(Reclamo reclamo) {
        return ReclamoResponse.builder()
                .idReclamo(reclamo.getIdReclamo()).numeroReclamo(reclamo.getNumeroReclamo()).nombreCliente(reclamo.getNombreCliente()).correoCliente(reclamo.getCorreoCliente())
                .telefonoCliente(reclamo.getTelefonoCliente()).tipoReclamacion(reclamo.getTipoReclamacion()).tipoProblema(reclamo.getTipoProblema())
                .causaReclamo(reclamo.getCausaReclamo()).estadoReclamo(reclamo.getEstadoReclamo()).solucionReclamo(reclamo.getSolucionReclamo()).descripcion(reclamo.getDescripcion())
                .notasInternas(reclamo.getNotasInternas()).montoReclamado(reclamo.getMontoReclamado()).montoCompensado(reclamo.getMontoCompensado())
                .fechaOcurrencia(reclamo.getFechaOcurrencia()).fechaReclamo(reclamo.getFechaReclamo()).fechaResolucion(reclamo.getFechaResolucion())
                .esPublico(reclamo.isEsPublico()).adjuntos(null).build();
    }

    public ReclamoResponse toDetalleResponse(Reclamo reclamo) {
        List<ReclamoAdjuntoResponse> adjuntos = reclamo.getAdjuntos() == null ? Collections.emptyList() : reclamo.getAdjuntos().stream()
                        .map(adjunto -> toAdjuntoResponse(adjunto)).toList();

        return ReclamoResponse.builder()
                .idReclamo(reclamo.getIdReclamo()).numeroReclamo(reclamo.getNumeroReclamo()).nombreCliente(reclamo.getNombreCliente()).correoCliente(reclamo.getCorreoCliente())
                .telefonoCliente(reclamo.getTelefonoCliente()).tipoReclamacion(reclamo.getTipoReclamacion()).tipoProblema(reclamo.getTipoProblema())
                .causaReclamo(reclamo.getCausaReclamo()).estadoReclamo(reclamo.getEstadoReclamo()).solucionReclamo(reclamo.getSolucionReclamo()).descripcion(reclamo.getDescripcion())
                .notasInternas(reclamo.getNotasInternas()).montoReclamado(reclamo.getMontoReclamado()).montoCompensado(reclamo.getMontoCompensado()).fechaOcurrencia(reclamo.getFechaOcurrencia())
                .fechaReclamo(reclamo.getFechaReclamo()).fechaResolucion(reclamo.getFechaResolucion()).esPublico(reclamo.isEsPublico()).adjuntos(adjuntos).build();
    }

    private ReclamoAdjuntoResponse toAdjuntoResponse(ReclamoAdjunto adjunto) {
        return ReclamoAdjuntoResponse.builder().idAdjunto(adjunto.getIdAdjunto()).tipoAdjunto(adjunto.getTipoAdjunto()).nombreOriginal(adjunto.getNombreOriginal())
                .urlArchivo(adjunto.getUrlArchivo()).mimeType(adjunto.getMimeType()).fechaSubida(adjunto.getFechaSubida()).build();
    }
}