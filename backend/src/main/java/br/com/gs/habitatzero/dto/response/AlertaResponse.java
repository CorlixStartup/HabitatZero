package br.com.gs.habitatzero.dto.response;

import br.com.gs.habitatzero.entity.Alerta;
import br.com.gs.habitatzero.entity.SensorAmbiente;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class AlertaResponse {
    private Long id;
    private Long estufaId;
    private String nomeEstufa;
    private Alerta.SeveridadeAlerta severidade;
    private String mensagem;
    private SensorAmbiente.TipoSensor tipoSensor;
    private Double valorRegistrado;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime criadoEm;
    private Boolean resolvido;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime resolvidoEm;
}
