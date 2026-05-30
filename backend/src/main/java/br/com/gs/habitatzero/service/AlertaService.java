package br.com.gs.habitatzero.service;

import br.com.gs.habitatzero.dto.response.AlertaResponse;
import br.com.gs.habitatzero.entity.Alerta;
import br.com.gs.habitatzero.entity.Estufa;
import br.com.gs.habitatzero.entity.SensorAmbiente;
import br.com.gs.habitatzero.exception.ResourceNotFoundException;
import br.com.gs.habitatzero.repository.AlertaRepository;
import br.com.gs.habitatzero.repository.EstufaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AlertaService {

    private final AlertaRepository alertaRepository;
    private final EstufaRepository estufaRepository;

    public AlertaService(AlertaRepository alertaRepository, EstufaRepository estufaRepository) {
        this.alertaRepository = alertaRepository;
        this.estufaRepository = estufaRepository;
    }

    /**
     * Avalia uma leitura de sensor e dispara alerta se ultrapassar threshold.
     * Regra central do sistema — CT-02 do plano de testes.
     */
    @Transactional
    public boolean avaliarLeituraEDispararAlerta(SensorAmbiente leitura, Estufa estufa) {
        Double valor = leitura.getValorLeitura();
        SensorAmbiente.TipoSensor tipo = leitura.getTipoSensor();

        boolean alertaGerado = switch (tipo) {
            case OXIGENIO -> {
                if (valor < estufa.getThresholdOxigenioMin()) {
                    salvarAlerta(estufa, tipo, valor,
                            String.format("O₂ abaixo do limite: %.2f%% (mínimo: %.2f%%)",
                                    valor, estufa.getThresholdOxigenioMin()),
                            calcularSeveridadeOxigenio(valor, estufa.getThresholdOxigenioMin()));
                    yield true;
                }
                yield false;
            }
            case UMIDADE_SOLO -> {
                if (valor < estufa.getThresholdUmidadeMin()) {
                    salvarAlerta(estufa, tipo, valor,
                            String.format("Umidade do solo crítica: %.2f%% (mínimo: %.2f%%)",
                                    valor, estufa.getThresholdUmidadeMin()),
                            Alerta.SeveridadeAlerta.ATENCAO);
                    yield true;
                }
                yield false;
            }
            case RADIACAO_EXTERNA -> {
                if (valor > estufa.getThresholdRadiacaoMax()) {
                    salvarAlerta(estufa, tipo, valor,
                            String.format("Radiação externa elevada: %.2f mSv/h (máximo: %.2f mSv/h)",
                                    valor, estufa.getThresholdRadiacaoMax()),
                            calcularSeveridadeRadiacao(valor, estufa.getThresholdRadiacaoMax()));
                    yield true;
                }
                yield false;
            }
            case TEMPERATURA -> {
                if (valor > estufa.getThresholdTemperaturaMax()) {
                    salvarAlerta(estufa, tipo, valor,
                            String.format("Temperatura acima do limite: %.2f°C (máximo: %.2f°C)",
                                    valor, estufa.getThresholdTemperaturaMax()),
                            Alerta.SeveridadeAlerta.CRITICO);
                    yield true;
                }
                yield false;
            }
        };

        return alertaGerado;
    }

    @Transactional(readOnly = true)
    public List<AlertaResponse> listarAlertasAtivos() {
        return alertaRepository.findByResolvidoFalseOrderByCriadoEmDesc()
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<AlertaResponse> listarAlertasPorEstufa(Long estufaId) {
        return alertaRepository.findByEstufaIdOrderByCriadoEmDesc(estufaId)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public AlertaResponse resolverAlerta(Long alertaId) {
        Alerta alerta = alertaRepository.findById(alertaId)
                .orElseThrow(() -> new ResourceNotFoundException("Alerta não encontrado: " + alertaId));
        alerta.setResolvido(true);
        alerta.setResolvidoEm(LocalDateTime.now());
        return toResponse(alertaRepository.save(alerta));
    }

    // ── Métodos privados ───────────────────────────────────────────────────

    private void salvarAlerta(Estufa estufa, SensorAmbiente.TipoSensor tipo,
                              Double valor, String mensagem, Alerta.SeveridadeAlerta severidade) {
        Alerta alerta = Alerta.builder()
                .estufa(estufa)
                .tipoSensor(tipo)
                .valorRegistrado(valor)
                .mensagem(mensagem)
                .severidade(severidade)
                .build();
        alertaRepository.save(alerta);
    }

    private Alerta.SeveridadeAlerta calcularSeveridadeOxigenio(Double valor, Double threshold) {
        double desvio = threshold - valor;
        if (desvio >= 5.0) return Alerta.SeveridadeAlerta.EMERGENCIA;
        if (desvio >= 2.0) return Alerta.SeveridadeAlerta.CRITICO;
        return Alerta.SeveridadeAlerta.ATENCAO;
    }

    private Alerta.SeveridadeAlerta calcularSeveridadeRadiacao(Double valor, Double threshold) {
        if (valor >= threshold * 3) return Alerta.SeveridadeAlerta.EMERGENCIA;
        if (valor >= threshold * 2) return Alerta.SeveridadeAlerta.CRITICO;
        return Alerta.SeveridadeAlerta.ATENCAO;
    }

    private AlertaResponse toResponse(Alerta a) {
        return AlertaResponse.builder()
                .id(a.getId())
                .estufaId(a.getEstufa().getId())
                .nomeEstufa(a.getEstufa().getNome())
                .severidade(a.getSeveridade())
                .mensagem(a.getMensagem())
                .tipoSensor(a.getTipoSensor())
                .valorRegistrado(a.getValorRegistrado())
                .criadoEm(a.getCriadoEm())
                .resolvido(a.getResolvido())
                .resolvidoEm(a.getResolvidoEm())
                .build();
    }

}
