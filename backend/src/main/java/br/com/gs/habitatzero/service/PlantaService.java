package br.com.gs.habitatzero.service;

import br.com.gs.habitatzero.dto.request.PlantaRequest;
import br.com.gs.habitatzero.dto.response.PlantaResponse;
import br.com.gs.habitatzero.entity.Estufa;
import br.com.gs.habitatzero.entity.Planta;
import br.com.gs.habitatzero.exception.ResourceNotFoundException;
import br.com.gs.habitatzero.repository.EstufaRepository;
import br.com.gs.habitatzero.repository.PlantaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class PlantaService {

    private final PlantaRepository plantaRepository;
    private final EstufaRepository estufaRepository;

    public PlantaService(PlantaRepository plantaRepository, EstufaRepository estufaRepository) {
        this.plantaRepository = plantaRepository;
        this.estufaRepository = estufaRepository;
    }

    @Transactional(readOnly = true)
    public List<PlantaResponse> listarTodas(Long estufaId) {
        List<Planta> plantas = estufaId != null
                ? plantaRepository.findByEstufaId(estufaId)
                : plantaRepository.findAll();
        return plantas.stream().map(this::toResponse).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public PlantaResponse buscarPorId(Long id) {
        return toResponse(findOrThrow(id));
    }

    @Transactional
    public PlantaResponse criar(PlantaRequest request) {
        Estufa estufa = estufaRepository.findById(request.getEstufaId())
                .orElseThrow(() -> new ResourceNotFoundException("Estufa não encontrada: " + request.getEstufaId()));

        Planta planta = Planta.builder()
                .nomeCientifico(request.getNomeCientifico())
                .nomeComum(request.getNomeComum())
                .faseCrescimento(request.getFaseCrescimento())
                .dataPlantio(request.getDataPlantio())
                .estufa(estufa)
                .build();

        return toResponse(plantaRepository.save(planta));
    }

    @Transactional
    public PlantaResponse atualizar(Long id, PlantaRequest request) {
        Planta planta = findOrThrow(id);
        Estufa estufa = estufaRepository.findById(request.getEstufaId())
                .orElseThrow(() -> new ResourceNotFoundException("Estufa não encontrada: " + request.getEstufaId()));

        planta.setNomeCientifico(request.getNomeCientifico());
        planta.setNomeComum(request.getNomeComum());
        planta.setFaseCrescimento(request.getFaseCrescimento());
        planta.setDataPlantio(request.getDataPlantio());
        planta.setEstufa(estufa);
        return toResponse(plantaRepository.save(planta));
    }

    @Transactional
    public void deletar(Long id) {
        findOrThrow(id);
        plantaRepository.deleteById(id);
    }

    private Planta findOrThrow(Long id) {
        return plantaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Planta não encontrada: " + id));
    }

    private PlantaResponse toResponse(Planta p) {
        return PlantaResponse.builder()
                .id(p.getId())
                .nomeCientifico(p.getNomeCientifico())
                .nomeComum(p.getNomeComum())
                .faseCrescimento(p.getFaseCrescimento())
                .dataPlantio(p.getDataPlantio())
                .estufaId(p.getEstufa().getId())
                .nomeEstufa(p.getEstufa().getNome())
                .build();
    }

}
