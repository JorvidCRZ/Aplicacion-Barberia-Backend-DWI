package com.sistemabarberia.fadex_backend.modules.persona.dto.request;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class PersonaUpdateRequestDTO {

    @Size(max = 100, message = "El nombre no puede superar 100 caracteres")
    private String nombre;

    @Size(max = 100, message = "El apellido no puede superar 100 caracteres")
    private String apellido;

    @Pattern(
            regexp = "^[0-9]{9}$",
            message = "El teléfono debe tener 9 dígitos"
    )
    private String telefono;

    @Email(message = "El email no tiene un formato válido")
    private String email;
}