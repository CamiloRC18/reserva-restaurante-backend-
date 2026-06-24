package com.miapp.reservarestauranter.service;

import com.miapp.reservarestauranter.model.Mesa;
import com.miapp.reservarestauranter.model.Reserva;
import com.miapp.reservarestauranter.repository.MesaRepository;
import com.miapp.reservarestauranter.dto.MesaDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MesaService {

    private final MesaRepository mesaRepository;

    public Mesa crearmesa (Mesa masa) {
        return mesaRepository.save(masa);
    }

    public List<Mesa> listartodas() {
        return mesaRepository.findAll();
    }

    public Mesa Listarporid(Long id) {
        return mesaRepository.findById(id)
                .orElseThrow (() -> new RuntimeException("Mesa no encontrada con id " + id));
    }

    public List<MesaDTO> buscarDisponiblesPorCapacidad(Integer cantidad) {
        return mesaRepository.findByCantidad(cantidad).stream()
                .map(this::aDTO)
                .toList();
    }
    private MesaDTO aDTO(Mesa mesa) {
        return new MesaDTO(mesa.getId(), mesa.getNombre(), mesa.getCapacidad(), mesa.getActiva());
    }
}

