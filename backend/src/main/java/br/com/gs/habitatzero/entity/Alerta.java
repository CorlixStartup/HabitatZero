package br.com.gs.habitatzero.entity;

import br.com.gs.habitatzero.enums.CriticalLevel;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "tb_alerta")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Alerta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String type;

    @Column(nullable = false)
    private String message;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CriticalLevel criticalLevel;

    private LocalDateTime warningDate;

    @Column(nullable = false)
    private Boolean resolved;

    private String actionExecuted;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "estufa_id")
    private Estufa estufa;

}
