package com.sistemabarberia.fadex_backend.modules.barbero.service.impl;


import com.sistemabarberia.fadex_backend.commons.exception.BusinessException;
import com.sistemabarberia.fadex_backend.modules.barbero.dto.request.BarberoRequestDTO;
import com.sistemabarberia.fadex_backend.modules.barbero.dto.request.BarberoUpdateRequestDTO;
import com.sistemabarberia.fadex_backend.modules.barbero.dto.response.BarberoResponseDTO;
import com.sistemabarberia.fadex_backend.modules.barbero.entity.Barbero;
import com.sistemabarberia.fadex_backend.modules.barbero.mapper.BarberoMapper;
import com.sistemabarberia.fadex_backend.modules.barbero.repository.BarberoRepository;
import com.sistemabarberia.fadex_backend.modules.barbero.service.IBarberoService;
import com.sistemabarberia.fadex_backend.modules.cliente.repository.ClienteRepository;
import com.sistemabarberia.fadex_backend.modules.persona.entity.Persona;
import com.sistemabarberia.fadex_backend.modules.persona.repository.PersonaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;


@Service
public class BarberoServiceImpl implements IBarberoService {

    @Autowired
    private BarberoRepository barberoRepository;

    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired
    private PersonaRepository personaRepository;

    @Autowired
    private BarberoMapper mapper;



    //Listar Barberos
    @Override
    public Page<BarberoResponseDTO> listarBarberos(Pageable pageable) {
        return barberoRepository.findAll(pageable)
                .map(mapper::toResponseDTO);
    }

    //Crear Barbero
    @Override
    public BarberoResponseDTO crearBarbero(BarberoRequestDTO dto) {
        //Verifica que la persona existe
        Persona persona = personaRepository.findById(dto.getPersonaId())
                .orElseThrow(() -> new BusinessException(
                        "Persona no encontrada con id: " + dto.getPersonaId(),
                        HttpStatus.NOT_FOUND
                ));
        //Verifica que la persona no está ya asignada a otro barbero
        if (barberoRepository.existsByPersona_PersonaId(dto.getPersonaId())) {
            throw new BusinessException(
                    "Esta persona ya está registrada como barbero",
                    HttpStatus.CONFLICT
            );
        }
        //Verifica que la persona no está ya asignada a un Cliente
        if (clienteRepository.existsByPersona_PersonaId(dto.getPersonaId())) {
            throw new BusinessException(
                    "Esta persona ya está registrada como Cliente",
                    HttpStatus.CONFLICT
            );
        }
        Barbero barbero = mapper.toEntity(dto, persona);
        Barbero guardado = barberoRepository.save(barbero);
        return mapper.toResponseDTO(guardado);
    }

    //Eliminar
    @Override
    public BarberoResponseDTO eliminar(Integer id) {
        Barbero barbero = barberoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Barbero no encontrado"));
        BarberoResponseDTO dto = mapper.toResponseDTO(barbero);
        /*-----ELIMINA CLIENTE DIRECTAMENTE PERO NO SU PERSONA-----
        barberoRepository.delete(barbero);
        return dto;*/
        Integer idPersona = barbero.getPersona().getPersonaId();
        personaRepository.deleteById(idPersona); // BORRAS LA PERSONA (esto elimina cliente automáticamente por CASCADE)
        return dto;
    }

    //Actualizar
    @Override
    public BarberoResponseDTO actualizarBarbero(Integer id, BarberoUpdateRequestDTO dto) {
        Barbero barbero = barberoRepository.findById(id)
                .orElseThrow(() -> new BusinessException(
                        "Barbero no encontrado con id: " + id,
                        HttpStatus.NOT_FOUND
                ));

        if (dto.getExperiencia() != null) {
            barbero.setExperiencia(dto.getExperiencia());
        }
        barbero.setOcupado(dto.isOcupado());

        Barbero actualizado = barberoRepository.save(barbero);
        return mapper.toResponseDTO(actualizado);
    }

    //Buscar
    @Override
    public BarberoResponseDTO buscarBarbero(Integer id) {
        Barbero barbero = barberoRepository.findById(id)
                .orElseThrow(() -> new BusinessException(
                        "Barbero no encontrado con id: " + id,
                        HttpStatus.NOT_FOUND
                ));

        return mapper.toResponseDTO(barbero);
    }
}
