package example.ms_horarios.controller;

import example.ms_horarios.dto.HorarioRequestDTO;
import example.ms_horarios.dto.HorarioResponseDTO;
import example.ms_horarios.service.HorarioService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;

@RestController
@RequestMapping("/api/horarios")
@RequiredArgsConstructor
@Slf4j
public class HorarioController {

    private final HorarioService service;

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','DOCTOR')")
    public ResponseEntity<List<HorarioResponseDTO>> listarTodos() {
        log.info("GET /api/horarios");
        return ResponseEntity.ok(service.listarTodos());
    }

    @GetMapping("/disponibles")
    @PreAuthorize("hasAnyRole('ADMIN','DOCTOR','PACIENTE')")
    public ResponseEntity<List<HorarioResponseDTO>> listarDisponibles() {
        log.info("GET /api/horarios/disponibles");
        return ResponseEntity.ok(service.listarDisponibles());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','DOCTOR')")
    public ResponseEntity<HorarioResponseDTO> buscarPorId(@PathVariable Long id) {
        log.info("GET /api/horarios/{}", id);
        return ResponseEntity.ok(service.buscarPorId(id));
    }

    @GetMapping("/medico/{medicoId}")
    @PreAuthorize("hasAnyRole('ADMIN','DOCTOR')")
    public ResponseEntity<List<HorarioResponseDTO>> listarPorMedico(@PathVariable Long medicoId) {
        log.info("GET /api/horarios/medico/{}", medicoId);
        return ResponseEntity.ok(service.listarPorMedico(medicoId));
    }

    @GetMapping("/medico/{medicoId}/disponibles")
    @PreAuthorize("hasAnyRole('ADMIN','DOCTOR','PACIENTE')")
    public ResponseEntity<List<HorarioResponseDTO>> listarDisponiblesPorMedico(@PathVariable Long medicoId) {
        log.info("GET /api/horarios/medico/{}/disponibles", medicoId);
        return ResponseEntity.ok(service.listarDisponiblesPorMedico(medicoId));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','DOCTOR')")
    public ResponseEntity<HorarioResponseDTO> crear(@Valid @RequestBody HorarioRequestDTO dto) {
        log.info("POST /api/horarios");
        HorarioResponseDTO response = service.crear(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','DOCTOR')")
    public ResponseEntity<HorarioResponseDTO> actualizar(
            @PathVariable Long id,
            @Valid @RequestBody HorarioRequestDTO dto
    ) {
        log.info("PUT /api/horarios/{}", id);
        return ResponseEntity.ok(service.actualizar(id, dto));
    }

    @PutMapping("/{id}/ocupar")
    @PreAuthorize("hasAnyRole('ADMIN','DOCTOR')")
    public ResponseEntity<HorarioResponseDTO> ocupar(@PathVariable Long id) {
        log.info("PUT /api/horarios/{}/ocupar", id);
        return ResponseEntity.ok(service.ocupar(id));
    }

    @PutMapping("/{id}/liberar")
    @PreAuthorize("hasAnyRole('ADMIN','DOCTOR')")
    public ResponseEntity<HorarioResponseDTO> liberar(@PathVariable Long id) {
        log.info("PUT /api/horarios/{}/liberar", id);
        return ResponseEntity.ok(service.liberar(id));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        log.info("DELETE /api/horarios/{}", id);
        service.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
