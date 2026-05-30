package br.com.gs.habitatzero.controller;

import br.com.gs.habitatzero.dto.request.ColonoRequest;
import br.com.gs.habitatzero.dto.response.ColonoResponse;
import br.com.gs.habitatzero.service.ColonoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/colonos")
@Tag(name = "Colonos", description = "Gerenciamento de colonos (usuários)")
@SecurityRequirement(name = "bearerAuth")
public class ColonoController {

    private final ColonoService colonoService;

    public ColonoController(ColonoService colonoService) {
        this.colonoService = colonoService;
    }

    @GetMapping
    @Operation(summary = "Listar todos os colonos")
    public ResponseEntity<List<ColonoResponse>> listar() {
        return ResponseEntity.ok(colonoService.listarTodos());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar colono por ID")
    public ResponseEntity<ColonoResponse> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(colonoService.buscarPorId(id));
    }

    @PostMapping
    @Operation(summary = "Cadastrar novo colono")
    public ResponseEntity<ColonoResponse> criar(@Valid @RequestBody ColonoRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(colonoService.criar(request));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Remover colono")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        colonoService.deletar(id);
        return ResponseEntity.noContent().build();
    }
}
