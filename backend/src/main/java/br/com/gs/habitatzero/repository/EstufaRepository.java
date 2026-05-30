package br.com.gs.habitatzero.repository;

import br.com.gs.habitatzero.entity.Estufa;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EstufaRepository extends JpaRepository<Estufa, Long> {
    List<Estufa> findByStatus(Estufa.StatusEstufa status);
}
