package com.sistemabarberia.fadex_backend.modules.cliente.mapper;

import com.sistemabarberia.fadex_backend.modules.cliente.dto.request.ClienteRequestDTO;
import com.sistemabarberia.fadex_backend.modules.cliente.dto.response.ClienteResponseDTO;
import com.sistemabarberia.fadex_backend.modules.cliente.entity.Cliente;
import com.sistemabarberia.fadex_backend.modules.persona.entity.Persona;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.stereotype.Component;

@Component
@Mapper(componentModel = "spring")
public interface ClienteMapper {

    //Listar
    @Mapping(source = "clienteId",target="clienteId")
    ClienteResponseDTO toResponseDTO(Cliente cliente);

    //Registrar
    @Mapping(source = "persona", target = "persona")
    @Mapping(target = "clienteId", ignore = true)
    Cliente toEntity(ClienteRequestDTO dto, Persona persona);
}
