package com.miapp.reservarestauranter.dto;

public record MesaDTO(
        Long id,
        String nombre,
        Integer capacidad,
        Boolean activa
) {

}
