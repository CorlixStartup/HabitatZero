package br.com.gs.habitatzero.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "alerta")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Alerta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    private SeveridadeAlerta severidade;

    @Column(nullable = false, length = 255)
    private String mensagem;

    @Column(name = "tipo_sensor", length = 30)
    @Enumerated(EnumType.STRING)
    private SensorAmbiente.TipoSensor tipoSensor;

    @Column(name = "valor_registrado")
    private Double valorRegistrado;

    @Column(name = "criado_em", nullable = false)
    @Builder.Default
    private LocalDateTime criadoEm = LocalDateTime.now();

    @Column(name = "resolvido_em")
    private LocalDateTime resolvidoEm;

    @Column(nullable = false)
    @Builder.Default
    private Boolean resolvido = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "estufa_id", nullable = false)
    private Estufa estufa;

    public enum SeveridadeAlerta {
        ATENCAO, CRITICO, EMERGENCIA
    }
}
