package br.com.gs.habitatzero.dto.response;

import br.com.gs.habitatzero.entity.Colono;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ColonoResponse {
    private Long id;
    private String nome;
    private String email;
    private Colono.CargoColono cargo;
    private Long estufaId;
    private String nomeEstufa;
}
