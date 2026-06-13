package com.sistemabarberia.fadex_backend.auth.usuario.service.impl;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.sistemabarberia.fadex_backend.auth.rol.Entity.Rol;
import com.sistemabarberia.fadex_backend.auth.rol.Entity.RolRepository;
import com.sistemabarberia.fadex_backend.auth.usuario.Entity.Usuario;
import com.sistemabarberia.fadex_backend.auth.usuario.Repository.UsuarioRepository;
import com.sistemabarberia.fadex_backend.auth.usuario.dto.request.*;
import com.sistemabarberia.fadex_backend.auth.usuario.dto.response.PermisoResponse;
import com.sistemabarberia.fadex_backend.auth.usuario.dto.response.RolResponse;
import com.sistemabarberia.fadex_backend.auth.usuario.dto.response.UsuarioResponse;
import com.sistemabarberia.fadex_backend.auth.usuario.dto.response.UsuarioTablaResponse;
import com.sistemabarberia.fadex_backend.auth.usuario.service.IUsuarioService;
import com.sistemabarberia.fadex_backend.commons.exception.ResourceNotFoundException;
import com.sistemabarberia.fadex_backend.commons.response.PageResponse;
import com.sistemabarberia.fadex_backend.modules.barbero.entity.Barbero;
import com.sistemabarberia.fadex_backend.modules.barbero.repository.BarberoRepository;
import com.sistemabarberia.fadex_backend.modules.cliente.entity.Cliente;
import com.sistemabarberia.fadex_backend.modules.cliente.repository.ClienteRepository;
import com.sistemabarberia.fadex_backend.modules.persona.entity.Persona;
import com.sistemabarberia.fadex_backend.modules.persona.repository.PersonaRepository;
import com.sistemabarberia.fadex_backend.modules.recompensa.entity.Recompensa;
import com.sistemabarberia.fadex_backend.modules.recompensa.repository.RecompensaRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.sistemabarberia.fadex_backend.commons.exception.BusinessException;
import org.springframework.http.HttpStatus;

