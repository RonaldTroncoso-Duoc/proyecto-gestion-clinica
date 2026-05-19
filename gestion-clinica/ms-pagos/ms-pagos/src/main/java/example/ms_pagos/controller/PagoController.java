package example.ms_pagos.controller;

import example.ms_pagos.dto.PagoRequestDTO;
import example.ms_pagos.dto.PagoResponseDTO;
import example.ms_pagos.service.PagoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;

@RestController
@RequestMapping("/api/pagos")
@RequiredArgsConstructor
@Slf4j
public class PagoController {

    private final PagoService service;

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<List<PagoResponseDTO>> listarTodos() {
        log.info("GET /api/pagos");
        return ResponseEntity.ok(service.listarTodos());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','PACIENTE')")
    public ResponseEntity<PagoResponseDTO> buscarPorId(@PathVariable Long id) {
        log.info("GET /api/pagos/{}", id);
        return ResponseEntity.ok(service.buscarPorId(id));
    }

    @GetMapping("/paciente/{pacienteId}")
    @PreAuthorize("hasAnyRole('ADMIN','PACIENTE')")
    public ResponseEntity<List<PagoResponseDTO>> listarPorPaciente(@PathVariable Long pacienteId) {
        log.info("GET /api/pagos/paciente/{}", pacienteId);
        return ResponseEntity.ok(service.listarPorPaciente(pacienteId));
    }

    @GetMapping("/estado/{estado}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<PagoResponseDTO>> listarPorEstado(@PathVariable String estado) {
        log.info("GET /api/pagos/estado/{}", estado);
        return ResponseEntity.ok(service.listarPorEstado(estado));
    }

    @GetMapping("/cita/{citaId}")
    @PreAuthorize("hasAnyRole('ADMIN','PACIENTE')")
    public ResponseEntity<PagoResponseDTO> buscarPorCita(@PathVariable Long citaId) {
        log.info("GET /api/pagos/cita/{}", citaId);
        return ResponseEntity.ok(service.buscarPorCita(citaId));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','PACIENTE')")
    public ResponseEntity<PagoResponseDTO> crear(@Valid @RequestBody PagoRequestDTO dto) {
        log.info("POST /api/pagos");
        PagoResponseDTO response = service.crear(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PagoResponseDTO> actualizar(
            @PathVariable Long id,
            @Valid @RequestBody PagoRequestDTO dto
    ) {
        log.info("PUT /api/pagos/{}", id);
        return ResponseEntity.ok(service.actualizar(id, dto));
    }

    @PutMapping("/{id}/confirmar")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PagoResponseDTO> confirmar(@PathVariable Long id) {
        log.info("PUT /api/pagos/{}/confirmar", id);
        return ResponseEntity.ok(service.confirmar(id));
    }

    @PutMapping("/{id}/anular")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PagoResponseDTO> anular(@PathVariable Long id) {
        log.info("PUT /api/pagos/{}/anular", id);
        return ResponseEntity.ok(service.anular(id));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        log.info("DELETE /api/pagos/{}", id);
        service.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
