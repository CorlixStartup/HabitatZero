package br.com.gs.habitatzero.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "sensor_ambiente", indexes = {
        @Index(name = "idx_sensor_estufa_tipo", columnList = "estufa_id, tipo_sensor"),
        @Index(name = "idx_sensor_timestamp", columnList = "timestamp")
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SensorAmbiente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "tipo_sensor", nullable = false, length = 30)
    @Enumerated(EnumType.STRING)
    private TipoSensor tipoSensor;

    @Column(name = "valor_leitura", nullable = false)
    private Double valorLeitura;

    @Column(nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    private UnidadeMedida unidade;

    @Column(nullable = false)
    @Builder.Default
    private LocalDateTime timestamp = LocalDateTime.now();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "estufa_id", nullable = false)
    private Estufa estufa;

    public enum TipoSensor {
        OXIGENIO, UMIDADE_SOLO, RADIACAO_EXTERNA, TEMPERATURA
    }

    public enum UnidadeMedida {
        PERCENTUAL, MSV_HORA, CELSIUS
    }
}

