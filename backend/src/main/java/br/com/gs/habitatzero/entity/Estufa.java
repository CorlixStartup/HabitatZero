package br.com.gs.habitatzero.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "tb_estufa")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Estufa {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(nullable = false)
    private String name;

    private String location;

    @Min(10)
    @Max(40)
    private Double temperature;

    @Min(0)
    @Max(100)
    private Double humidity;

    @Min(0)
    @Max(100)
    private Double oxygen;

    @Min(0)
    @Max(1000)
    private Double radiation;

    @Column(nullable = false)
    private String status;

    @JsonIgnore
    @OneToMany(mappedBy = "estufa", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SensorAmbiente> sensores;

    @JsonIgnore
    @OneToMany(mappedBy = "estufa", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Alerta> alertas;

    @JsonIgnore
    @OneToMany(mappedBy = "estufa", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Equipamento> equipamentos;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

}
