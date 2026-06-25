package com.miapp.reservarestauranter.dto;
import com.miapp.reservarestauranter.model.EstadoReserva;

import java.time.LocalDate;
import java.time.LocalTime;

public record ReservaResponseDTO(
        Long id,
        String mesaNombre,
        String nombreCliente,
        String email,
        LocalDate fecha,
        LocalTime hora,
        Integer numeroPersonas,
        String telefono,
        EstadoReserva estado
) {
}
