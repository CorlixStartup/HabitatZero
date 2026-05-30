package br.com.gs.habitatzero.dto.response;

import br.com.gs.habitatzero.entity.Alerta;
import br.com.gs.habitatzero.entity.SensorAmbiente;
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
    private LocalDateTime criadoEm;
    private Boolean resolvido;
    private LocalDateTime resolvidoEm;
}
