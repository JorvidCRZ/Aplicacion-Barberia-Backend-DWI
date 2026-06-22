package com.sistemabarberia.fadex_backend.modules.cliente.controller;

import com.sistemabarberia.fadex_backend.auth.security.service.CustomUserDetails;
import com.sistemabarberia.fadex_backend.auth.usuario.Entity.Usuario;
import com.sistemabarberia.fadex_backend.auth.usuario.Repository.UsuarioRepository;
import com.sistemabarberia.fadex_backend.auth.usuario.service.IUsuarioService;
import com.sistemabarberia.fadex_backend.commons.response.ApiResponse;
import com.sistemabarberia.fadex_backend.commons.response.PageResponse;
import com.sistemabarberia.fadex_backend.modules.cliente.dto.request.ClienteRequestDTO;
import com.sistemabarberia.fadex_backend.modules.cliente.dto.response.ActividadRecienteResponse;
import com.sistemabarberia.fadex_backend.modules.cliente.dto.response.ClienteDetalleResumenDTO;
import com.sistemabarberia.fadex_backend.modules.cliente.dto.response.ClienteResponseDTO;
import com.sistemabarberia.fadex_backend.modules.cliente.dto.response.ClienteResumenResponseDTO;
import com.sistemabarberia.fadex_backend.modules.cliente.service.IClienteService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/v1/clientes")
public class ClienteController {

    @Autowired
    private IClienteService clienteService;
    @Autowired
    private UsuarioRepository usuarioRepository;


    // ─────────────────────────────────────────────────────────────────────────
    // CRUD BÁSICO
    // ─────────────────────────────────────────────────────────────────────────

    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<ClienteResponseDTO>>> listarClientes(
            @RequestParam(defaultValue = "0")  int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        Page<ClienteResponseDTO> result = clienteService.listarClientes(pageable);
        return ResponseEntity.ok(ApiResponse.ok("Clientes obtenidos correctamente", result));
    }

    @GetMapping("/inhabilitados")
    public ResponseEntity<ApiResponse<PageResponse<ClienteResponseDTO>>> listarClientesInhabilitados(
            @RequestParam(defaultValue = "0")  int page,
            @RequestParam(defaultValue = "10") int size
    ) {

        Pageable pageable = PageRequest.of(page, size);

        Page<ClienteResponseDTO> result =
                clienteService.listarClientesInhabilitados(pageable);

        return ResponseEntity.ok(
                ApiResponse.ok("Clientes inhabilitados obtenidos correctamente", result)
        );
    }

    @PatchMapping("/{id}/deshabilitar")
    public ResponseEntity<ApiResponse<String>> deshabilitarCliente(
            @PathVariable Integer id
    ) {

        clienteService.deshabilitarCliente(id);

        return ResponseEntity.ok(
                ApiResponse.ok("Cliente deshabilitado correctamente", "OK")
        );
    }

