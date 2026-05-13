package example.ms_medicos.controller;

import java.util.List;

import example.ms_medicos.dto.MedicoRequestDTO;
import example.ms_medicos.dto.MedicoResponseDTO;
import example.ms_medicos.service.MedicoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/medicos")
@RequiredArgsConstructor
@Slf4j
public class MedicoController {
    private final MedicoService service;

    @GetMapping
    public ResponseEntity<List<MedicoResponseDTO>> listarTodos() {
        log.info("GET /api/medicos");
        return ResponseEntity.ok(service.listarTodos());
    }

    @GetMapping("/activos")
    public ResponseEntity<List<MedicoResponseDTO>> listarActivos() {
        log.info("GET /api/medicos/activos");
        return ResponseEntity.ok(service.listarActivos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<MedicoResponseDTO> buscarPorId(@PathVariable Long id) {
        log.info("GET /api/medicos/{}", id);
        return ResponseEntity.ok(service.buscarPorId(id));
    }

    @GetMapping("/especialidad/{especialidadId}")
    public ResponseEntity<List<MedicoResponseDTO>> buscarPorEspecialidad(@PathVariable Long especialidadId) {
        log.info("GET /api/medicos/especialidad/{}", especialidadId);
        return ResponseEntity.ok(service.buscarPorEspecialidad(especialidadId));
    }

    @GetMapping("/especialidad/{especialidadId}/activos")
    public ResponseEntity<List<MedicoResponseDTO>> buscarActivosPorEspecialidad(@PathVariable Long especialidadId) {
        log.info("GET /api/medicos/especialidad/{}/activos", especialidadId);
        return ResponseEntity.ok(service.buscarActivosPorEspecialidad(especialidadId));
    }

    @PostMapping
    public ResponseEntity<MedicoResponseDTO> crear(@Valid @RequestBody MedicoRequestDTO dto) {
        log.info("POST /api/medicos");
        MedicoResponseDTO response = service.crear(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<MedicoResponseDTO> actualizar(
            @PathVariable Long id,
            @Valid @RequestBody MedicoRequestDTO dto
    ) {
        log.info("PUT /api/medicos/{}", id);
        return ResponseEntity.ok(service.actualizar(id, dto));
    }

    @PatchMapping("/{id}/activar")
    public ResponseEntity<MedicoResponseDTO> activar(@PathVariable Long id) {
        log.info("PATCH /api/medicos/{}/activar", id);
        return ResponseEntity.ok(service.activar(id));
    }

    @PatchMapping("/{id}/desactivar")
    public ResponseEntity<MedicoResponseDTO> desactivar(@PathVariable Long id) {
        log.info("PATCH /api/medicos/{}/desactivar", id);
        return ResponseEntity.ok(service.desactivar(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        log.info("DELETE /api/medicos/{}", id);
        service.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}