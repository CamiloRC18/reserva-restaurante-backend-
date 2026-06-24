package com.miapp.reservarestauranter.repository;

import com.miapp.reservarestauranter.model.Reserva;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDate;
import java.util.List;

public interface ReservaRepository  extends JpaRepository<Reserva,Long> {

    List<Reserva> findByFecha(LocalDate fecha);

    List<Reserva> findByMesaIdAndFecha (Long mesa_id, LocalDate fecha) ;

    List<Reserva> findAllByCliente (String cliente);

    List<Reserva> findAllByEmail (String email);

}