    @PatchMapping("/{id}/reactivar")
    public ResponseEntity<ApiResponse<String>> reactivarCliente(
            @PathVariable Integer id
    ) {

        clienteService.reactivarCliente(id);

        return ResponseEntity.ok(
                ApiResponse.ok("Cliente reactivado correctamente", "OK")
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ClienteResponseDTO>> buscarCliente(
            @PathVariable Integer id
    ) {
        ClienteResponseDTO cliente = clienteService.buscarCliente(id);
        return ResponseEntity.ok(ApiResponse.ok("Cliente obtenido correctamente", cliente));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<ClienteResponseDTO>> crearCliente(
            @Valid @RequestBody ClienteRequestDTO requestDTO
    ) {
        ClienteResponseDTO creado = clienteService.crearCliente(requestDTO);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Cliente creado correctamente", creado));
    }


    @DeleteMapping("/eliminar/{id}")
    public ResponseEntity<ApiResponse<ClienteResponseDTO>> eliminar(
            @PathVariable Integer id
    ) {
        ClienteResponseDTO eliminado = clienteService.eliminar(id);
        return ResponseEntity.ok(ApiResponse.ok("Cliente eliminado correctamente", eliminado));
    }

    // ─────────────────────────────────────────────────────────────────────────
    // BUSCADOR POR NOMBRE
    // ─────────────────────────────────────────────────────────────────────────

    @GetMapping("/buscar")
    public ResponseEntity<ApiResponse<PageResponse<ClienteResponseDTO>>> buscarPorNombre(
            @RequestParam String nombre,
            @RequestParam(defaultValue = "0")  int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        Page<ClienteResponseDTO> result = clienteService.buscarPorNombre(nombre, pageable);
        return ResponseEntity.ok(ApiResponse.ok("Búsqueda completada", result));
    }

    // ─────────────────────────────────────────────────────────────────────────
    // FILTROS POR FECHA
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Filtra clientes según el parámetro {@code filtro}:
     * <ul>
     *   <li><b>todos</b>     – todos los clientes (por defecto)</li>
     *   <li><b>recientes</b> – los más recientes primero</li>
     *   <li><b>mes</b>       – registrados en el mes en curso</li>
     *   <li><b>anio</b>      – registrados en el año en curso</li>
     * </ul>
     *
     * GET /api/v1/clientes/filtrar?filtro=mes&page=0&size=10
     */
    @GetMapping("/filtrar")
    public ResponseEntity<ApiResponse<PageResponse<ClienteResponseDTO>>> filtrar(
            @RequestParam(defaultValue = "todos") String filtro,
            @RequestParam(defaultValue = "0")      int page,
            @RequestParam(defaultValue = "10")     int size
    ) {
        Pageable pageable = PageRequest.of(page, size);

        Page<ClienteResponseDTO> result = switch (filtro.toLowerCase()) {
            case "recientes" -> clienteService.filtrarRecientes(pageable);
            case "mes"       -> clienteService.filtrarPorMesActual(pageable);
            case "anio"      -> clienteService.filtrarPorAnioActual(pageable);
            default          -> clienteService.filtrarTodos(pageable);
        };

        String mensaje = switch (filtro.toLowerCase()) {
            case "recientes" -> "Clientes recientes obtenidos";
            case "mes"       -> "Clientes del mes actual obtenidos";
            case "anio"      -> "Clientes del año actual obtenidos";
            default          -> "Todos los clientes obtenidos";
        };

        return ResponseEntity.ok(ApiResponse.ok(mensaje, result));
    }

    /**
     * Filtra clientes registrados entre dos fechas (inclusive).
     *
     * GET /api/v1/clientes/filtrar/rango?fechaInicio=2024-01-01&fechaFin=2024-06-30&page=0&size=10
     */
    @GetMapping("/filtrar/rango")
    public ResponseEntity<ApiResponse<PageResponse<ClienteResponseDTO>>> filtrarPorRango(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaInicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaFin,
            @RequestParam(defaultValue = "0")  int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        Page<ClienteResponseDTO> result =
                clienteService.filtrarPorRangoFechas(fechaInicio, fechaFin, pageable);
        return ResponseEntity.ok(
                ApiResponse.ok("Clientes en el rango de fechas obtenidos", result));
    }

    // ─────────────────────────────────────────────────────────────────────────
    // RESÚMENES Y ACTIVIDAD
    // ─────────────────────────────────────────────────────────────────────────

    @GetMapping("/resumen")
    public ResponseEntity<ApiResponse<ClienteResumenResponseDTO>> obtenerResumen() {
        ClienteResumenResponseDTO resumen = clienteService.obtenerResumen();
        return ResponseEntity.ok(ApiResponse.ok("Resumen obtenido correctamente", resumen));
    }

    @GetMapping("/{id}/resumen")
    public ResponseEntity<ApiResponse<ClienteDetalleResumenDTO>> obtenerResumenCliente(
            @PathVariable Integer id
    ) {
        ClienteDetalleResumenDTO resumen = clienteService.obtenerResumenCliente(id);
        return ResponseEntity.ok(
                ApiResponse.ok("Resumen del cliente obtenido correctamente", resumen));
    }

    @GetMapping("/{id}/actividad")
    public ResponseEntity<ApiResponse<List<ActividadRecienteResponse>>> obtenerActividad(
            @PathVariable Integer id
    ) {
        List<ActividadRecienteResponse> actividad =
                clienteService.obtenerActividadReciente(id);
        return ResponseEntity.ok(
                ApiResponse.ok("Actividad reciente obtenida correctamente", actividad));
    }
    @GetMapping("/perfil-propio")
    public ResponseEntity<ApiResponse<ClienteResponseDTO>> obtenerPerfilPropio(
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        Integer usuarioId = userDetails.getUsuario().getIdUsuario();
        ClienteResponseDTO cliente = clienteService.obtenerPerfilPropio(usuarioId);
        return ResponseEntity.ok(
                ApiResponse.ok("Perfil del cliente autenticado obtenido", cliente));
    }

    @GetMapping("/perfil-propio/resumen")
    public ResponseEntity<ApiResponse<ClienteDetalleResumenDTO>> obtenerResumenPropio(
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        Integer usuarioId = userDetails.getUsuario().getIdUsuario();
        System.out.println("USUARIO ID PERFIL RESUMEN: " + usuarioId);
        ClienteDetalleResumenDTO resumen = clienteService.obtenerResumenPropio(usuarioId);
        return ResponseEntity.ok(
                ApiResponse.ok("Resumen del cliente autenticado obtenido", resumen));
    }

    // ClienteController.java — nuevo endpoint
    @GetMapping("/mi-cliente")
    public ResponseEntity<ApiResponse<Integer>> obtenerMiClienteId(
            @AuthenticationPrincipal UserDetails userDetails) {
        System.out.println("USUARIO AUTENTICADO: " + userDetails.getUsername());
        Integer idUsuario = usuarioRepository.findByUser(userDetails.getUsername())
                .map(Usuario::getIdUsuario)
                .orElseThrow();
        return ResponseEntity.ok(ApiResponse.ok("Cliente obtenido",
                clienteService.obtenerIdClientePorUsuario(idUsuario)));
    }
}