package example.ms_notificaciones.service;

import example.ms_notificaciones.client.CitaClient;
import example.ms_notificaciones.client.PacienteClient;
import example.ms_notificaciones.dto.NotificacionRequestDTO;
import example.ms_notificaciones.dto.NotificacionResponseDTO;
import example.ms_notificaciones.exception.NotificacionNotFoundException;
import example.ms_notificaciones.model.Notificacion;
import example.ms_notificaciones.repository.NotificacionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificacionServiceImpl implements NotificacionService {

    private final NotificacionRepository repository;
    private final PacienteClient pacienteClient;
    private final CitaClient citaClient;

    private static final String ESTADO_PENDIENTE = "PENDIENTE";
    private static final String ESTADO_ENVIADA = "ENVIADA";
    private static final String ESTADO_FALLIDA = "FALLIDA";

    @Override
    public List<NotificacionResponseDTO> listarTodas() {

        log.info("Listando todas las notificaciones");

        return repository.findAll()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public List<NotificacionResponseDTO> listarPorPaciente(Long pacienteId) {

        log.info("Listando notificaciones del paciente ID: {}", pacienteId);

        pacienteClient.obtenerPaciente(pacienteId);

        return repository.findByPacienteId(pacienteId)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public List<NotificacionResponseDTO> listarPorEstado(String estado) {

        log.info("Listando notificaciones por estado: {}", estado);

        return repository.findByEstado(estado.toUpperCase())
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public List<NotificacionResponseDTO> listarPorTipo(String tipo) {

        log.info("Listando notificaciones por tipo: {}", tipo);

        return repository.findByTipo(tipo.toUpperCase())
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public NotificacionResponseDTO buscarPorId(Long id) {

        log.info("Buscando notificación ID: {}", id);

        return mapToResponse(obtenerEntidadPorId(id));
    }

    @Override
    public NotificacionResponseDTO crear(NotificacionRequestDTO dto) {

        log.info("Creando notificación para paciente ID: {}", dto.getPacienteId());

        pacienteClient.obtenerPaciente(dto.getPacienteId());
        citaClient.obtenerCita(dto.getCitaId());

        Notificacion notificacion = Notificacion.builder()
                .pacienteId(dto.getPacienteId())
                .citaId(dto.getCitaId())
                .tipo(dto.getTipo().trim().toUpperCase())
                .mensaje(dto.getMensaje().trim())
                .estado(ESTADO_PENDIENTE)
                .fechaEnvio(LocalDateTime.now())
                .build();

        Notificacion guardada = repository.save(notificacion);

        log.info("Notificación creada correctamente ID: {}", guardada.getId());

        return mapToResponse(guardada);
    }

    @Override
    public NotificacionResponseDTO marcarEnviada(Long id) {

        log.info("Marcando notificación como enviada ID: {}", id);

        Notificacion notificacion = obtenerEntidadPorId(id);

        notificacion.setEstado(ESTADO_ENVIADA);

        return mapToResponse(repository.save(notificacion));
    }

    @Override
    public NotificacionResponseDTO marcarFallida(Long id) {

        log.info("Marcando notificación como fallida ID: {}", id);

        Notificacion notificacion = obtenerEntidadPorId(id);

        notificacion.setEstado(ESTADO_FALLIDA);

        return mapToResponse(repository.save(notificacion));
    }

    @Override
    public void eliminar(Long id) {

        log.info("Eliminando notificación ID: {}", id);

        Notificacion notificacion = obtenerEntidadPorId(id);

        repository.delete(notificacion);

        log.info("Notificación eliminada correctamente ID: {}", id);
    }

    private Notificacion obtenerEntidadPorId(Long id) {

        return repository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Notificación no encontrada ID: {}", id);
                    return new NotificacionNotFoundException(
                            "Notificación con ID " + id + " no encontrada"
                    );
                });
    }

    private NotificacionResponseDTO mapToResponse(Notificacion notificacion) {

        return NotificacionResponseDTO.builder()
                .id(notificacion.getId())
                .pacienteId(notificacion.getPacienteId())
                .citaId(notificacion.getCitaId())
                .tipo(notificacion.getTipo())
                .mensaje(notificacion.getMensaje())
                .estado(notificacion.getEstado())
                .fechaEnvio(notificacion.getFechaEnvio())
                .build();
    }
}
