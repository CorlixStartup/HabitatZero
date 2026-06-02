package br.com.gs.habitatzero.service;

import br.com.gs.habitatzero.dto.request.EstufaRequest;
import br.com.gs.habitatzero.dto.response.EstufaResponse;
import br.com.gs.habitatzero.entity.Estufa;
import br.com.gs.habitatzero.exception.ResourceNotFoundException;
import br.com.gs.habitatzero.repository.AlertaRepository;
import br.com.gs.habitatzero.repository.EstufaRepository;
import br.com.gs.habitatzero.repository.PlantaRepository;
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
class EstufaServiceTest {

    @Mock
    private EstufaRepository estufaRepository;

    @Mock
    private AlertaRepository alertaRepository;

    @Mock
    private PlantaRepository plantaRepository;

    @InjectMocks
    private EstufaService estufaService;

    // TC-03: Criação de estufa com dados válidos aplica thresholds padrão
    @Test
    void criar_comDadosValidos_deveCriarEstufa() {
        EstufaRequest request = new EstufaRequest();
        request.setNome("Estufa Alpha");
        request.setLocalizacao("Setor A - Marte");
        request.setCapacidadeM2(500.0);

        Estufa estufaSalva = Estufa.builder()
                .id(1L)
                .nome("Estufa Alpha")
                .localizacao("Setor A - Marte")
                .capacidadeM2(500.0)
                .status(Estufa.StatusEstufa.ATIVA)
                .thresholdOxigenioMin(19.5)
                .thresholdUmidadeMin(30.0)
                .thresholdRadiacaoMax(2.0)
                .thresholdTemperaturaMax(40.0)
                .build();

        when(estufaRepository.save(any(Estufa.class))).thenReturn(estufaSalva);
        when(alertaRepository.countByEstufaIdAndResolvidoFalse(1L)).thenReturn(0L);
        when(plantaRepository.countByEstufaId(1L)).thenReturn(0L);

        EstufaResponse response = estufaService.criar(request);

        assertNotNull(response);
        assertEquals("Estufa Alpha", response.getNome());
        assertEquals(19.5, response.getThresholdOxigenioMin());
        assertEquals(Estufa.StatusEstufa.ATIVA, response.getStatus());
        verify(estufaRepository).save(any(Estufa.class));
    }

    // TC-04: Busca por ID inexistente deve lançar ResourceNotFoundException
    @Test
    void buscarPorId_comIdInexistente_deveDispararResourceNotFoundException() {
        when(estufaRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> estufaService.buscarPorId(999L));
    }
}
