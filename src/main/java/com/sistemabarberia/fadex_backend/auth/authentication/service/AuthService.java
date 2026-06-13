package com.sistemabarberia.fadex_backend.auth.authentication.service;


import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.sistemabarberia.fadex_backend.auth.authentication.dto.request.LoginRequest;
import com.sistemabarberia.fadex_backend.auth.authentication.dto.response.TokenResponse;
import com.sistemabarberia.fadex_backend.auth.refreshToken.entity.RefreshToken;
import com.sistemabarberia.fadex_backend.auth.refreshToken.repository.RefreshTokenRepository;
import com.sistemabarberia.fadex_backend.auth.refreshToken.service.RefreshTokenService;
import com.sistemabarberia.fadex_backend.auth.rol.Entity.Rol;
import com.sistemabarberia.fadex_backend.auth.rol.Entity.RolRepository;
import com.sistemabarberia.fadex_backend.auth.security.jwt.JwtProperties;
import com.sistemabarberia.fadex_backend.auth.security.jwt.JwtService;
import com.sistemabarberia.fadex_backend.auth.security.service.CustomUserDetailService;
import com.sistemabarberia.fadex_backend.auth.security.service.CustomUserDetails;
import com.sistemabarberia.fadex_backend.auth.usuario.Entity.Usuario;
import com.sistemabarberia.fadex_backend.auth.usuario.Repository.UsuarioRepository;
import com.sistemabarberia.fadex_backend.auth.authentication.dto.request.RegisterRequest;
import com.sistemabarberia.fadex_backend.auth.usuario.dto.response.UsuarioResponse;
import com.sistemabarberia.fadex_backend.commons.exception.BusinessException;
import com.sistemabarberia.fadex_backend.modules.persona.entity.Persona;
import com.sistemabarberia.fadex_backend.modules.persona.repository.PersonaRepository;
import com.sistemabarberia.fadex_backend.modules.cliente.entity.Cliente;
import com.sistemabarberia.fadex_backend.modules.cliente.repository.ClienteRepository;
import com.sistemabarberia.fadex_backend.modules.persona.entity.Persona;
import com.sistemabarberia.fadex_backend.modules.persona.repository.PersonaRepository;
import com.sistemabarberia.fadex_backend.modules.recompensa.entity.Recompensa;
import com.sistemabarberia.fadex_backend.modules.recompensa.repository.RecompensaRepository;

