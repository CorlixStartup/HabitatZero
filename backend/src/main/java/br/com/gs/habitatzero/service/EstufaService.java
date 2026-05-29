package br.com.gs.habitatzero.service;

import br.com.gs.habitatzero.entity.Estufa;
import br.com.gs.habitatzero.repository.EstufaRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EstufaService {

    private final EstufaRepository repository;

    public EstufaService(EstufaRepository estufaRepository) {
        this.repository = estufaRepository;
    }

    public List<Estufa> list() {
        return repository.findAll();
    }

    public Estufa findById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Estufa não encontrada"));
    }

    public Estufa save(Estufa estufa) {
        validateWeather(estufa);

        return repository.save(estufa);
    }

    private void validateWeather(Estufa estufa) {
        if (estufa.getOxygen() < 10) {
            throw new RuntimeException("Oxigênio crítico.");
        }
    }

}
