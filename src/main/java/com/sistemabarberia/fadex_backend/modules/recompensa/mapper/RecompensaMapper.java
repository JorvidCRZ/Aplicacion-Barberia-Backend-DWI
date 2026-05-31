package com.sistemabarberia.fadex_backend.modules.recompensa.mapper;

import com.sistemabarberia.fadex_backend.modules.recompensa.dto.response.RecompensaResponseDTO;
import com.sistemabarberia.fadex_backend.modules.recompensa.entity.Recompensa;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.stereotype.Component;

@Component
@Mapper(componentModel = "spring")
public interface RecompensaMapper {

    @Mapping(target = "clienteId",      source = "cliente.clienteId")
    @Mapping(target = "nombreCliente",  expression = "java(r.getCliente().getPersona().getNombre() + ' ' + r.getCliente().getPersona().getApellido())")
    @Mapping(target = "tieneCorteGratis",   expression = "java(r.getCortesGratis() > 0)")
    @Mapping(target = "cortesParaProximo",  expression = "java(10 - r.getCortesAcumulados())")
    RecompensaResponseDTO toResponseDTO(Recompensa r);
}