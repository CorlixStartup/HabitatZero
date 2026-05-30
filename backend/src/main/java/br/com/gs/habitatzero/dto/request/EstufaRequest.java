package br.com.gs.habitatzero.dto.request;

import br.com.gs.habitatzero.entity.Estufa;
import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class EstufaRequest {

    @NotBlank(message = "Nome da estufa é obrigatório")
    @Size(min = 2, max = 100, message = "Nome deve ter entre 2 e 100 caracteres")
    @Pattern(regexp = "^[^<>\"';&]*$", message = "Nome contém caracteres inválidos")
    private String nome;

    @NotBlank(message = "Localização é obrigatória")
    @Size(max = 200, message = "Localização deve ter no máximo 200 caracteres")
    @Pattern(regexp = "^[^<>\"';&]*$", message = "Localização contém caracteres inválidos")
    private String localizacao;

    @NotNull(message = "Capacidade é obrigatória")
    @Min(value = 1, message = "Capacidade mínima é 1 m²")
    @Max(value = 10000, message = "Capacidade máxima é 10.000 m²")
    private Double capacidadeM2;

    private Estufa.StatusEstufa status;

    // Thresholds personalizáveis
    @Min(value = 0, message = "Threshold de O₂ não pode ser negativo")
    @Max(value = 100, message = "Threshold de O₂ não pode exceder 100%")
    private Double thresholdOxigenioMin;

    @Min(value = 0, message = "Threshold de umidade não pode ser negativo")
    @Max(value = 100, message = "Threshold de umidade não pode exceder 100%")
    private Double thresholdUmidadeMin;

    @Min(value = 0, message = "Threshold de radiação não pode ser negativo")
    @Max(value = 1000, message = "Valor de radiação fora do range físico")
    private Double thresholdRadiacaoMax;

    @Min(value = -100, message = "Temperatura fora do range físico")
    @Max(value = 200, message = "Temperatura fora do range físico")
    private Double thresholdTemperaturaMax;
}
