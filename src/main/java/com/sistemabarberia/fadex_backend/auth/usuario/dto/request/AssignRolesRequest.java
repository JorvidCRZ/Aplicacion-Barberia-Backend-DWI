package com.sistemabarberia.fadex_backend.auth.usuario.dto.request;

import jakarta.validation.constraints.NotEmpty;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AssignRolesRequest {

    @NotEmpty(message = "Debe enviar al menos un rol")
    private List<Integer> roles;
}