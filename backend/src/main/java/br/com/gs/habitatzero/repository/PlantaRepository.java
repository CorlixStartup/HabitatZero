package br.com.gs.habitatzero.repository;

import br.com.gs.habitatzero.entity.Planta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PlantaRepository  extends JpaRepository<Planta, Long> {
    List<Planta> findByEstufaId(Long estufaId);
    long countByEstufaId(Long estufaId);
    List<Planta> findByFaseCrescimento(Planta.FaseCrescimento fase);
}
