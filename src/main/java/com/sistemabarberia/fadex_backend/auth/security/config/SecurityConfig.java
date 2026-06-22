package com.sistemabarberia.fadex_backend.auth.security.config;

import com.sistemabarberia.fadex_backend.auth.security.filter.JwtAuthenticationFilter;
import com.sistemabarberia.fadex_backend.auth.security.service.CustomUserDetailService;
import com.sistemabarberia.fadex_backend.commons.exception.JwtAccesDeniedHandler;
import com.sistemabarberia.fadex_backend.commons.exception.JwtAuthenticationEntryPoint;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    private final JwtAccesDeniedHandler jwtAccesDeniedHandler;
    private final CustomUserDetailService UserDetailService;
    private final JwtAuthenticationFilter filter;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http
                .cors(Customizer.withDefaults())
                .exceptionHandling(exception ->
                        exception
                                .authenticationEntryPoint(jwtAuthenticationEntryPoint)
                                .accessDeniedHandler(jwtAccesDeniedHandler)
                )
                .csrf(csrf -> csrf.disable())
                .userDetailsService(UserDetailService)
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .authorizeHttpRequests(auth -> auth

                                // ─────────────────────────────────────────────
// AUTH PUBLICO
// ─────────────────────────────────────────────
                                .requestMatchers(
                                        "/api/v1/auth/login",
                                        "/api/v1/auth/refresh",
                                        "/api/v1/auth/logout",
                                        "/api/v1/auth/forgot-password",
                                        "/api/v1/auth/reset-password",
                                        "/api/v1/auth/google",
                                        "/api/v1/auth/qr-login",
                                        "/swagger-ui/**",
                                        "/v3/api-docs/**",
                                        "/uploads/**"
                                ).permitAll()

// ─────────────────────────────────────────────
// CHANGE PASSWORD (requiere JWT)
// ─────────────────────────────────────────────
                                .requestMatchers(HttpMethod.PATCH,
                                        "/api/v1/auth/change-password"
                                ).hasAnyRole("admin", "barbero", "cliente")
                        // ─────────────────────────────────────────────
                        // CREACIÓN DE CLIENTES (PÚBLICO)
                        // ─────────────────────────────────────────────
                        .requestMatchers(HttpMethod.POST,
                                "/api/v1/usuarios/cliente"
                        ).permitAll()

                        // ─────────────────────────────────────────────
                        // IA MODULE (TU NUEVO BLOQUE)
                        // ─────────────────────────────────────────────
                        .requestMatchers("/api/v1/ia/**")
                        .hasAnyRole("admin", "barbero", "cliente")

                        // ─────────────────────────────────────────────
                        // CREACIÓN BARBERO / ADMIN
                        // ─────────────────────────────────────────────
                        .requestMatchers(HttpMethod.POST,
                                "/api/v1/usuarios/barbero",
                                "/api/v1/usuarios/admin"
                        ).hasRole("admin")

                        // ─────────────────────────────────────────────
                        // USUARIOS ADMIN ONLY
                        // ─────────────────────────────────────────────
                        .requestMatchers(HttpMethod.GET,
                                "/api/v1/usuarios/**"
                        ).hasRole("admin")

                        .requestMatchers(HttpMethod.PUT,
                                "/api/v1/usuarios/**"
                        ).hasRole("admin")

                        // ─────────────────────────────────────────────
                        // SERVICIOS PUBLICOS
                        // ─────────────────────────────────────────────
                        .requestMatchers(HttpMethod.GET,
                                "/api/v1/servicios/**",
                                "/api/v1/categorias/**",
                                "/api/v1/barberos/**",
                                "/api/v1/productos/**"
                        ).permitAll()

                        // ─────────────────────────────────────────────
                        // BARBERO PANEL
                        // ─────────────────────────────────────────────
                        .requestMatchers("/api/v1/barbero/citas/**")
                        .hasAnyAuthority("ROLE_barbero", "ROLE_admin")

                        // ─────────────────────────────────────────────
                        // CLIENTE PRIVADO
                        // ─────────────────────────────────────────────
                        .requestMatchers(HttpMethod.GET,
                                "/api/v1/reservas/mis-reservas",
                                "/api/v1/clientes/perfil-propio",
                                "/api/v1/clientes/perfil-propio/resumen",
                                "/api/v1/servicio",
                                "/api/v1/servicio/**"
                        ).hasRole("cliente")

                        // ─────────────────────────────────────────────
                        // RESERVAS
                        // ─────────────────────────────────────────────
                        .requestMatchers(HttpMethod.POST,
                                "/api/v1/reservas"
                        ).hasAnyAuthority("ROLE_barbero", "ROLE_admin", "ROLE_cliente")

                        // ─────────────────────────────────────────────
                        // RECOMPENSAS
                        // ─────────────────────────────────────────────
                        .requestMatchers(HttpMethod.GET,
                                "/api/v1/recompensas/mi-tarjeta"
                        ).hasRole("cliente")

                        .requestMatchers(HttpMethod.GET,
                                "/api/v1/recompensas/**"
                        ).hasAnyRole("admin", "barbero")

                        // ─────────────────────────────────────────────
                        // PAGOS
                        // ─────────────────────────────────────────────
                        .requestMatchers(HttpMethod.GET,
                                "/api/v1/pagos/**"
                        ).hasAnyRole("admin", "barbero")

                        .requestMatchers(HttpMethod.POST,
                                "/api/v1/pagos"
                        ).hasAnyRole("admin", "barbero")

                        .requestMatchers(HttpMethod.PUT,
                                "/api/v1/pagos/**"
                        ).hasAnyRole("admin", "barbero")

                        .requestMatchers(HttpMethod.DELETE,
                                "/api/v1/pagos/**"
                        ).hasRole("admin")

                        // ─────────────────────────────────────────────
                        // RECLAMOS
                        // ─────────────────────────────────────────────
                        .requestMatchers(HttpMethod.POST,
                                "/api/v1/reclamos/publico"
                        ).permitAll()

                        .requestMatchers(HttpMethod.POST,
                                "/api/v1/reclamos/**",
                                "/api/v1/reclamos/**",
                                "/api/v1/reclamos/**"
                        ).hasRole("admin")

                        .requestMatchers(HttpMethod.GET,
                                "/api/v1/reclamos/**"
                        ).hasRole("admin")

                        .requestMatchers(HttpMethod.PUT,
                                "/api/v1/reclamos/**"
                        ).hasRole("admin")

                        .requestMatchers(HttpMethod.DELETE,
                                "/api/v1/reclamos/**"
                        ).hasRole("admin")

                        // ─────────────────────────────────────────────
                        // DEFAULT
                        // ─────────────────────────────────────────────
                        // ─────────────────────────────────────────────
                        // PLANILLAS (ACCESO PARA TODOS LOS ROLES)
                        // ─────────────────────────────────────────────
                                .requestMatchers("/api/v1/planillas/**")
                                .hasAnyRole("admin", "barbero", "cliente")
                        .anyRequest().authenticated()
                )
                .addFilterBefore(filter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {

        CorsConfiguration config = new CorsConfiguration();

        config.setAllowedOrigins(List.of("http://localhost:4200"));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));

        config.setAllowedHeaders(List.of(
                "Authorization",
                "Content-Type",
                "Accept"
        ));

        config.setExposedHeaders(List.of("Authorization"));
        config.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);

        return source;
    }
}