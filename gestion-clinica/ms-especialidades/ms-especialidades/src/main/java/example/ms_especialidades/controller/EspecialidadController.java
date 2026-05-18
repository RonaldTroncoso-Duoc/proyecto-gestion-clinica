package example.ms_especialidades.controller;

import example.ms_especialidades.dto.EspecialidadRequestDTO;
import example.ms_especialidades.dto.EspecialidadResponseDTO;
import example.ms_especialidades.service.EspecialidadService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;

@RestController
@RequestMapping("/api/especialidades")
@RequiredArgsConstructor
@Slf4j
public class EspecialidadController {

    private final EspecialidadService service;

    @PreAuthorize("hasAnyRole('ADMIN','DOCTOR')")
    @GetMapping
    public ResponseEntity<List<EspecialidadResponseDTO>> listarTodas() {
        log.info("GET /api/especialidades");
        return ResponseEntity.ok(service.listarTodas());
    }

    @PreAuthorize("hasAnyRole('PATIENT','ADMIN')")
    @GetMapping("/activas")
    public ResponseEntity<List<EspecialidadResponseDTO>> listarActivas() {
        log.info("GET /api/especialidades/activas");
        return ResponseEntity.ok(service.listarActivas());
    }

    @PreAuthorize("hasAnyRole('ADMIN','DOCTOR')")
    @GetMapping("/{id}")
    public ResponseEntity<EspecialidadResponseDTO> buscarPorId(@PathVariable Long id) {
        log.info("GET /api/especialidades/{}", id);
        return ResponseEntity.ok(service.buscarPorId(id));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<EspecialidadResponseDTO> crear(@Valid @RequestBody EspecialidadRequestDTO dto) {
        log.info("POST /api/especialidades");
        EspecialidadResponseDTO response = service.crear(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<EspecialidadResponseDTO> actualizar(
            @PathVariable Long id,
            @Valid @RequestBody EspecialidadRequestDTO dto
    ) {
        log.info("PUT /api/especialidades/{}", id);
        return ResponseEntity.ok(service.actualizar(id, dto));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        log.info("DELETE /api/especialidades/{}", id);
        service.eliminar(id);
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/{id}/activar")
    public ResponseEntity<EspecialidadResponseDTO> activar(@PathVariable Long id) {
        log.info("PATCH /api/especialidades/{}/activar", id);
        return ResponseEntity.ok(service.activar(id));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/{id}/desactivar")
    public ResponseEntity<EspecialidadResponseDTO> desactivar(@PathVariable Long id) {
        log.info("PATCH /api/especialidades/{}/desactivar", id);
        return ResponseEntity.ok(service.desactivar(id));
    }
}