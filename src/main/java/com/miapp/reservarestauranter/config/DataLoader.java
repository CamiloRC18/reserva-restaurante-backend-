package com.miapp.reservarestauranter.config;

import com.miapp.reservarestauranter.model.Mesa;
import com.miapp.reservarestauranter.repository.MesaRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataLoader implements CommandLineRunner {

    private final MesaRepository mesaRepository;

    public DataLoader(MesaRepository mesaRepository) {
        this.mesaRepository = mesaRepository;
    }

    @Override
    public void run(String... args) {
        if (mesaRepository.count() == 0) {
            mesaRepository.save(new Mesa(null, "Mesa 1", 2, true));
            mesaRepository.save(new Mesa(null, "Mesa 2", 2, true));
            mesaRepository.save(new Mesa(null, "Mesa 3", 4, true));
            mesaRepository.save(new Mesa(null, "Mesa 4", 4, true));
            mesaRepository.save(new Mesa(null, "Mesa VIP", 8, true));
        }
    }
}