package br.com.gs.habitatzero.service;

import br.com.gs.habitatzero.dto.request.EstufaRequest;
import br.com.gs.habitatzero.dto.response.EstufaResponse;
import br.com.gs.habitatzero.entity.Estufa;
import br.com.gs.habitatzero.exception.ResourceNotFoundException;
import br.com.gs.habitatzero.repository.AlertaRepository;
import br.com.gs.habitatzero.repository.EstufaRepository;
import br.com.gs.habitatzero.repository.PlantaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class EstufaService {

    private final EstufaRepository estufaRepository;
    private final AlertaRepository alertaRepository;
    private final PlantaRepository plantaRepository;

    public EstufaService(EstufaRepository estufaRepository,
                         AlertaRepository alertaRepository,
                         PlantaRepository plantaRepository) {
        this.estufaRepository = estufaRepository;
        this.alertaRepository = alertaRepository;
        this.plantaRepository = plantaRepository;
    }

    @Transactional(readOnly = true)
    public List<EstufaResponse> listarTodas() {
        return estufaRepository.findAll().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public EstufaResponse buscarPorId(Long id) {
        return toResponse(findOrThrow(id));
    }

    @Transactional
    public EstufaResponse criar(EstufaRequest request) {
        Estufa estufa = Estufa.builder()
                .nome(request.getNome())
                .localizacao(request.getLocalizacao())
                .capacidadeM2(request.getCapacidadeM2())
                .status(request.getStatus() != null ? request.getStatus() : Estufa.StatusEstufa.ATIVA)
                .thresholdOxigenioMin(request.getThresholdOxigenioMin() != null ? request.getThresholdOxigenioMin() : 19.5)
                .thresholdUmidadeMin(request.getThresholdUmidadeMin() != null ? request.getThresholdUmidadeMin() : 30.0)
                .thresholdRadiacaoMax(request.getThresholdRadiacaoMax() != null ? request.getThresholdRadiacaoMax() : 2.0)
                .thresholdTemperaturaMax(request.getThresholdTemperaturaMax() != null ? request.getThresholdTemperaturaMax() : 40.0)
                .build();
        return toResponse(estufaRepository.save(estufa));
    }

    @Transactional
    public EstufaResponse atualizar(Long id, EstufaRequest request) {
        Estufa estufa = findOrThrow(id);
        estufa.setNome(request.getNome());
        estufa.setLocalizacao(request.getLocalizacao());
        estufa.setCapacidadeM2(request.getCapacidadeM2());
        if (request.getStatus() != null) estufa.setStatus(request.getStatus());
        if (request.getThresholdOxigenioMin() != null)
            estufa.setThresholdOxigenioMin(request.getThresholdOxigenioMin());
        if (request.getThresholdUmidadeMin() != null) estufa.setThresholdUmidadeMin(request.getThresholdUmidadeMin());
        if (request.getThresholdRadiacaoMax() != null)
            estufa.setThresholdRadiacaoMax(request.getThresholdRadiacaoMax());
        if (request.getThresholdTemperaturaMax() != null)
            estufa.setThresholdTemperaturaMax(request.getThresholdTemperaturaMax());
        return toResponse(estufaRepository.save(estufa));
    }

    @Transactional
    public void deletar(Long id) {
        findOrThrow(id);
        estufaRepository.deleteById(id);
    }

    private Estufa findOrThrow(Long id) {
        return estufaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Estufa não encontrada: " + id));
    }

    private EstufaResponse toResponse(Estufa e) {
        long alertasAtivos = alertaRepository.countByEstufaIdAndResolvidoFalse(e.getId());
        long totalPlantas = plantaRepository.countByEstufaId(e.getId());

        return EstufaResponse.builder()
                .id(e.getId())
                .nome(e.getNome())
                .localizacao(e.getLocalizacao())
                .capacidadeM2(e.getCapacidadeM2())
                .status(e.getStatus())
                .thresholdOxigenioMin(e.getThresholdOxigenioMin())
                .thresholdUmidadeMin(e.getThresholdUmidadeMin())
                .thresholdRadiacaoMax(e.getThresholdRadiacaoMax())
                .thresholdTemperaturaMax(e.getThresholdTemperaturaMax())
                .totalPlantas(totalPlantas)
                .alertasAtivos(alertasAtivos)
                .build();
    }

}