import java.io.ByteArrayOutputStream;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UsuarioServiceImpl implements IUsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final RolRepository rolRepository;
    private final PersonaRepository personaRepository;
    private final BarberoRepository barberoRepository;
    private final ClienteRepository clienteRepository;
    private final PasswordEncoder passwordEncoder;


    @Override
    public List<UsuarioResponse> getAll() {
        return usuarioRepository.findAll()
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public UsuarioResponse getById(Integer id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new BusinessException(
                        "Usuario no encontrado con ID: " + id,
                        HttpStatus.NOT_FOUND));
        return toResponse(usuario);
    }

    @Override
    public UsuarioResponse update(Integer id, RegisterRequest request) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new BusinessException(
                        "Usuario no encontrado con ID: " + id,
                        HttpStatus.NOT_FOUND));
        usuario.setPassword(passwordEncoder.encode(request.getPassword()));
        usuarioRepository.save(usuario);
        return toResponse(usuario);
    }


    @Override
    @Transactional
    public UsuarioResponse crearAdmin(CreateUsuarioRequest req) {
        Usuario usuario = crearUsuarioBase(req);
        Persona persona = crearPersona(req, usuario);
        return toResponse(usuario, persona);
    }

    @Override
    @Transactional
    public UsuarioResponse crearBarbero(CreateBarberoRequest req) {
        Usuario usuario = crearUsuarioBase(req);
        Persona persona = crearPersona(req, usuario);

        Barbero barbero = Barbero.builder()
                .persona(persona)
                .activo(true)
                .experiencia(req.getExperiencia())
                .sueldo(req.getSueldo())
                .comision(req.getComision())
                .descripcion(req.getDescripcion())
                .fotoUrl(req.getFotoUrl())
                .ocupado(false)
                .build();

        barberoRepository.save(barbero);
        return toResponse(usuario, persona);
    }

    @Autowired
    private RecompensaRepository recompensaRepository;

    @Override
    @Transactional
    public UsuarioResponse crearCliente(CreateClienteRequest req) {
        Usuario usuario = crearUsuarioBase(req);
        Persona persona = crearPersona(req, usuario);

        Cliente cliente = Cliente.builder()
                .persona(persona)
                .activo(true)        // ← también faltaba esto
                .build();

        Cliente guardado = clienteRepository.save(cliente);

        // Crear tarjeta de recompensas automáticamente
        Recompensa recompensa = Recompensa.builder()
                .cliente(guardado)
                .cortesAcumulados(0)
                .cortesGratis(0)
                .fechaActualizacion(LocalDateTime.now())
                .build();
        recompensaRepository.save(recompensa);

        return toResponse(usuario, persona);
    }

    @Override
    public void resetPassword(Integer idUsuario, ResetPasswordRequest request) {

        Usuario usuario = usuarioRepository.findById(idUsuario)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Usuario no encontrado")
                );

        usuario.setPassword(
                passwordEncoder.encode(request.getNewPassword())
        );

        usuarioRepository.save(usuario);
    }

    @Override
    public void updateUsername(Integer id, UpdateUsernameRequest request) {

        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Usuario no encontrado")
                );

        if (usuarioRepository.existsByUser(request.getUsername())) {
            throw new BusinessException(
                    "El nombre de usuario ya existe",
                    HttpStatus.BAD_REQUEST
            );
        }
        usuario.setUser(request.getUsername());
        usuarioRepository.save(usuario);
    }

    // ─── HELPERS PRIVADOS ────────────────────────────────────────────────────

    private Usuario crearUsuarioBase(CreateUsuarioRequest req) {
        if (usuarioRepository.existsByUser(req.getUsername())) {
            throw new BusinessException("El username ya está en uso", HttpStatus.CONFLICT);
        }

        Rol rol = rolRepository.findById(req.getIdRol())
                .orElseThrow(() -> new BusinessException("Rol no encontrado", HttpStatus.NOT_FOUND));

        Usuario usuario = Usuario.builder()
                .user(req.getUsername())
                .password(passwordEncoder.encode(req.getPassword()))
                .qrToken(UUID.randomUUID().toString())
                .roles(new HashSet<>(Set.of(rol)))
                .build();

        return usuarioRepository.save(usuario);
    }

    private Persona crearPersona(CreateUsuarioRequest req, Usuario usuario) {
        Persona persona = Persona.builder()
                .usuario(usuario)
                .nombre(req.getNombre())
                .apellido(req.getApellido())
                .telefono(req.getTelefono())
                .email(req.getEmail())
                .build();

        return personaRepository.save(persona);
    }


    private UsuarioResponse toResponse(Usuario usuario) {
        String rolNombre = usuario.getRoles().isEmpty() ? "SIN ROL" :
                usuario.getRoles().iterator().next().getNombre();

        Persona persona = personaRepository.findByUsuario(usuario)
                .orElse(null);

        return UsuarioResponse.builder()
                .idUsuario(usuario.getIdUsuario())
                .username(usuario.getUser())
                .nombre(persona != null ? persona.getNombre() : null)
                .apellido(persona != null ? persona.getApellido() : null)
                .email(persona != null ? persona.getEmail() : null)
                .telefono(persona != null ? persona.getTelefono() : null)
                .rol(rolNombre)
                .tieneQr(
                        usuario.getQrToken() != null
                                && !usuario.getQrToken().isBlank()
                )
                .build();
    }

    private UsuarioResponse toResponse(Usuario usuario, Persona persona) {
        String rolNombre = usuario.getRoles().isEmpty() ? "SIN ROL" :
                usuario.getRoles().iterator().next().getNombre();

        return UsuarioResponse.builder()
                .idUsuario(usuario.getIdUsuario())
                .username(usuario.getUser())
                .nombre(persona.getNombre())
                .apellido(persona.getApellido())
                .email(persona.getEmail())
                .telefono(persona.getTelefono())
                .rol(rolNombre)
                .tieneQr(
                        usuario.getQrToken() != null
                                && !usuario.getQrToken().isBlank()
                )
                .build();
    }

    @Override
    public Page<UsuarioTablaResponse> listarUsuariosTabla(Pageable pageable) {

        Page<Object[]> page = usuarioRepository.listarUsuariosTabla(pageable);

        List<UsuarioTablaResponse> content = page.getContent()
                .stream()
                .collect(Collectors.groupingBy(
                        row -> ((Usuario) row[0]).getIdUsuario()
                ))
                .values()
                .stream()
                .map(rows -> {

                    Usuario usuario = (Usuario) rows.get(0)[0];
                    Persona persona = (Persona) rows.get(0)[1];

                    List<String> roles = rows.stream()
                            .map(r -> ((Rol) r[2]).getNombre())
                            .distinct()
                            .toList();

                    return UsuarioTablaResponse.builder()
                            .idUsuario(usuario.getIdUsuario())
                            .usuario(usuario.getUser())

                            .nombre(
                                    persona != null
                                            ? persona.getNombre()
                                            : null
                            )

                            .apellido(
                                    persona != null
                                            ? persona.getApellido()
                                            : null
                            )

                            .tieneQr(
                                    usuario.getQrToken() != null
                                            && !usuario.getQrToken().isBlank()
                            )
                            .tienePin(usuario.getPin() != null && !usuario.getPin().isBlank())

                            .roles(roles)
                            .build();
                })
                .toList();

        return new PageImpl<>(
                content,
                pageable,
                page.getTotalElements()
        );
    }

    @Override
    public Page<UsuarioTablaResponse> filtrarUsuarios(
            String rol,
            Boolean tieneQr,
            Boolean multiplesRoles,
            Pageable pageable
    ) {

        return usuarioRepository.filtrarUsuarios(
                        rol,
                        tieneQr,
                        multiplesRoles,
                        pageable
                )
                .map(usuario -> {

                    Persona persona = personaRepository.findByUsuario(usuario)
                            .orElse(null);

                    return UsuarioTablaResponse.builder()
                            .idUsuario(usuario.getIdUsuario())
                            .usuario(usuario.getUser())
                            .nombre(persona != null ? persona.getNombre() : null)
                            .apellido(persona != null ? persona.getApellido() : null)
                            .tieneQr(usuario.getQrToken() != null && !usuario.getQrToken().isBlank())

                            .tienePin(usuario.getPin() != null && !usuario.getPin().isBlank())

                            .roles(
                                    usuario.getRoles()
                                            .stream()
                                            .map(Rol::getNombre)
                                            .toList()
                            )
                            .build();
                });
    }


    @Override
    public Page<UsuarioTablaResponse> buscarUsuarios(
            String texto,
            Pageable pageable
    ) {

        return usuarioRepository.buscarUsuarios(texto, pageable)
                .map(usuario -> {

                    Persona persona = personaRepository.findByUsuario(usuario)
                            .orElse(null);

                    return UsuarioTablaResponse.builder()

                            .idUsuario(usuario.getIdUsuario())

                            .usuario(usuario.getUser())

                            .nombre(
                                    persona != null
                                            ? persona.getNombre()
                                            : null
                            )

                            .apellido(
                                    persona != null
                                            ? persona.getApellido()
                                            : null
                            )

                            .tieneQr(
                                    usuario.getQrToken() != null
                                            && !usuario.getQrToken().isBlank()
                            )

                            .roles(
                                    usuario.getRoles()
                                            .stream()
                                            .map(Rol::getNombre)
                                            .toList()
                            )

                            .build();
                });
    }

    @Override
    public byte[] generarQr(Integer idUsuario) {

        Usuario usuario = usuarioRepository.findById(idUsuario)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Usuario no encontrado")
                );

        try {

            String contenido = usuario.getQrToken();

            QRCodeWriter qrCodeWriter = new QRCodeWriter();

            BitMatrix bitMatrix = qrCodeWriter.encode(
                    contenido,
                    BarcodeFormat.QR_CODE,
                    300,
                    300
            );

            ByteArrayOutputStream outputStream =
                    new ByteArrayOutputStream();

            MatrixToImageWriter.writeToStream(
                    bitMatrix,
                    "PNG",
                    outputStream
            );

            return outputStream.toByteArray();

        } catch (Exception e) {

            throw new RuntimeException(
                    "Error al generar QR"
            );
        }
    }

    @Override
    @Transactional
    public byte[] regenerarQr(Integer idUsuario) {

        Usuario usuario = usuarioRepository.findById(idUsuario)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Usuario no encontrado")
                );

        usuario.setQrToken(UUID.randomUUID().toString());

        usuarioRepository.save(usuario);

        return generarQr(idUsuario);
    }


    @Override
    public void asignarPin(Integer idUsuario, AsignarPinRequest request) {
        Usuario usuario = usuarioRepository.findById(idUsuario)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));

        usuario.setPin(passwordEncoder.encode(request.getPin()));
        usuarioRepository.save(usuario);
    }

    @Override
    @Transactional
    public void asignarRoles(
            Integer idUsuario,
            AssignRolesRequest request
    ) {

        Usuario usuario = usuarioRepository.findById(idUsuario)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Usuario no encontrado"
                        )
                );

        Set<Rol> nuevosRoles = request.getRoles()
                .stream()
                .map(idRol -> rolRepository.findById(idRol)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Rol no encontrado: " + idRol
                                )
                        )
                )
                .collect(Collectors.toSet());

        usuario.getRoles().addAll(nuevosRoles);

        usuarioRepository.save(usuario);
    }


    @Override
    public List<RolResponse> listarRoles() {

        return rolRepository.findAll()
                .stream()
                .map(rol -> RolResponse.builder()
                        .idRol(rol.getIdRol())
                        .nombre(rol.getNombre())
                        .build()
                )
                .toList();
    }

    @Override
    public List<RolResponse> obtenerRolesUsuario(Integer idUsuario) {

        Usuario usuario = usuarioRepository.findById(idUsuario)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Usuario no encontrado"
                        )
                );

        return usuario.getRoles()
                .stream()
                .map(rol -> RolResponse.builder()
                        .idRol(rol.getIdRol())
                        .nombre(rol.getNombre())
                        .build()
                )
                .toList();
    }

    @Override
    public Page<PermisoResponse> obtenerPermisosUsuario(Integer idUsuario, Pageable pageable) {

        // Verifica que el usuario existe
        usuarioRepository.findById(idUsuario)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con id: " + idUsuario));

        return usuarioRepository.findPermisosByUsuarioId(idUsuario, pageable)
                .map(permiso -> PermisoResponse.builder()
                        .idPermiso(permiso.getIdPermiso())
                        .nombre(permiso.getNombre())
                        .descripcion(permiso.getDescripcion())
                        .build());
    }

    @Override
    @Transactional
    public void quitarRol(Integer idUsuario, Integer idRol) {
        Usuario usuario = usuarioRepository.findById(idUsuario)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));

        Rol rol = rolRepository.findById(idRol)
                .orElseThrow(() -> new ResourceNotFoundException("Rol no encontrado"));

        usuario.getRoles().remove(rol);
        usuarioRepository.save(usuario);
    }




}