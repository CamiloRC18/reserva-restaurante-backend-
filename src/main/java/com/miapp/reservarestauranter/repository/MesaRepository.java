package com.miapp.reservarestauranter.repository;

import org.springframework.data.domain.Example;
import org.springframework.data.jpa.repository.JpaRepository;
import com.miapp.reservarestauranter.model.Mesa;

import java.util.List;

public interface MesaRepository extends JpaRepository <Mesa, Long> {

    List<Mesa> findByCapacidadGreaterThanEqual (Integer capacidad);

}
