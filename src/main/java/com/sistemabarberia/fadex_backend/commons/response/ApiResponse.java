package com.sistemabarberia.fadex_backend.commons.response;


import lombok.*;
import org.springframework.data.domain.Page;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class  ApiResponse<T> {

    private boolean success;
    private String message;
    private T data;
    private String timestamp;

    // ===== ÉXITO =====
    public static <T> ApiResponse<T> ok(String message, T data) {
        return ApiResponse.<T>builder()
                .success(true)
                .message(message)
                .data(data)
                .timestamp(LocalDateTime.now().toString())
                .build();
    }

    public static <T> ApiResponse<PageResponse<T>> ok(String message, Page<T> page) {
        return ApiResponse.<PageResponse<T>>builder()
                .success(true)
                .message(message)
                .data(PageResponse.of(page))
                .timestamp(LocalDateTime.now().toString())
                .build();
    }

    public static ApiResponse<Void> ok(String message) {
        return ApiResponse.<Void>builder()
                .success(true)
                .message(message)
                .data(null)
                .timestamp(LocalDateTime.now().toString())
                .build();
    }

    // ===== ERROR =====
    public static <T> ApiResponse<T> error(String message) {
        return ApiResponse.<T>builder()
                .success(false)
                .message(message)
                .data(null)
                .timestamp(LocalDateTime.now().toString())
                .build();
    }

    // Para errores de validación con detalle
    public static <T> ApiResponse<T> error(String message, T data) {
        return ApiResponse.<T>builder()
                .success(false)
                .message(message)
                .data(data)
                .timestamp(LocalDateTime.now().toString())
                .build();
    }
}