package com.sistemabarberia.fadex_backend.modules.persona.controller;


import com.sistemabarberia.fadex_backend.commons.response.ApiResponse;
import com.sistemabarberia.fadex_backend.commons.response.PageResponse;
import com.sistemabarberia.fadex_backend.modules.persona.dto.request.PersonaRequestDTO;
import com.sistemabarberia.fadex_backend.modules.persona.dto.request.PersonaUpdateRequestDTO;
import com.sistemabarberia.fadex_backend.modules.persona.dto.response.PersonaResponseDTO;
import com.sistemabarberia.fadex_backend.modules.persona.service.IPersonaService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/personas")
public class PersonaController {
    @Autowired
    private IPersonaService personaService;

    /*CRUD BASICO*/

    //Listar
    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<PersonaResponseDTO>>> listarPersonas(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        Page<PersonaResponseDTO> result = personaService.listarPersonas(pageable);
        return ResponseEntity.ok(ApiResponse.ok("Personas obtenidos correctamente", result));
    }

    //Buscar
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<PersonaResponseDTO>> buscarPersona(@PathVariable Integer id) {
        PersonaResponseDTO persona = personaService.buscarPersona(id);
        return ResponseEntity.ok(ApiResponse.ok("Persona obtenido correctamente", persona));
    }

    //Crear
    @PostMapping
    public ResponseEntity<ApiResponse<PersonaResponseDTO>> crearPersona(@Valid @RequestBody PersonaRequestDTO requestDTO) {
        PersonaResponseDTO creado = personaService.crearPersona(requestDTO);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Persona creado correctamente", creado));
    }

    /*
       {
        "nombre": "Jojhan Smith",
        "apellido":"Arce Cristobal",
        "telefono":"997236893",
        "email":"elmaspro@gmail.com"
       }
     */

    //Eliminar persona
    @DeleteMapping("/eliminar/{id}")
    public ResponseEntity<ApiResponse<PersonaResponseDTO>> eliminar(@PathVariable Integer id) {
        PersonaResponseDTO eliminado = personaService.eliminar(id);
        return ResponseEntity.ok(ApiResponse.ok("Persona eliminado correctamente", eliminado));
    }

    //Actualizar persona
    @PatchMapping("/actualizar/{id}") //Utilizacion de PATH para actualizar lo que se necesite
    public ResponseEntity<ApiResponse<PersonaResponseDTO>> actualizarPersona(
            @PathVariable Integer id,
            @Valid @RequestBody PersonaUpdateRequestDTO requestDTO) {
        PersonaResponseDTO actualizado = personaService.actualizarPersona(id, requestDTO);
        return ResponseEntity.ok(ApiResponse.ok("Persona actualizado correctamente", actualizado));
    }

    /* OPCIONALES
       {
        "nombre": "Juan",
        "apellido": "Pérez",
        "telefono": "987654321",
        "email": "juan@gmail.com"
       }
    */

    /*AVANZADOS*/
}
