package br.com.gs.habitatzero.repository;

import br.com.gs.habitatzero.entity.SensorAmbiente;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SensorAmbienteRepository extends JpaRepository<SensorAmbiente, Long> {

    List<SensorAmbiente> findByEstufaId(Long id);

}
