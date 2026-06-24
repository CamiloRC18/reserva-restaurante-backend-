package com.miapp.reservarestauranter.controller;
import com.miapp.reservarestauranter.model.Mesa;
import com.miapp.reservarestauranter.service.MesaService;


import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/mesas")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class MesaController {

    private final MesaService mesaService;

    @GetMapping
    public List<Mesa> listar() {
        return mesaService.listartodas();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Mesa> obtener(@PathVariable Long id) {
        return ResponseEntity.ok(mesaService.Listarporid(id));
    }
}
