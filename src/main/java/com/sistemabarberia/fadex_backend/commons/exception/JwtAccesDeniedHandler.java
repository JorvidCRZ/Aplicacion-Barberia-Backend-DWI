package com.sistemabarberia.fadex_backend.commons.exception;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sistemabarberia.fadex_backend.commons.response.ApiResponse;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component

public class JwtAccesDeniedHandler implements AccessDeniedHandler {

    private final ObjectMapper objectMapper= new ObjectMapper();

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException, ServletException {
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        response.setContentType("application/json");
        ApiResponse<Void> body = ApiResponse.error("No tienes permisos para esta acción");
        response.getWriter().write(objectMapper.writeValueAsString(body));
    }
}
