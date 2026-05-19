package example.ms_recetas.controller;

import example.ms_recetas.dto.RecetaRequestDTO;
import example.ms_recetas.dto.RecetaResponseDTO;
import example.ms_recetas.service.RecetaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;

@RestController
@RequestMapping("/api/recetas")
@RequiredArgsConstructor
@Slf4j
public class RecetaController {

    private final RecetaService service;

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','MEDICO')")
    public ResponseEntity<List<RecetaResponseDTO>> listarTodas() {
        log.info("GET /api/recetas");
        return ResponseEntity.ok(service.listarTodas());
    }

    @GetMapping("/activas")
    @PreAuthorize("hasAnyRole('ADMIN','MEDICO','PACIENTE')")
    public ResponseEntity<List<RecetaResponseDTO>> listarActivas() {
        log.info("GET /api/recetas/activas");
        return ResponseEntity.ok(service.listarActivas());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','MEDICO','PACIENTE')")
    public ResponseEntity<RecetaResponseDTO> buscarPorId(@PathVariable Long id) {
        log.info("GET /api/recetas/{}", id);
        return ResponseEntity.ok(service.buscarPorId(id));
    }

    @GetMapping("/paciente/{pacienteId}")
    @PreAuthorize("hasAnyRole('ADMIN','MEDICO','PACIENTE')")
    public ResponseEntity<List<RecetaResponseDTO>> listarPorPaciente(@PathVariable Long pacienteId) {
        log.info("GET /api/recetas/paciente/{}", pacienteId);
        return ResponseEntity.ok(service.listarPorPaciente(pacienteId));
    }

    @GetMapping("/medico/{medicoId}")
    @PreAuthorize("hasAnyRole('ADMIN','MEDICO')")
    public ResponseEntity<List<RecetaResponseDTO>> listarPorMedico(@PathVariable Long medicoId) {
        log.info("GET /api/recetas/medico/{}", medicoId);
        return ResponseEntity.ok(service.listarPorMedico(medicoId));
    }

    @GetMapping("/ficha-clinica/{fichaClinicaId}")
    @PreAuthorize("hasAnyRole('ADMIN','MEDICO')")    
    public ResponseEntity<List<RecetaResponseDTO>> listarPorFichaClinica(@PathVariable Long fichaClinicaId) {
        log.info("GET /api/recetas/ficha-clinica/{}", fichaClinicaId);
        return ResponseEntity.ok(service.listarPorFichaClinica(fichaClinicaId));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','MEDICO')")
    public ResponseEntity<RecetaResponseDTO> crear(@Valid @RequestBody RecetaRequestDTO dto) {
        log.info("POST /api/recetas");
        RecetaResponseDTO response = service.crear(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','MEDICO')")
    public ResponseEntity<RecetaResponseDTO> actualizar(
            @PathVariable Long id,
            @Valid @RequestBody RecetaRequestDTO dto
    ) {
        log.info("PUT /api/recetas/{}", id);
        return ResponseEntity.ok(service.actualizar(id, dto));
    }

    @PutMapping("/{id}/activar")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<RecetaResponseDTO> activar(@PathVariable Long id) {
        log.info("PUT /api/recetas/{}/activar", id);
        return ResponseEntity.ok(service.activar(id));
    }

    @PutMapping("/{id}/desactivar")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<RecetaResponseDTO> desactivar(@PathVariable Long id) {
        log.info("PUT /api/recetas/{}/desactivar", id);
        return ResponseEntity.ok(service.desactivar(id));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        log.info("DELETE /api/recetas/{}", id);
        service.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
