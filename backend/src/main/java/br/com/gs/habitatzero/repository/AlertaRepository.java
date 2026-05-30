package br.com.gs.habitatzero.repository;

import br.com.gs.habitatzero.entity.Alerta;
import br.com.gs.habitatzero.entity.SensorAmbiente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AlertaRepository extends JpaRepository<Alerta, Long> {

    List<Alerta> findByResolvidoFalseOrderByCriadoEmDesc();
    List<Alerta> findByEstufaIdOrderByCriadoEmDesc(Long estufaId);
    List<Alerta> findByEstufaIdAndResolvidoFalse(Long estufaId);
    long countByEstufaIdAndResolvidoFalse(Long estufaId);
    boolean existsByEstufaIdAndTipoSensorAndResolvidoFalse(
            Long estufaId, SensorAmbiente.TipoSensor tipoSensor);

}
