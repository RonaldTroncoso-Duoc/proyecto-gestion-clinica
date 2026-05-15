package example.ms_notificaciones.controller;

import example.ms_notificaciones.dto.NotificacionRequestDTO;
import example.ms_notificaciones.dto.NotificacionResponseDTO;
import example.ms_notificaciones.service.NotificacionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notificaciones")
@RequiredArgsConstructor
@Slf4j
public class NotificacionController {

    private final NotificacionService service;

    @GetMapping
    public ResponseEntity<List<NotificacionResponseDTO>> listarTodas() {

        log.info("GET /api/notificaciones");

        return ResponseEntity.ok(service.listarTodas());
    }

    @GetMapping("/{id}")
    public ResponseEntity<NotificacionResponseDTO> buscarPorId(@PathVariable Long id) {

        log.info("GET /api/notificaciones/{}", id);

        return ResponseEntity.ok(service.buscarPorId(id));
    }

    @GetMapping("/paciente/{pacienteId}")
    public ResponseEntity<List<NotificacionResponseDTO>> listarPorPaciente(
            @PathVariable Long pacienteId
    ) {

        log.info("GET /api/notificaciones/paciente/{}", pacienteId);

        return ResponseEntity.ok(service.listarPorPaciente(pacienteId));
    }

    @GetMapping("/estado/{estado}")
    public ResponseEntity<List<NotificacionResponseDTO>> listarPorEstado(
            @PathVariable String estado
    ) {

        log.info("GET /api/notificaciones/estado/{}", estado);

        return ResponseEntity.ok(service.listarPorEstado(estado));
    }

    @GetMapping("/tipo/{tipo}")
    public ResponseEntity<List<NotificacionResponseDTO>> listarPorTipo(
            @PathVariable String tipo
    ) {

        log.info("GET /api/notificaciones/tipo/{}", tipo);

        return ResponseEntity.ok(service.listarPorTipo(tipo));
    }

    @PostMapping
    public ResponseEntity<NotificacionResponseDTO> crear(
            @Valid @RequestBody NotificacionRequestDTO dto
    ) {

        log.info("POST /api/notificaciones");

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(service.crear(dto));
    }

    @PutMapping("/{id}/enviada")
    public ResponseEntity<NotificacionResponseDTO> marcarEnviada(
            @PathVariable Long id
    ) {

        log.info("PUT /api/notificaciones/{}/enviada", id);

        return ResponseEntity.ok(service.marcarEnviada(id));
    }

    @PutMapping("/{id}/fallida")
    public ResponseEntity<NotificacionResponseDTO> marcarFallida(
            @PathVariable Long id
    ) {

        log.info("PUT /api/notificaciones/{}/fallida", id);

        return ResponseEntity.ok(service.marcarFallida(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {

        log.info("DELETE /api/notificaciones/{}", id);

        service.eliminar(id);

        return ResponseEntity.noContent().build();
    }
}
