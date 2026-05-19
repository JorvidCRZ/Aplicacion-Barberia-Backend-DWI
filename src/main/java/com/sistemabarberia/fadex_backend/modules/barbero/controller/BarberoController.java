package com.sistemabarberia.fadex_backend.modules.barbero.controller;


import com.sistemabarberia.fadex_backend.commons.response.ApiResponse;
import com.sistemabarberia.fadex_backend.commons.response.PageResponse;
import com.sistemabarberia.fadex_backend.modules.barbero.dto.request.BarberoRequestDTO;
import com.sistemabarberia.fadex_backend.modules.barbero.dto.request.BarberoUpdateRequestDTO;
import com.sistemabarberia.fadex_backend.modules.barbero.dto.response.BarberoResponseDTO;
import com.sistemabarberia.fadex_backend.modules.barbero.service.IBarberoService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/barberos")
public class BarberoController {

    @Autowired
    private IBarberoService barberoService;

    /*CRUD BASICO*/

    //Listar Barberos
    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<BarberoResponseDTO>>> listarBarberos(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        Page<BarberoResponseDTO> result = barberoService.listarBarberos(pageable);
        return ResponseEntity.ok(ApiResponse.ok("Barberos obtenidos correctamente", result));
    }

    //Buscar Barbero
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<BarberoResponseDTO>> buscarBarbero(@PathVariable Integer id) {
        BarberoResponseDTO barbero = barberoService.buscarBarbero(id);
        return ResponseEntity.ok(ApiResponse.ok("Barbero obtenido correctamente", barbero));
    }

    //Crear barbero
    @PostMapping
    public ResponseEntity<ApiResponse<BarberoResponseDTO>> crearBarbero(@Valid @RequestBody BarberoRequestDTO requestDTO) {
        BarberoResponseDTO creado = barberoService.crearBarbero(requestDTO);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Barbero creado correctamente", creado));
    }

    /*
       {
        "personaId": 1, [Antes crear persona]
        "experiencia": 3,
        "ocupado": false
       }
    * */



    //Eliminar barbero
    @DeleteMapping("/eliminar/{id}")
    public ResponseEntity<ApiResponse<BarberoResponseDTO>> eliminar(@PathVariable Integer id) {
        BarberoResponseDTO eliminado = barberoService.eliminar(id);
        return ResponseEntity.ok(ApiResponse.ok("Barbero eliminado correctamente", eliminado));
    }

    // Actualizar barbero
    @PatchMapping("/actualizar/{id}") //Utilizacion de PATH para actualizar lo que se necesite
    public ResponseEntity<ApiResponse<BarberoResponseDTO>> actualizarBarbero(
            @PathVariable Integer id,
            @Valid @RequestBody BarberoUpdateRequestDTO requestDTO) {
        BarberoResponseDTO actualizado = barberoService.actualizarBarbero(id, requestDTO);
        return ResponseEntity.ok(ApiResponse.ok("Barbero actualizado correctamente", actualizado));
    }
    /*
      {
        "experiencia": 5,
        "ocupado": false  --> O true 
      }
     */



    /*AVANZADOS*/
}
