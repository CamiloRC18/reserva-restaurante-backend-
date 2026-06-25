package com.miapp.reservarestauranter.service;

import com.miapp.reservarestauranter.dto.ReservaRequestDTO;
import com.miapp.reservarestauranter.dto.ReservaResponseDTO;
import com.miapp.reservarestauranter.exception.MesaNoDisponibleException;
import com.miapp.reservarestauranter.exception.RecursoNoEncontradoException;
import com.miapp.reservarestauranter.model.EstadoReserva;
import com.miapp.reservarestauranter.model.Mesa;
import com.miapp.reservarestauranter.model.Reserva;
import com.miapp.reservarestauranter.repository.ReservaRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReservaService {

    private static final Duration DURACION_RESERVA = Duration.ofHours(2);

    private final ReservaRepository reservaRepository;
    private final MesaService mesaService;

    @Transactional
    public ReservaResponseDTO crear(ReservaRequestDTO request) {
        Mesa mesa = mesaService.Listarporid(request.mesaId());

        if (request.numeroPersonas() > mesa.getCapacidad()) {
            throw new MesaNoDisponibleException ("La mesa tiene capacidad para " + mesa.getCapacidad() + " personas");
        }

        if (!hayDisponibilidad(request.mesaId(), request.fecha(), request.hora())) {
            throw new MesaNoDisponibleException ("La mesa ya está reservada en ese horario");
        }

        Reserva reserva = new Reserva();
        reserva.setMesa(mesa);
        reserva.setCliente(request.cliente());
        reserva.setTelefono(request.telefono());
        reserva.setEmail(request.email());
        reserva.setFecha(request.fecha());
        reserva.setHora(request.hora());
        reserva.setNumeroPersonas(request.numeroPersonas());
        reserva.setEstado(EstadoReserva.PENDIENTE);

        Reserva guardada = reservaRepository.save(reserva);

        return aDTO(guardada);
    }

    public ReservaResponseDTO ListarPorId(Long id){
        return aDTO(buscarEntidad(id));
    }

    public List<ReservaResponseDTO> ListarReservasFechas (LocalDate fecha) {
        return reservaRepository.findByFecha(fecha).stream().map(this::aDTO).toList();
    }

    public ReservaResponseDTO cambiarEstado(Long id, EstadoReserva nuevoEstado) {
        Reserva reserva = buscarEntidad(id);
        reserva.setEstado(nuevoEstado);
        return aDTO(reservaRepository.save(reserva));
    }

    //metodos privados
    private Reserva buscarEntidad(Long id) {
        return reservaRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Reserva no encontrada con id " + id));
    }

    private boolean hayDisponibilidad(Long mesaId, LocalDate fecha, LocalTime horaSolicitada) {
        List<Reserva> reservasDelDia = reservaRepository.findByMesaIdAndFecha(mesaId, fecha);

        LocalTime inicioNuevo = horaSolicitada;
        LocalTime finNuevo = horaSolicitada.plus(DURACION_RESERVA);

        for (Reserva existente : reservasDelDia) {
            LocalTime inicioExistente = existente.getHora();
            LocalTime finExistente = inicioExistente.plus(DURACION_RESERVA);

            boolean seSuperponen = inicioNuevo.isBefore(finExistente)
                    && inicioExistente.isBefore(finNuevo);

            if (seSuperponen) {
                return false;
            }
        }
        return true;
    }

    private ReservaResponseDTO aDTO(Reserva reserva) {
        return new ReservaResponseDTO(
                reserva.getId(),
                reserva.getMesa().getNombre(),
                reserva.getCliente(),
                reserva.getEmail(),
                reserva.getFecha(),
                reserva.getHora(),
                reserva.getNumeroPersonas(),
                reserva.getTelefono(),
                reserva.getEstado()
        );
    }
}