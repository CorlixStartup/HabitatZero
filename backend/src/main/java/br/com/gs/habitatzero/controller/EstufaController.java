package br.com.gs.habitatzero.controller;

import br.com.gs.habitatzero.dto.request.EstufaRequest;
import br.com.gs.habitatzero.dto.response.EstufaResponse;
import br.com.gs.habitatzero.service.EstufaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/estufas")
@Tag(name = "Estufas", description = "Gerenciamento de estufas")
@SecurityRequirement(name = "bearerAuth")
public class EstufaController {

    private final EstufaService estufaService;

    public EstufaController(EstufaService estufaService) {
        this.estufaService = estufaService;
    }

    @GetMapping
    @Operation(summary = "Listar todas as estufas")
    public ResponseEntity<List<EstufaResponse>> listar() {
        return ResponseEntity.ok(estufaService.listarTodas());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar estufa por ID")
    public ResponseEntity<EstufaResponse> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(estufaService.buscarPorId(id));
    }

    @PostMapping
    @Operation(summary = "Cadastrar nova estufa")
    public ResponseEntity<EstufaResponse> criar(@Valid @RequestBody EstufaRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(estufaService.criar(request));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar configurações da estufa")
    public ResponseEntity<EstufaResponse> atualizar(@PathVariable Long id,
                                                     @Valid @RequestBody EstufaRequest request) {
        return ResponseEntity.ok(estufaService.atualizar(id, request));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Remover estufa do sistema")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        estufaService.deletar(id);
        return ResponseEntity.noContent().build();
    }
}
