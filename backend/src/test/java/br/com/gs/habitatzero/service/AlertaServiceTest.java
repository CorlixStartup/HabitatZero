package br.com.gs.habitatzero.service;

import br.com.gs.habitatzero.dto.response.AlertaResponse;
import br.com.gs.habitatzero.entity.Alerta;
import br.com.gs.habitatzero.entity.Estufa;
import br.com.gs.habitatzero.entity.SensorAmbiente;
import br.com.gs.habitatzero.repository.AlertaRepository;
import br.com.gs.habitatzero.repository.EstufaRepository;
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
class AlertaServiceTest {

    @Mock
    private AlertaRepository alertaRepository;

    @Mock
    private EstufaRepository estufaRepository;

    @InjectMocks
    private AlertaService alertaService;

    // TC-06: Leitura de O₂ abaixo do threshold deve criar um alerta
    @Test
    void avaliarLeitura_comOxigenioAbaixoThreshold_deveCriarAlerta() {
        Estufa estufa = Estufa.builder()
                .id(1L)
                .nome("Estufa Gama")
                .localizacao("Setor C")
                .capacidadeM2(300.0)
                .status(Estufa.StatusEstufa.ATIVA)
                .thresholdOxigenioMin(19.5)
                .thresholdUmidadeMin(30.0)
                .thresholdRadiacaoMax(2.0)
                .thresholdTemperaturaMax(40.0)
                .build();

        SensorAmbiente leitura = SensorAmbiente.builder()
                .id(5L)
                .estufa(estufa)
                .tipoSensor(SensorAmbiente.TipoSensor.OXIGENIO)
                .valorLeitura(17.0) // abaixo do mínimo de 19.5%
                .unidade(SensorAmbiente.UnidadeMedida.PERCENTUAL)
                .build();

        boolean alertaDisparado = alertaService.avaliarLeituraEDispararAlerta(leitura, estufa);

        assertTrue(alertaDisparado);
        verify(alertaRepository).save(any(Alerta.class));
    }

    // TC-07: Resolver alerta ativo deve marcar como resolvido com timestamp
    @Test
    void resolverAlerta_comAlertaAtivo_deveMarcarComoResolvido() {
        Estufa estufa = Estufa.builder()
                .id(1L)
                .nome("Estufa Delta")
                .localizacao("Setor D")
                .capacidadeM2(100.0)
                .status(Estufa.StatusEstufa.ATIVA)
                .thresholdOxigenioMin(19.5)
                .thresholdUmidadeMin(30.0)
                .thresholdRadiacaoMax(2.0)
                .thresholdTemperaturaMax(40.0)
                .build();

        Alerta alertaAtivo = Alerta.builder()
                .id(42L)
                .estufa(estufa)
                .severidade(Alerta.SeveridadeAlerta.CRITICO)
                .mensagem("O₂ abaixo do limite")
                .tipoSensor(SensorAmbiente.TipoSensor.OXIGENIO)
                .valorRegistrado(17.0)
                .resolvido(false)
                .build();

        when(alertaRepository.findById(42L)).thenReturn(Optional.of(alertaAtivo));
        when(alertaRepository.save(any(Alerta.class))).thenAnswer(inv -> inv.getArgument(0));

        AlertaResponse response = alertaService.resolverAlerta(42L);

        assertTrue(response.getResolvido());
        assertNotNull(response.getResolvidoEm());
        verify(alertaRepository).save(any(Alerta.class));
    }
}
