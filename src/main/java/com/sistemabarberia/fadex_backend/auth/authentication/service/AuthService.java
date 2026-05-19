package com.sistemabarberia.fadex_backend.auth.authentication.service;


import com.sistemabarberia.fadex_backend.auth.authentication.dto.request.LoginRequest;
import com.sistemabarberia.fadex_backend.auth.authentication.dto.response.TokenResponse;
import com.sistemabarberia.fadex_backend.auth.refreshToken.entity.RefreshToken;
import com.sistemabarberia.fadex_backend.auth.refreshToken.repository.RefreshTokenRepository;
import com.sistemabarberia.fadex_backend.auth.refreshToken.service.RefreshTokenService;
import com.sistemabarberia.fadex_backend.auth.security.jwt.JwtProperties;
import com.sistemabarberia.fadex_backend.auth.security.jwt.JwtService;
import com.sistemabarberia.fadex_backend.auth.security.service.CustomUserDetailService;
import com.sistemabarberia.fadex_backend.auth.security.service.CustomUserDetails;
import com.sistemabarberia.fadex_backend.auth.usuario.Entity.Usuario;
import com.sistemabarberia.fadex_backend.auth.usuario.Repository.UsuarioRepository;
import com.sistemabarberia.fadex_backend.commons.exception.BusinessException;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final JwtProperties props;
    private final JwtProperties jwtProperties;
    private final RefreshTokenService tokenRefreshService;
    private final RefreshTokenRepository refreshTokenRepository;
    private final UserDetailsService userDetailsService;
    private final UsuarioRepository usuarioRepositorio;


    public TokenResponse login(LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.username(),
                        request.password()
                )
        );


       CustomUserDetails custom =  (CustomUserDetails) authentication.getPrincipal();
       Usuario usuario = custom.getUsuario();

        String token = jwtService.generateToken(custom);

        String username = jwtService.extractClaim(token, Claims::getSubject);
        long expiredIn = jwtProperties.getExpiration() / 1000;


        List<String> roles = jwtService.extractClaim(token,
                claims -> claims.get("roles", List.class));

        List<String> permisos = jwtService.extractClaim(token,
                claims -> claims.get("permisos", List.class));

        String rol = (roles != null && !roles.isEmpty())
                ? roles.get(0).replace("ROLE_", "")
                : null;
        RefreshToken refreshToken = tokenRefreshService.crearRefreshToken(usuario);

        return new TokenResponse(token,refreshToken.getToken(), "bearer", expiredIn, username, rol, permisos);
    }

    @Transactional
    public TokenResponse refresh(String token) {


        if (token == null || token.isBlank()) {
            throw new BusinessException("Refresh token es requerido",HttpStatus.BAD_REQUEST);
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


    public void logout(String refreshToken){
        RefreshToken token = refreshTokenRepository.findByToken(refreshToken).orElseThrow(()->new BusinessException("token no existe",HttpStatus.BAD_REQUEST));
        token.setRevoked(true);
        refreshTokenRepository.save(token);
    }
}
