package example.ms_fichas_clinicas.controller;

import example.ms_fichas_clinicas.dto.FichaClinicaRequestDTO;
import example.ms_fichas_clinicas.dto.FichaClinicaResponseDTO;
import example.ms_fichas_clinicas.service.FichaClinicaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/fichas-clinicas")
@RequiredArgsConstructor
@Slf4j
public class FichaClinicaController {

    private final FichaClinicaService service;

    @GetMapping
    public ResponseEntity<List<FichaClinicaResponseDTO>> listarTodas() {
        log.info("GET /api/fichas-clinicas");
        return ResponseEntity.ok(service.listarTodas());
    }

    @GetMapping("/activas")
    public ResponseEntity<List<FichaClinicaResponseDTO>> listarActivas() {
        log.info("GET /api/fichas-clinicas/activas");
        return ResponseEntity.ok(service.listarActivas());
    }

    @GetMapping("/{id}")
    public ResponseEntity<FichaClinicaResponseDTO> buscarPorId(@PathVariable Long id) {
        log.info("GET /api/fichas-clinicas/{}", id);
        return ResponseEntity.ok(service.buscarPorId(id));
    }

    @GetMapping("/cita/{citaId}")
    public ResponseEntity<FichaClinicaResponseDTO> buscarPorCita(@PathVariable Long citaId) {
        log.info("GET /api/fichas-clinicas/cita/{}", citaId);
        return ResponseEntity.ok(service.buscarPorCita(citaId));
    }

    @GetMapping("/paciente/{pacienteId}")
    public ResponseEntity<List<FichaClinicaResponseDTO>> listarPorPaciente(@PathVariable Long pacienteId) {
        log.info("GET /api/fichas-clinicas/paciente/{}", pacienteId);
        return ResponseEntity.ok(service.listarPorPaciente(pacienteId));
    }

    @GetMapping("/medico/{medicoId}")
    public ResponseEntity<List<FichaClinicaResponseDTO>> listarPorMedico(@PathVariable Long medicoId) {
        log.info("GET /api/fichas-clinicas/medico/{}", medicoId);
        return ResponseEntity.ok(service.listarPorMedico(medicoId));
    }

    @PostMapping
    public ResponseEntity<FichaClinicaResponseDTO> crear(@Valid @RequestBody FichaClinicaRequestDTO dto) {
        log.info("POST /api/fichas-clinicas");
        return ResponseEntity.status(HttpStatus.CREATED).body(service.crear(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<FichaClinicaResponseDTO> actualizar(
            @PathVariable Long id,
            @Valid @RequestBody FichaClinicaRequestDTO dto
    ) {
        log.info("PUT /api/fichas-clinicas/{}", id);
        return ResponseEntity.ok(service.actualizar(id, dto));
    }

    @PutMapping("/{id}/desactivar")
    public ResponseEntity<FichaClinicaResponseDTO> desactivar(@PathVariable Long id) {
        log.info("PUT /api/fichas-clinicas/{}/desactivar", id);
        return ResponseEntity.ok(service.desactivar(id));
    }

    @PutMapping("/{id}/activar")
    public ResponseEntity<FichaClinicaResponseDTO> activar(@PathVariable Long id) {
        log.info("PUT /api/fichas-clinicas/{}/activar", id);
        return ResponseEntity.ok(service.activar(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        log.info("DELETE /api/fichas-clinicas/{}", id);
        service.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
