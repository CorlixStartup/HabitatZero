package br.com.gs.habitatzero.service;

import br.com.gs.habitatzero.dto.request.ColonoRequest;
import br.com.gs.habitatzero.dto.response.ColonoResponse;
import br.com.gs.habitatzero.entity.Colono;
import br.com.gs.habitatzero.exception.BusinessException;
import br.com.gs.habitatzero.repository.ColonoRepository;
import br.com.gs.habitatzero.repository.EstufaRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ColonoServiceTest {

    @Mock
    private ColonoRepository colonoRepository;

    @Mock
    private EstufaRepository estufaRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private ColonoService colonoService;

    // TC-01: Criação de colono com dados válidos
    @Test
    void criar_comDadosValidos_deveCriarColono() {
        ColonoRequest request = new ColonoRequest();
        request.setNome("Joao Silva");
        request.setEmail("joao@habitat.com");
        request.setSenha("Senha@123");
        request.setCargo(Colono.CargoColono.AGRONOMISTA);

        Colono colonoSalvo = Colono.builder()
                .id(1L)
                .nome("Joao Silva")
                .email("joao@habitat.com")
                .senhaHash("hash_encoded")
                .cargo(Colono.CargoColono.AGRONOMISTA)
                .build();

        when(colonoRepository.existsByEmail("joao@habitat.com")).thenReturn(false);
        when(passwordEncoder.encode("Senha@123")).thenReturn("hash_encoded");
        when(colonoRepository.save(any(Colono.class))).thenReturn(colonoSalvo);

        ColonoResponse response = colonoService.criar(request);

        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals("joao@habitat.com", response.getEmail());
        verify(colonoRepository).save(any(Colono.class));
    }

    // TC-02: Criação de colono com e-mail já cadastrado deve lançar BusinessException
    @Test
    void criar_comEmailExistente_deveDispararBusinessException() {
        ColonoRequest request = new ColonoRequest();
        request.setNome("Maria Santos");
        request.setEmail("duplicado@habitat.com");
        request.setSenha("Senha@456");
        request.setCargo(Colono.CargoColono.MEDICO);

        when(colonoRepository.existsByEmail("duplicado@habitat.com")).thenReturn(true);

        assertThrows(BusinessException.class, () -> colonoService.criar(request));
        verify(colonoRepository, never()).save(any());
    }
}
