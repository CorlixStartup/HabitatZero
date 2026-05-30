package br.com.gs.habitatzero.controller;

import br.com.gs.habitatzero.dto.request.PlantaRequest;
import br.com.gs.habitatzero.dto.response.PlantaResponse;
import br.com.gs.habitatzero.service.PlantaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/plantas")
@Tag(name = "Plantas", description = "Gerenciamento de plantas cultivadas")
@SecurityRequirement(name = "bearerAuth")
public class PlantaController {

    private final PlantaService plantaService;

    public PlantaController(PlantaService plantaService) {
        this.plantaService = plantaService;
    }

    @GetMapping
    @Operation(summary = "Listar plantas (filtro opcional por estufa)")
    public ResponseEntity<List<PlantaResponse>> listar(
            @RequestParam(required = false) Long estufaId) {
        return ResponseEntity.ok(plantaService.listarTodas(estufaId));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar planta por ID")
    public ResponseEntity<PlantaResponse> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(plantaService.buscarPorId(id));
    }

    @PostMapping
    @Operation(summary = "Adicionar planta a uma estufa")
    public ResponseEntity<PlantaResponse> criar(@Valid @RequestBody PlantaRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(plantaService.criar(request));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar dados da planta")
    public ResponseEntity<PlantaResponse> atualizar(@PathVariable Long id,
                                                     @Valid @RequestBody PlantaRequest request) {
        return ResponseEntity.ok(plantaService.atualizar(id, request));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Remover planta")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        plantaService.deletar(id);
        return ResponseEntity.noContent().build();
    }
}
