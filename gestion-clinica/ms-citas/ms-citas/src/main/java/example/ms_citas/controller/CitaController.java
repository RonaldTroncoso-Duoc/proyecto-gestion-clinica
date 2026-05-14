package example.ms_citas.controller;

import example.ms_citas.dto.CitaRequestDTO;
import example.ms_citas.dto.CitaResponseDTO;
import example.ms_citas.service.CitaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/citas")
@RequiredArgsConstructor
@Slf4j
public class CitaController {

    private final CitaService service;

    @GetMapping
    public ResponseEntity<List<CitaResponseDTO>> listarTodas() {
        log.info("GET /api/citas");
        return ResponseEntity.ok(service.listarTodas());
    }

    @GetMapping("/{id}")
    public ResponseEntity<CitaResponseDTO> buscarPorId(@PathVariable Long id) {
        log.info("GET /api/citas/{}", id);
        return ResponseEntity.ok(service.buscarPorId(id));
    }

    @GetMapping("/paciente/{pacienteId}")
    public ResponseEntity<List<CitaResponseDTO>> listarPorPaciente(@PathVariable Long pacienteId) {
        log.info("GET /api/citas/paciente/{}", pacienteId);
        return ResponseEntity.ok(service.listarPorPaciente(pacienteId));
    }

    @GetMapping("/medico/{medicoId}")
    public ResponseEntity<List<CitaResponseDTO>> listarPorMedico(@PathVariable Long medicoId) {
        log.info("GET /api/citas/medico/{}", medicoId);
        return ResponseEntity.ok(service.listarPorMedico(medicoId));
    }

    @GetMapping("/estado/{estado}")
    public ResponseEntity<List<CitaResponseDTO>> listarPorEstado(@PathVariable String estado) {
        log.info("GET /api/citas/estado/{}", estado);
        return ResponseEntity.ok(service.listarPorEstado(estado));
    }

    @PostMapping
    public ResponseEntity<CitaResponseDTO> crear(@Valid @RequestBody CitaRequestDTO dto) {
        log.info("POST /api/citas");
        CitaResponseDTO response = service.crear(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<CitaResponseDTO> actualizar(
            @PathVariable Long id,
            @Valid @RequestBody CitaRequestDTO dto
    ) {
        log.info("PUT /api/citas/{}", id);
        return ResponseEntity.ok(service.actualizar(id, dto));
    }

    @PatchMapping("/{id}/cancelar")
    public ResponseEntity<CitaResponseDTO> cancelar(@PathVariable Long id) {
        log.info("PATCH /api/citas/{}/cancelar", id);
        return ResponseEntity.ok(service.cancelar(id));
    }

    @PatchMapping("/{id}/realizar")
    public ResponseEntity<CitaResponseDTO> realizar(@PathVariable Long id) {
        log.info("PATCH /api/citas/{}/realizar", id);
        return ResponseEntity.ok(service.realizar(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        log.info("DELETE /api/citas/{}", id);
        service.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}