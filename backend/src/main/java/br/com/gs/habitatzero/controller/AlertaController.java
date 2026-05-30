package br.com.gs.habitatzero.controller;

import br.com.gs.habitatzero.dto.response.AlertaResponse;
import br.com.gs.habitatzero.service.AlertaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/alertas")
@Tag(name = "Alertas", description = "Alertas críticos dos sensores")
@SecurityRequirement(name = "bearerAuth")
public class AlertaController {

    private final AlertaService alertaService;

    public AlertaController(AlertaService alertaService) {
        this.alertaService = alertaService;
    }

    @GetMapping
    @Operation(summary = "Listar alertas ativos (não resolvidos)")
    public ResponseEntity<List<AlertaResponse>> listarAtivos() {
        return ResponseEntity.ok(alertaService.listarAlertasAtivos());
    }

    @GetMapping("/estufa/{estufaId}")
    @Operation(summary = "Listar alertas por estufa")
    public ResponseEntity<List<AlertaResponse>> listarPorEstufa(@PathVariable Long estufaId) {
        return ResponseEntity.ok(alertaService.listarAlertasPorEstufa(estufaId));
    }

    @PatchMapping("/{id}/resolver")
    @Operation(summary = "Marcar alerta como resolvido")
    public ResponseEntity<AlertaResponse> resolver(@PathVariable Long id) {
        return ResponseEntity.ok(alertaService.resolverAlerta(id));
    }
}
