package example.ms_pacientes.controller;

import example.ms_pacientes.dto.PacienteRequestDTO;
import example.ms_pacientes.dto.PacienteResponseDTO;
import example.ms_pacientes.service.PacienteService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/pacientes")
@RequiredArgsConstructor
@Slf4j
public class PacienteController {

    private final PacienteService service;

    @GetMapping
    public ResponseEntity<List<PacienteResponseDTO>> listarTodos() {
        log.info("GET /api/pacientes");
        return ResponseEntity.ok(service.listarTodos());
    }

    @GetMapping("/activos")
    public ResponseEntity<List<PacienteResponseDTO>> listarActivos() {
        log.info("GET /api/pacientes/activos");
        return ResponseEntity.ok(service.listarActivos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<PacienteResponseDTO> buscarPorId(@PathVariable Long id) {
        log.info("GET /api/pacientes/{}", id);
        return ResponseEntity.ok(service.buscarPorId(id));
    }

    @GetMapping("/run/{run}")
    public ResponseEntity<PacienteResponseDTO> buscarPorRun(@PathVariable String run) {
        log.info("GET /api/pacientes/run/{}", run);
        return ResponseEntity.ok(service.buscarPorRun(run));
    }

    @PostMapping
    public ResponseEntity<PacienteResponseDTO> crear(@Valid @RequestBody PacienteRequestDTO dto) {
        log.info("POST /api/pacientes");
        PacienteResponseDTO response = service.crear(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<PacienteResponseDTO> actualizar(
            @PathVariable Long id,
            @Valid @RequestBody PacienteRequestDTO dto
    ) {
        log.info("PUT /api/pacientes/{}", id);
        return ResponseEntity.ok(service.actualizar(id, dto));
    }

    @PatchMapping("/{id}/activar")
    public ResponseEntity<PacienteResponseDTO> activar(@PathVariable Long id) {
        log.info("PATCH /api/pacientes/{}/activar", id);
        return ResponseEntity.ok(service.activar(id));
    }

    @PatchMapping("/{id}/desactivar")
    public ResponseEntity<PacienteResponseDTO> desactivar(@PathVariable Long id) {
        log.info("PATCH /api/pacientes/{}/desactivar", id);
        return ResponseEntity.ok(service.desactivar(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        log.info("DELETE /api/pacientes/{}", id);
        service.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}