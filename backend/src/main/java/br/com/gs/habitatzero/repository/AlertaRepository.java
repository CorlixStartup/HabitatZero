package br.com.gs.habitatzero.repository;

import br.com.gs.habitatzero.entity.Alerta;
import br.com.gs.habitatzero.enums.CriticalLevel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AlertaRepository extends JpaRepository<Alerta, Long> {

    List<Alerta> findByCriticalLevel(CriticalLevel criticalLevel);

}
