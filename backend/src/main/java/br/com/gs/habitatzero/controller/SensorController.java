package br.com.gs.habitatzero.controller;

import br.com.gs.habitatzero.dto.request.SensorLeituraRequest;
import br.com.gs.habitatzero.dto.response.SensorLeituraResponse;
import br.com.gs.habitatzero.service.SensorService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/sensores")
@Tag(name = "Sensores IoT", description = "Leituras dos sensores ESP32")
public class SensorController {

    private final SensorService sensorService;

    public SensorController(SensorService sensorService) {
        this.sensorService = sensorService;
    }

    @PostMapping("/leitura")
    @Operation(summary = "Receber leitura do ESP32 (endpoint IoT — sem autenticação)")
    public ResponseEntity<SensorLeituraResponse> registrarLeitura(
            @Valid @RequestBody SensorLeituraRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(sensorService.registrarLeitura(request));
    }

    @GetMapping("/leituras")
    @Operation(summary = "Consultar leituras (filtro opcional por estufa)")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<List<SensorLeituraResponse>> listar(
            @RequestParam(required = false) Long estufaId) {
        return ResponseEntity.ok(sensorService.listarLeiturasRecentes(estufaId));
    }
}
