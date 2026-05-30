package br.com.gs.habitatzero.dto.request;

import br.com.gs.habitatzero.entity.SensorAmbiente;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class SensorLeituraRequest {

    @NotNull(message = "ID da estufa é obrigatório")
    private Long estufaId;

    @NotNull(message = "Tipo do sensor é obrigatório")
    private SensorAmbiente.TipoSensor tipoSensor;

    // Validação de range físico — impede payloads maliciosos (CT-04)
    @NotNull(message = "Valor da leitura é obrigatório")
    @Min(value = 0, message = "Valor de leitura não pode ser negativo")
    @Max(value = 100000, message = "Valor de leitura fora do range físico aceitável")
    private Double valorLeitura;

    @NotNull(message = "Unidade de medida é obrigatória")
    private SensorAmbiente.UnidadeMedida unidade;

    // Opcional — se não enviado, o backend usa o momento do recebimento
    private LocalDateTime timestamp;
}