import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {


    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final JwtProperties props;
    private final RefreshTokenService tokenRefreshService;
    private final RefreshTokenRepository refreshTokenRepository;
    private final UserDetailsService userDetailsService;
    private final UsuarioRepository usuarioRepository;
    private final RolRepository rolRepository;
    private final PasswordEncoder passwordEncoder;
    private final PersonaRepository personaRepository;

    private final ClienteRepository clienteRepository;
    private final RecompensaRepository recompensaRepository;

    @Value("${spring.security.oauth2.client.registration.google.client-id}")
    private String googleClientId;

    public TokenResponse login(LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.username(),
                        request.password()
                )
        );

        CustomUserDetails custom = (CustomUserDetails) authentication.getPrincipal();
        Usuario usuario = custom.getUsuario();
        String token = jwtService.generateToken(custom);
        String username = jwtService.extractClaim(token, Claims::getSubject);
        long expiredIn = props.getExpiration() / 1000;

        List<String> roles = jwtService.extractClaim(token,
                claims -> claims.get("roles", List.class));

        List<String> permisos = jwtService.extractClaim(token,
                claims -> claims.get("permisos", List.class));

        String rol = (roles != null && !roles.isEmpty())
                ? roles.get(0).replace("ROLE_", "")
                : null;

        RefreshToken refreshToken = tokenRefreshService.crearRefreshToken(usuario);

        return new TokenResponse(token, refreshToken.getToken(), "bearer", expiredIn, usuario.getIdUsuario(), username, rol, permisos);
    }

    @Transactional
    public TokenResponse refresh(String token) {


        if (token == null || token.isBlank()) {
            throw new BusinessException("Refresh token es requerido", HttpStatus.BAD_REQUEST);
        }


        RefreshToken storedToken = tokenRefreshService.validarRefreshToken(token);


        storedToken.setRevoked(true);
        refreshTokenRepository.save(storedToken);


        Usuario usuario = storedToken.getUsuario();


        UserDetails userDetails = userDetailsService.loadUserByUsername(usuario.getUser());


        String newAccessToken = jwtService.generateToken(userDetails);


        RefreshToken newRefreshToken = tokenRefreshService.crearRefreshToken(usuario);

        List<String> roles = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .filter(auth -> auth.startsWith("ROLE_"))
                .toList();

        List<String> permisos = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .filter(auth -> !auth.startsWith("ROLE_"))
                .toList();

        String rol = (roles != null && !roles.isEmpty())
                ? roles.get(0).replace("ROLE_", "")
                : null;

        log.info("Tokens renovados para usuario: {}", usuario.getUser());

        return TokenResponse.builder()
                .accessToken(newAccessToken)
                .refreshToken(newRefreshToken.getToken())
                .tokenType("bearer")
                .expiresIn(props.getExpiration() / 1000)
                .username(usuario.getUser())
                .rol(rol)
                .permisos(permisos)
                .build();
    }


    public void logout(String refreshToken) {
        RefreshToken token = refreshTokenRepository.findByToken(refreshToken).orElseThrow(() -> new BusinessException("token no existe", HttpStatus.BAD_REQUEST));
        token.setRevoked(true);
        refreshTokenRepository.save(token);
    }

    public UsuarioResponse register(RegisterRequest request) {
        if (personaRepository.existsByEmail(request.getCorreo())) {
            throw new BusinessException("El correo ya está registrado", HttpStatus.CONFLICT);
        }

        Rol rolCliente = rolRepository.findByNombre("cliente")
                .orElseThrow(() -> new BusinessException("Rol cliente no encontrado", HttpStatus.NOT_FOUND));

        Usuario usuario = new Usuario();
        usuario.setUser(request.getCorreo()); // username = correo
        usuario.setPassword(passwordEncoder.encode(request.getPassword()));
        usuario.setRoles(new HashSet<>(Set.of(rolCliente)));

        Usuario guardado = usuarioRepository.save(usuario);

        Persona persona = new Persona();
        persona.setNombre(request.getNombre());
        persona.setApellido(request.getApellido());
        persona.setTelefono(request.getTelefono());
        persona.setEmail(request.getCorreo());
        persona.setUsuario(guardado);
        personaRepository.save(persona);

        UsuarioResponse response = new UsuarioResponse();
        response.setIdUsuario(guardado.getIdUsuario());
        response.setUsername(guardado.getUser());
        response.setNombre(persona.getNombre());
        response.setApellido(persona.getApellido());
        response.setEmail(persona.getEmail());
        response.setTelefono(persona.getTelefono());
        response.setRol(guardado.getRoles().stream()
                .findFirst()
                .map(Rol::getNombre)
                .orElse(null));

        return response;
    }

    @Transactional
    public TokenResponse loginWithGoogle(String idTokenString) {
        try {
            GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(new NetHttpTransport(), new GsonFactory())
                    .setAudience(Collections.singletonList(googleClientId))
                    .build();
            GoogleIdToken idToken = verifier.verify(idTokenString);
            if (idToken == null) {
                throw new BusinessException("Token de Google inválido", HttpStatus.UNAUTHORIZED);
            }
            GoogleIdToken.Payload payload = idToken.getPayload();
            String email = payload.getEmail();
            String googleId = payload.getSubject();
            String name = (String) payload.get("given_name");
            String familyName = (String) payload.get("family_name");
            Usuario usuario = usuarioRepository.findByUser(email).orElse(null);
            if (usuario == null) {
                Rol rolCliente = rolRepository.findByNombre("cliente")
                        .orElseThrow(() -> new BusinessException("Rol cliente no encontrado", HttpStatus.INTERNAL_SERVER_ERROR));

                usuario = Usuario.builder()
                        .user(email)
                        .qrToken(UUID.randomUUID().toString())
                        .roles(new HashSet<>(Set.of(rolCliente)))
                        .oauthProvider("GOOGLE")
                        .oauthId(googleId)
                        .build();
                usuario = usuarioRepository.save(usuario);

                Persona persona = Persona.builder()
                        .usuario(usuario)
                        .nombre(name != null ? name : "Usuario")
                        .apellido(familyName != null ? familyName : "")
                        .email(email)
                        .build();
                personaRepository.save(persona);

                Cliente cliente = Cliente.builder()
                        .persona(persona)
                        .activo(true)
                        .build();
                Cliente guardado = clienteRepository.save(cliente);

                Recompensa recompensa = Recompensa.builder()
                        .cliente(guardado)
                        .cortesAcumulados(0)
                        .cortesGratis(0)
                        .fechaActualizacion(LocalDateTime.now())
                        .build();
                recompensaRepository.save(recompensa);
            }

            UserDetails userDetails = userDetailsService.loadUserByUsername(usuario.getUser());
            CustomUserDetails customUserDetails;
            if (userDetails instanceof CustomUserDetails) {
                customUserDetails = (CustomUserDetails) userDetails;
            } else {
                customUserDetails = new CustomUserDetails(usuario, userDetails.getAuthorities());
            }

            String token = jwtService.generateToken(customUserDetails);
            RefreshToken refreshToken = tokenRefreshService.crearRefreshToken(usuario);

            List<String> roles = customUserDetails.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .filter(auth -> auth.startsWith("ROLE_"))
                    .toList();

            List<String> permisos = customUserDetails.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .filter(auth -> !auth.startsWith("ROLE_"))
                    .toList();

            String rol = (roles != null && !roles.isEmpty()) ? roles.get(0).replace("ROLE_", "") : null;

            return new TokenResponse(
                    token,
                    refreshToken.getToken(),
                    "bearer",
                    props.getExpiration() / 1000,
                    usuario.getIdUsuario(),
                    usuario.getUser(),
                    rol,
                    permisos
            );

        } catch (Exception e) {
            log.error("Error validando token de Google: ", e);
            throw new BusinessException("No se pudo autenticar con Google", HttpStatus.UNAUTHORIZED);
        }
    }


    @Transactional
    public TokenResponse loginWithQr(String qrToken, String pin) {

        System.out.println("=== QR LOGIN ===");
        System.out.println("QR TOKEN RECIBIDO: " + qrToken);
        System.out.println("PIN RECIBIDO: " + pin);

        Usuario usuario = usuarioRepository.findByQrToken(qrToken)
                .orElseThrow(() -> {
                    System.out.println("ERROR: USUARIO NO ENCONTRADO");
                    return new BusinessException("QR inválido o no registrado", HttpStatus.UNAUTHORIZED);
                });

        System.out.println("USUARIO ENCONTRADO: " + usuario.getUser());
        System.out.println("PIN EN BD: " + usuario.getPin());

        if (usuario.getPin() == null) {
            System.out.println("ERROR: PIN NULL");
            throw new BusinessException("Este usuario no tiene PIN configurado", HttpStatus.UNAUTHORIZED);
        }

        boolean pinValido = passwordEncoder.matches(pin, usuario.getPin());
        System.out.println("PIN VALIDO: " + pinValido);

        if (!pinValido) {
            System.out.println("ERROR: PIN INCORRECTO");
            throw new BusinessException("PIN incorrecto", HttpStatus.UNAUTHORIZED);
        }

        UserDetails userDetails = userDetailsService.loadUserByUsername(usuario.getUser());

        CustomUserDetails customUserDetails;
        if (userDetails instanceof CustomUserDetails) {
            customUserDetails = (CustomUserDetails) userDetails;
        } else {
            customUserDetails = new CustomUserDetails(usuario, userDetails.getAuthorities());
        }

        String token = jwtService.generateToken(customUserDetails);
        RefreshToken refreshToken = tokenRefreshService.crearRefreshToken(usuario);

        List<String> roles = customUserDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .filter(auth -> auth.startsWith("ROLE_"))
                .toList();

        List<String> permisos = customUserDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .filter(auth -> !auth.startsWith("ROLE_"))
                .toList();

        String rol = (roles != null && !roles.isEmpty())
                ? roles.get(0).replace("ROLE_", "")
                : null;

        System.out.println("LOGIN EXITOSO para: " + usuario.getUser());

        return new TokenResponse(
                token,
                refreshToken.getToken(),
                "bearer",
                props.getExpiration() / 1000,
                usuario.getIdUsuario(),
                usuario.getUser(),
                rol,
                permisos
        );
    }
}