package br.com.gs.habitatzero.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name = "planta")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Planta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "nome_cientifico", nullable = false, length = 150)
    private String nomeCientifico;

    @Column(name = "nome_comum", length = 100)
    private String nomeComum;

    @Column(name = "fase_crescimento", nullable = false, length = 50)
    @Enumerated(EnumType.STRING)
    private FaseCrescimento faseCrescimento;

    @Column(name = "data_plantio", nullable = false)
    private LocalDate dataPlantio;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "estufa_id", nullable = false)
    private Estufa estufa;

    public enum FaseCrescimento {
        SEMENTE, GERMINACAO, CRESCIMENTO, MATURACAO, COLHEITA
    }
}
