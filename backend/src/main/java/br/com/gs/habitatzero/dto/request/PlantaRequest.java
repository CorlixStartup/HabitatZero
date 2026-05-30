package br.com.gs.habitatzero.dto.request;

import br.com.gs.habitatzero.entity.Planta;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDate;

@Data
public class PlantaRequest {

    @NotBlank(message = "Nome científico é obrigatório")
    @Size(max = 150, message = "Nome científico deve ter no máximo 150 caracteres")
    @Pattern(regexp = "^[^<>\"';&]*$", message = "Nome contém caracteres inválidos")
    private String nomeCientifico;

    @Size(max = 100, message = "Nome comum deve ter no máximo 100 caracteres")
    @Pattern(regexp = "^[^<>\"';&]*$", message = "Nome contém caracteres inválidos")
    private String nomeComum;

    @NotNull(message = "Fase de crescimento é obrigatória")
    private Planta.FaseCrescimento faseCrescimento;

    @NotNull(message = "Data de plantio é obrigatória")
    @PastOrPresent(message = "Data de plantio não pode ser futura")
    private LocalDate dataPlantio;

    @NotNull(message = "ID da estufa é obrigatório")
    private Long estufaId;
}
