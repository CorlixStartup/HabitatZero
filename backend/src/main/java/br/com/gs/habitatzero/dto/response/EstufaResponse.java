package br.com.gs.habitatzero.dto.response;

import br.com.gs.habitatzero.entity.Estufa;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class EstufaResponse {
    private Long id;
    private String nome;
    private String localizacao;
    private Double capacidadeM2;
    private Estufa.StatusEstufa status;
    private Double thresholdOxigenioMin;
    private Double thresholdUmidadeMin;
    private Double thresholdRadiacaoMax;
    private Double thresholdTemperaturaMax;
    private long totalPlantas;
    private long alertasAtivos;
}
