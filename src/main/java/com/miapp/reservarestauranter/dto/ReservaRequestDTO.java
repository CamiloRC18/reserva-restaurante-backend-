package com.miapp.reservarestauranter.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.time.LocalTime;

public record ReservaRequestDTO(
        @NotNull(message = "Debes indicar la mesa")
        Long mesaId,

        @NotBlank(message = "El nombre es obligatorio")
        String Cliente,

        @NotBlank(message = "El email es obligatorio")
        @Email(message = "El email no es válido")
        String email,

        Integer telefono,

        @NotNull(message = "La fecha es obligatoria")
        @FutureOrPresent(message = "La fecha no puede ser en el pasado")
        LocalDate fecha,

        @NotNull(message = "La hora es obligatoria")
        LocalTime hora,

        @NotNull(message = "Indica el número de personas")
        @Min(value = 1, message = "Debe ser al menos 1 persona")
        Integer numeroPersonas
) {
}
