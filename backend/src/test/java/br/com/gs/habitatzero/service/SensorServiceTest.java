package br.com.gs.habitatzero.service;

import br.com.gs.habitatzero.dto.request.SensorLeituraRequest;
import br.com.gs.habitatzero.dto.response.SensorLeituraResponse;
import br.com.gs.habitatzero.entity.Estufa;
import br.com.gs.habitatzero.entity.SensorAmbiente;
import br.com.gs.habitatzero.repository.EstufaRepository;
import br.com.gs.habitatzero.repository.SensorAmbienteRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SensorServiceTest {

    @Mock
    private SensorAmbienteRepository sensorRepository;

    @Mock
    private EstufaRepository estufaRepository;

    @Mock
    private AlertaService alertaService;

    @InjectMocks
    private SensorService sensorService;

    // TC-05: Leitura de sensor dentro dos limites deve ser persistida sem disparar alerta
    @Test
    void registrarLeitura_comDadosValidos_devePersistirLeitura() {
        Estufa estufa = Estufa.builder()
                .id(1L)
                .nome("Estufa Beta")
                .localizacao("Setor B")
                .capacidadeM2(200.0)
                .status(Estufa.StatusEstufa.ATIVA)
                .thresholdOxigenioMin(19.5)
                .thresholdUmidadeMin(30.0)
                .thresholdRadiacaoMax(2.0)
                .thresholdTemperaturaMax(40.0)
                .build();

        SensorLeituraRequest request = new SensorLeituraRequest();
        request.setEstufaId(1L);
        request.setTipoSensor(SensorAmbiente.TipoSensor.OXIGENIO);
        request.setValorLeitura(21.0); // acima do mínimo de 19.5%
        request.setUnidade(SensorAmbiente.UnidadeMedida.PERCENTUAL);

        SensorAmbiente leituraSalva = SensorAmbiente.builder()
                .id(10L)
                .estufa(estufa)
                .tipoSensor(SensorAmbiente.TipoSensor.OXIGENIO)
                .valorLeitura(21.0)
                .unidade(SensorAmbiente.UnidadeMedida.PERCENTUAL)
                .build();

        when(estufaRepository.findById(1L)).thenReturn(Optional.of(estufa));
        when(sensorRepository.save(any(SensorAmbiente.class))).thenReturn(leituraSalva);
        when(alertaService.avaliarLeituraEDispararAlerta(any(), any())).thenReturn(false);

        SensorLeituraResponse response = sensorService.registrarLeitura(request);

        assertNotNull(response);
        assertEquals(21.0, response.getValorLeitura());
        assertFalse(response.isAlertaDisparado());
        verify(sensorRepository).save(any(SensorAmbiente.class));
        verify(alertaService).avaliarLeituraEDispararAlerta(any(), any());
    }
}
