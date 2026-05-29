package br.com.gs.habitatzero.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "tb_sensor_ambiente")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SensorAmbiente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String sensorType;

    @Min(-50)
    @Max(100)
    @Column(nullable = false)
    private Double value;

    @Column(nullable = false)
    private String unit;

    private LocalDateTime readingDate;

    private String sensorStatus;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "estufa_id")
    private Estufa estufa;

}
