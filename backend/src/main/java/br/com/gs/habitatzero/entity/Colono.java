package br.com.gs.habitatzero.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "colono", uniqueConstraints = {
        @UniqueConstraint(name = "uk_colono_email", columnNames = "email")
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Colono {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String nome;

    @Column(nullable = false, length = 150)
    private String email;

    @Column(name = "senha_hash", nullable = false)
    private String senhaHash;

    @Column(nullable = false, length = 50)
    @Enumerated(EnumType.STRING)
    private CargoColono cargo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "estufa_id")
    private Estufa estufa;

    public enum CargoColono {
        AGRONOMISTA, ENGENHEIRO, MEDICO, COMANDANTE, TECNICO
    }
}