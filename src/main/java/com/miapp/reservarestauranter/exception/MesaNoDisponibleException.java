package com.miapp.reservarestauranter.exception;

public class MesaNoDisponibleException extends RuntimeException {
    public MesaNoDisponibleException(String mensaje) {
        super(mensaje);
    }
}
