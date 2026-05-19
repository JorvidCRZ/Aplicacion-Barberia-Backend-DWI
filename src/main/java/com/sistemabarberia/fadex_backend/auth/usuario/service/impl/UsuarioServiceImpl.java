package com.sistemabarberia.fadex_backend.auth.usuario.service.impl;

import com.sistemabarberia.fadex_backend.auth.rol.Entity.Rol;
import com.sistemabarberia.fadex_backend.auth.rol.Entity.RolRepository;
import com.sistemabarberia.fadex_backend.auth.usuario.Entity.Usuario;
import com.sistemabarberia.fadex_backend.auth.usuario.Repository.UsuarioRepository;
import com.sistemabarberia.fadex_backend.auth.usuario.dto.request.*;
import com.sistemabarberia.fadex_backend.auth.usuario.dto.response.UsuarioResponse;
import com.sistemabarberia.fadex_backend.auth.usuario.service.IUsuarioService;
import com.sistemabarberia.fadex_backend.commons.exception.ResourceNotFoundException;
import com.sistemabarberia.fadex_backend.modules.barbero.entity.Barbero;
import com.sistemabarberia.fadex_backend.modules.barbero.repository.BarberoRepository;
import com.sistemabarberia.fadex_backend.modules.cliente.entity.Cliente;
import com.sistemabarberia.fadex_backend.modules.cliente.repository.ClienteRepository;
import com.sistemabarberia.fadex_backend.modules.persona.entity.Persona;
import com.sistemabarberia.fadex_backend.modules.persona.repository.PersonaRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.sistemabarberia.fadex_backend.commons.exception.BusinessException;
import org.springframework.http.HttpStatus;

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

    @Override
    @Transactional
    public UsuarioResponse crearCliente(CreateClienteRequest req) {
        Usuario usuario = crearUsuarioBase(req);
        Persona persona = crearPersona(req, usuario);

        Cliente cliente = Cliente.builder()
                .persona(persona)
                .build();

        clienteRepository.save(cliente);
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
                .build();
    }
}