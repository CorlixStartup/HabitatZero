package br.com.gs.habitatzero.service;

import br.com.gs.habitatzero.dto.request.ColonoRequest;
import br.com.gs.habitatzero.dto.response.ColonoResponse;
import br.com.gs.habitatzero.entity.Colono;
import br.com.gs.habitatzero.entity.Estufa;
import br.com.gs.habitatzero.exception.BusinessException;
import br.com.gs.habitatzero.exception.ResourceNotFoundException;
import br.com.gs.habitatzero.repository.ColonoRepository;
import br.com.gs.habitatzero.repository.EstufaRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;


@Service
public class ColonoService {

    private final ColonoRepository colonoRepository;
    private final EstufaRepository estufaRepository;
    private final PasswordEncoder passwordEncoder;

    public ColonoService(ColonoRepository colonoRepository,
                         EstufaRepository estufaRepository,
                         PasswordEncoder passwordEncoder) {
        this.colonoRepository = colonoRepository;
        this.estufaRepository = estufaRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional(readOnly = true)
    public List<ColonoResponse> listarTodos() {
        return colonoRepository.findAll().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public ColonoResponse buscarPorId(Long id) {
        return toResponse(findOrThrow(id));
    }

    @Transactional
    public ColonoResponse criar(ColonoRequest request) {
        if (colonoRepository.existsByEmail(request.getEmail())) {
            throw new BusinessException("E-mail já cadastrado: " + request.getEmail());
        }

        Estufa estufa = null;
        if (request.getEstufaId() != null) {
            estufa = estufaRepository.findById(request.getEstufaId())
                    .orElseThrow(() -> new ResourceNotFoundException("Estufa não encontrada: " + request.getEstufaId()));
        }

        Colono colono = Colono.builder()
                .nome(request.getNome())
                .email(request.getEmail())
                .senhaHash(passwordEncoder.encode(request.getSenha()))
                .cargo(request.getCargo())
                .estufa(estufa)
                .build();

        return toResponse(colonoRepository.save(colono));
    }

    @Transactional
    public void deletar(Long id) {
        findOrThrow(id);
        colonoRepository.deleteById(id);
    }

    private Colono findOrThrow(Long id) {
        return colonoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Colono não encontrado: " + id));
    }

    private ColonoResponse toResponse(Colono c) {
        return ColonoResponse.builder()
                .id(c.getId())
                .nome(c.getNome())
                .email(c.getEmail())
                .cargo(c.getCargo())
                .estufaId(c.getEstufa() != null ? c.getEstufa().getId() : null)
                .nomeEstufa(c.getEstufa() != null ? c.getEstufa().getNome() : null)
                .build();
    }

}
