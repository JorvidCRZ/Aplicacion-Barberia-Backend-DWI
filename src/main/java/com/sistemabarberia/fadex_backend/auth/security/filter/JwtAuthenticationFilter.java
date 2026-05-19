package com.sistemabarberia.fadex_backend.auth.security.filter;

import com.sistemabarberia.fadex_backend.auth.security.jwt.JwtService;
import com.sistemabarberia.fadex_backend.auth.security.service.CustomUserDetails;
import com.sistemabarberia.fadex_backend.auth.usuario.Entity.Usuario;
import com.sistemabarberia.fadex_backend.auth.usuario.Repository.UsuarioRepository;
import com.sistemabarberia.fadex_backend.commons.exception.ResourceNotFoundException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;



@Component
@Slf4j
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UsuarioRepository usuarioRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {


        try {
            final String authHeader = request.getHeader("Authorization");

            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                filterChain.doFilter(request, response);
                return;
            }
            String path = request.getServletPath();

            if (path.startsWith("/api/v1/auth")) {
                filterChain.doFilter(request, response);
                return;
            }


            if (request.getMethod().equals("OPTIONS")) {
                filterChain.doFilter(request, response);
                return;
            }

            final String jwt = authHeader.substring(7);

            if (!jwtService.validateToken(jwt)) {
                filterChain.doFilter(request, response);
                return;
            }

            if (SecurityContextHolder.getContext().getAuthentication() != null) {
                filterChain.doFilter(request, response);
                return;
            }

            final String username = jwtService.extractClaim(jwt, Claims::getSubject);

            Usuario usuario = usuarioRepository.findByUser(username).orElseThrow(()->new ResourceNotFoundException("no existe el usuario"));

            final List<String> permisos = jwtService.extractClaim(jwt, claims -> claims.get("permisos", List.class));
            List<GrantedAuthority> authorities = new ArrayList<>();

            final List<String> roles =
                    jwtService.extractClaim(jwt, claims -> claims.get("roles", List.class));



            if (roles != null) {
                authorities.addAll(
                        roles.stream()
                                .map(SimpleGrantedAuthority::new)
                                .toList()
                );
            }

            if (permisos != null) {
                authorities.addAll(
                        permisos.stream()
                                .map(SimpleGrantedAuthority::new)
                                .toList()
                );
            }
            CustomUserDetails userDetails =
                    new CustomUserDetails(usuario, authorities);

            UsernamePasswordAuthenticationToken authToken =
                    new UsernamePasswordAuthenticationToken(
                            userDetails,
                            null,
                            authorities
                    );

            authToken.setDetails(
                    new WebAuthenticationDetailsSource().buildDetails(request)
            );



            SecurityContextHolder.getContext().setAuthentication(authToken);

            log.debug("Usuario autenticado vía JWT: {}", username);


        } catch (ExpiredJwtException e) {
            log.warn("Token JWT expirado: {}", e.getMessage());

        } catch (JwtException e) {
            log.error("Error procesando JWT: {}", e.getMessage());
        } catch (Exception e) {
            log.error("Error inesperado en JwtAuthenticationFilter: {}", e.getMessage());
        }

        filterChain.doFilter(request, response);

    }
}
