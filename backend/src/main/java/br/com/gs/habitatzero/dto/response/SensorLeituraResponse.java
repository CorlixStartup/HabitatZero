package br.com.gs.habitatzero.dto.response;

import br.com.gs.habitatzero.entity.SensorAmbiente;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class SensorLeituraResponse {
    private Long id;
    private Long estufaId;
    private String nomeEstufa;
    private SensorAmbiente.TipoSensor tipoSensor;
    private Double valorLeitura;
    private SensorAmbiente.UnidadeMedida unidade;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime timestamp;
    private boolean alertaDisparado;
}
