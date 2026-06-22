package com.sistemabarberia.fadex_backend.modules.ia.controller;

import com.sistemabarberia.fadex_backend.commons.response.ApiResponse;
import com.sistemabarberia.fadex_backend.modules.ia.dto.HaircutFeaturesRequestDTO;
import com.sistemabarberia.fadex_backend.modules.ia.service.IHaircutFeaturesService;
import com.sistemabarberia.fadex_backend.modules.ia.service.IaClienteService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api/v1/ia")
public class IaController {

    @Autowired
    private IaClienteService iaClienteService;

    @Autowired
    private IHaircutFeaturesService haircutFeaturesService;

    @PostMapping("/analizar")
    public ResponseEntity<?> analizar(
            @RequestParam("foto") MultipartFile foto,
            @RequestParam("id_cliente") Integer idCliente
    ) throws IOException {
        return ResponseEntity.ok(iaClienteService.analizar(foto, idCliente));
    }

    @GetMapping("/cortes")
    public ResponseEntity<?> listarCortes() {
        return ResponseEntity.ok().build();
    }

    @GetMapping("/clientes")
    public ResponseEntity<?> listarClientes() {
        return ResponseEntity.ok().build();
    }

    @PostMapping("/feedback")
    public ResponseEntity<?> feedback(
            @RequestParam("client_id") Integer clientId,
            @RequestParam("haircut_id") Integer haircutId,
            @RequestParam("liked") Boolean liked,
            @RequestParam("rating") Integer rating
    ) {
        return ResponseEntity.ok(iaClienteService.guardarFeedback(clientId, haircutId, liked, rating));
    }

    @GetMapping("/features/{idCorte}")
    public ResponseEntity<ApiResponse<?>> obtenerFeatures(@PathVariable Integer idCorte) {
        return ResponseEntity.ok(ApiResponse.ok("Features obtenidas",
                haircutFeaturesService.obtener(idCorte)));
    }

    @PutMapping("/features/{idCorte}")
    public ResponseEntity<ApiResponse<?>> actualizarFeatures(
            @PathVariable Integer idCorte,
            @Valid @RequestBody HaircutFeaturesRequestDTO dto
    ) {
        return ResponseEntity.ok(ApiResponse.ok("Features actualizadas correctamente",
                haircutFeaturesService.actualizar(idCorte, dto)));
    }
}