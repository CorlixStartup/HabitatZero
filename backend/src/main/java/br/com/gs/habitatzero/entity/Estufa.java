package br.com.gs.habitatzero.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Table(name = "estufa")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Estufa {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String nome;

    @Column(nullable = false, length = 200)
    private String localizacao;

    @Column(name = "capacidade_m2", nullable = false)
    private Double capacidadeM2;

    @Column(nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private StatusEstufa status = StatusEstufa.ATIVA;

    // Thresholds personalizáveis por estufa
    @Column(name = "threshold_oxigenio_min")
    @Builder.Default
    private Double thresholdOxigenioMin = 19.5;

    @Column(name = "threshold_umidade_min")
    @Builder.Default
    private Double thresholdUmidadeMin = 30.0;

    @Column(name = "threshold_radiacao_max")
    @Builder.Default
    private Double thresholdRadiacaoMax = 2.0;

    @Column(name = "threshold_temperatura_max")
    @Builder.Default
    private Double thresholdTemperaturaMax = 40.0;

    @OneToMany(mappedBy = "estufa", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Planta> plantas;

    @OneToMany(mappedBy = "estufa", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<SensorAmbiente> sensores;

    @OneToMany(mappedBy = "estufa", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Colono> colonos;

    @OneToMany(mappedBy = "estufa", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Alerta> alertas;

    public enum StatusEstufa {
        ATIVA, INATIVA, MANUTENCAO
    }
}

