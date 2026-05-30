package br.com.gs.habitatzero.service;

import br.com.gs.habitatzero.dto.request.SensorLeituraRequest;
import br.com.gs.habitatzero.dto.response.SensorLeituraResponse;
import br.com.gs.habitatzero.entity.Estufa;
import br.com.gs.habitatzero.entity.SensorAmbiente;
import br.com.gs.habitatzero.exception.ResourceNotFoundException;
import br.com.gs.habitatzero.repository.EstufaRepository;
import br.com.gs.habitatzero.repository.SensorAmbienteRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class SensorService {

    private final SensorAmbienteRepository sensorRepository;
    private final EstufaRepository estufaRepository;
    private final AlertaService alertaService;

    public SensorService(SensorAmbienteRepository sensorRepository,
                         EstufaRepository estufaRepository,
                         AlertaService alertaService) {
        this.sensorRepository = sensorRepository;
        this.estufaRepository = estufaRepository;
        this.alertaService = alertaService;
    }

    /**
     * Recebe leitura do ESP32, persiste e avalia alertas.
     */
    @Transactional
    public SensorLeituraResponse registrarLeitura(SensorLeituraRequest request) {
        Estufa estufa = estufaRepository.findById(request.getEstufaId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Estufa não encontrada: " + request.getEstufaId()));

        SensorAmbiente leitura = SensorAmbiente.builder()
                .estufa(estufa)
                .tipoSensor(request.getTipoSensor())
                .valorLeitura(request.getValorLeitura())
                .unidade(request.getUnidade())
                .timestamp(request.getTimestamp() != null ? request.getTimestamp() : LocalDateTime.now())
                .build();

        leitura = sensorRepository.save(leitura);

        boolean alertaDisparado = alertaService.avaliarLeituraEDispararAlerta(leitura, estufa);

        return toResponse(leitura, alertaDisparado);
    }

    @Transactional(readOnly = true)
    public List<SensorLeituraResponse> listarLeiturasRecentes(Long estufaId) {
        if (estufaId != null) {
            return sensorRepository.findByEstufaIdOrderByTimestampDesc(estufaId)
                    .stream()
                    .map(s -> toResponse(s, false))
                    .collect(Collectors.toList());
        }
        return sensorRepository.findAll().stream()
                .map(s -> toResponse(s, false))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<SensorLeituraResponse> listarLeiturasAbaixoThreshold(
            SensorAmbiente.TipoSensor tipo, Double threshold) {
        LocalDateTime desde = LocalDateTime.now().minusHours(24);
        return sensorRepository.findLeiturasAbaixoDoThreshold(tipo, threshold, desde)
                .stream()
                .map(s -> toResponse(s, false))
                .collect(Collectors.toList());
    }

    private SensorLeituraResponse toResponse(SensorAmbiente s, boolean alertaDisparado) {
        return SensorLeituraResponse.builder()
                .id(s.getId())
                .estufaId(s.getEstufa().getId())
                .nomeEstufa(s.getEstufa().getNome())
                .tipoSensor(s.getTipoSensor())
                .valorLeitura(s.getValorLeitura())
                .unidade(s.getUnidade())
                .timestamp(s.getTimestamp())
                .alertaDisparado(alertaDisparado)
                .build();
    }

}
