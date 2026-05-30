package br.com.gs.habitatzero.dto.response;

import br.com.gs.habitatzero.entity.Planta;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder
public class PlantaResponse {
    private Long id;
    private String nomeCientifico;
    private String nomeComum;
    private Planta.FaseCrescimento faseCrescimento;
    private LocalDate dataPlantio;
    private Long estufaId;
    private String nomeEstufa;
}
