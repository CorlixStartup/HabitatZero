package br.com.gs.habitatzero.repository;

import br.com.gs.habitatzero.entity.SensorAmbiente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface SensorAmbienteRepository extends JpaRepository<SensorAmbiente, Long> {

    List<SensorAmbiente> findByEstufaIdOrderByTimestampDesc(Long estufaId);

    List<SensorAmbiente> findByEstufaIdAndTipoSensorOrderByTimestampDesc(
            Long estufaId, SensorAmbiente.TipoSensor tipoSensor);

    // Leituras críticas de O2 nas últimas 24h (consulta SQL do enunciado)
    @Query("""
        SELECT s FROM SensorAmbiente s
        JOIN s.estufa e
        WHERE s.tipoSensor = :tipo
          AND s.valorLeitura < :threshold
          AND s.timestamp >= :desde
        ORDER BY s.timestamp DESC
    """)
    List<SensorAmbiente> findLeiturasAbaixoDoThreshold(
            @Param("tipo") SensorAmbiente.TipoSensor tipo,
            @Param("threshold") Double threshold,
            @Param("desde") LocalDateTime desde);

    // Última leitura de cada tipo por estufa
    @Query("""
        SELECT s FROM SensorAmbiente s
        WHERE s.estufa.id = :estufaId
          AND s.timestamp = (
              SELECT MAX(s2.timestamp) FROM SensorAmbiente s2
              WHERE s2.estufa.id = :estufaId AND s2.tipoSensor = s.tipoSensor
          )
    """)
    List<SensorAmbiente> findUltimasLeiturasParaEstufa(@Param("estufaId") Long estufaId);

}
