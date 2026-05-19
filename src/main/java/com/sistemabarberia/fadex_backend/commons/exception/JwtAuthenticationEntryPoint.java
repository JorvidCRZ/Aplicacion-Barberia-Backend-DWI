package com.sistemabarberia.fadex_backend.commons.exception;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sistemabarberia.fadex_backend.commons.response.ApiResponse;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;
@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final ObjectMapper objectMapper= new ObjectMapper();
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
         response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
         response.setContentType("application/json");

        ApiResponse<Void> body = ApiResponse.error("TOKEN INVALIDO O EXPIRADO");
        response.getWriter().write(objectMapper.writeValueAsString(body));
    }
}
