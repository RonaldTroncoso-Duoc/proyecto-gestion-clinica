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
import org.springframework.security.access.prepost.PreAuthorize;
import java.util.List;

@RestController
@RequestMapping("/api/pacientes")
@RequiredArgsConstructor
@Slf4j
public class PacienteController {

    private final PacienteService service;

    @PostMapping("/register")
    public ResponseEntity<PacienteResponseDTO> register(@Valid @RequestBody PacienteRequestDTO dto) {
        log.info("POST /api/pacientes/register para RUN: {}", dto.getRun());
        PacienteResponseDTO response = service.registrar(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PreAuthorize("hasAnyRole('ADMIN','DOCTOR')")
    @GetMapping
    public ResponseEntity<List<PacienteResponseDTO>> listarTodos() {
        log.info("GET /api/pacientes");
        return ResponseEntity.ok(service.listarTodos());
    }

    @PreAuthorize("hasAnyRole('ADMIN','DOCTOR')")
    @GetMapping("/activos")
    public ResponseEntity<List<PacienteResponseDTO>> listarActivos() {
        log.info("GET /api/pacientes/activos");
        return ResponseEntity.ok(service.listarActivos());
    }

    @PreAuthorize("hasAnyRole('ADMIN','DOCTOR')")
    @GetMapping("/{id}")
    public ResponseEntity<PacienteResponseDTO> buscarPorId(@PathVariable Long id) {
        log.info("GET /api/pacientes/{}", id);
        return ResponseEntity.ok(service.buscarPorId(id));
    }

    @PreAuthorize("hasAnyRole('ADMIN','DOCTOR')")
    @GetMapping("/run/{run}")
    public ResponseEntity<PacienteResponseDTO> buscarPorRun(@PathVariable String run) {
        log.info("GET /api/pacientes/run/{}", run);
        return ResponseEntity.ok(service.buscarPorRun(run));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<PacienteResponseDTO> crear(@Valid @RequestBody PacienteRequestDTO dto) {
        log.info("POST /api/pacientes");
        PacienteResponseDTO response = service.crear(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<PacienteResponseDTO> actualizar(
            @PathVariable Long id,
            @Valid @RequestBody PacienteRequestDTO dto
    ) {
        log.info("PUT /api/pacientes/{}", id);
        return ResponseEntity.ok(service.actualizar(id, dto));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/{id}/activar")
    public ResponseEntity<PacienteResponseDTO> activar(@PathVariable Long id) {
        log.info("PATCH /api/pacientes/{}/activar", id);
        return ResponseEntity.ok(service.activar(id));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/{id}/desactivar")
    public ResponseEntity<PacienteResponseDTO> desactivar(@PathVariable Long id) {
        log.info("PATCH /api/pacientes/{}/desactivar", id);
        return ResponseEntity.ok(service.desactivar(id));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        log.info("DELETE /api/pacientes/{}", id);
        service.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}