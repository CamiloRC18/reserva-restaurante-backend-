package com.miapp.reservarestauranter.controller;

import com.miapp.reservarestauranter.dto.ReservaRequestDTO;
import com.miapp.reservarestauranter.dto.ReservaResponseDTO;
import com.miapp.reservarestauranter.model.EstadoReserva;
import com.miapp.reservarestauranter.service.ReservaService;
import lombok.RequiredArgsConstructor;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/reservas")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")

public class ReservaController {
    private final ReservaService reservaService;

    @PostMapping
    public ResponseEntity <ReservaResponseDTO> crear(@Valid @RequestBody ReservaRequestDTO request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(reservaService.crear(request));
    }

    @GetMapping
    public List<ReservaResponseDTO> listarFecha(@RequestParam LocalDate fecha) {
        return reservaService.ListarReservasFechas(fecha);
    }

    @GetMapping("/{id}")
    public ReservaResponseDTO buscarReservaPorId(@PathVariable Long id) {
        return reservaService.ListarPorId(id);
    }

    @PatchMapping("/{id}/estado")
    public ReservaResponseDTO cambiarEstado(@PathVariable Long id, @RequestBody EstadoReserva nuevoEstado) {
        return reservaService.cambiarEstado(id, nuevoEstado);
    }

}
