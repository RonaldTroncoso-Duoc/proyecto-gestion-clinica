package example.ms_citas.service;

import example.ms_citas.client.HorarioClient;
import example.ms_citas.client.MedicoClient;
import example.ms_citas.client.PacienteClient;
import example.ms_citas.dto.CitaRequestDTO;
import example.ms_citas.dto.CitaResponseDTO;
import example.ms_citas.exception.CitaNotFoundException;
import example.ms_citas.model.Cita;
import example.ms_citas.repository.CitaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class CitaServiceImpl implements CitaService {

    private final CitaRepository repository;
    private final PacienteClient pacienteClient;
    private final MedicoClient medicoClient;
    private final HorarioClient horarioClient;

    private static final String ESTADO_AGENDADA = "AGENDADA";
    private static final String ESTADO_CANCELADA = "CANCELADA";
    private static final String ESTADO_REALIZADA = "REALIZADA";

    @Override
    public List<CitaResponseDTO> listarTodas() {
        log.info("Listando todas las citas");
        return repository.findAll()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public List<CitaResponseDTO> listarPorPaciente(Long pacienteId) {
        log.info("Listando citas del paciente ID: {}", pacienteId);
        pacienteClient.obtenerPaciente(pacienteId);

        return repository.findByPacienteId(pacienteId)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public List<CitaResponseDTO> listarPorMedico(Long medicoId) {
        log.info("Listando citas del médico ID: {}", medicoId);
        medicoClient.obtenerMedico(medicoId);

        return repository.findByMedicoId(medicoId)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public List<CitaResponseDTO> listarPorEstado(String estado) {
        log.info("Listando citas por estado: {}", estado);
        return repository.findByEstado(estado.toUpperCase())
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public CitaResponseDTO buscarPorId(Long id) {
        log.info("Buscando cita por ID: {}", id);
        return mapToResponse(obtenerEntidadPorId(id));
    }

    @Override
    public CitaResponseDTO crear(CitaRequestDTO dto) {
        log.info("Creando cita para paciente ID: {} y médico ID: {}", dto.getPacienteId(), dto.getMedicoId());

        pacienteClient.obtenerPaciente(dto.getPacienteId());
        medicoClient.obtenerMedico(dto.getMedicoId());
        horarioClient.obtenerHorario(dto.getHorarioId());

        if (repository.existsByHorarioIdAndEstado(dto.getHorarioId(), ESTADO_AGENDADA)) {
            log.warn("Intento de crear cita con horario ya agendado ID: {}", dto.getHorarioId());
            throw new RuntimeException("Ya existe una cita agendada para ese horario");
        }

        horarioClient.ocuparHorario(dto.getHorarioId());

        Cita cita = Cita.builder()
                .pacienteId(dto.getPacienteId())
                .medicoId(dto.getMedicoId())
                .horarioId(dto.getHorarioId())
                .motivo(dto.getMotivo().trim())
                .estado(ESTADO_AGENDADA)
                .fechaCreacion(LocalDateTime.now())
                .build();

        Cita guardada = repository.save(cita);

        log.info("Cita creada correctamente con ID: {}", guardada.getId());

        return mapToResponse(guardada);
    }

    @Override
    public CitaResponseDTO actualizar(Long id, CitaRequestDTO dto) {
        log.info("Actualizando cita ID: {}", id);

        Cita cita = obtenerEntidadPorId(id);

        if (!cita.getEstado().equals(ESTADO_AGENDADA)) {
            log.warn("Intento de actualizar cita no agendada ID: {}", id);
            throw new RuntimeException("Solo se pueden actualizar citas en estado AGENDADA");
        }

        pacienteClient.obtenerPaciente(dto.getPacienteId());
        medicoClient.obtenerMedico(dto.getMedicoId());
        horarioClient.obtenerHorario(dto.getHorarioId());

        boolean cambiaHorario = !cita.getHorarioId().equals(dto.getHorarioId());

        if (cambiaHorario) {
            if (repository.existsByHorarioIdAndEstado(dto.getHorarioId(), ESTADO_AGENDADA)) {
                log.warn("Intento de actualizar cita a horario ya ocupado ID: {}", dto.getHorarioId());
                throw new RuntimeException("Ya existe una cita agendada para el nuevo horario");
            }

            horarioClient.liberarHorario(cita.getHorarioId());
            horarioClient.ocuparHorario(dto.getHorarioId());
        }

        cita.setPacienteId(dto.getPacienteId());
        cita.setMedicoId(dto.getMedicoId());
        cita.setHorarioId(dto.getHorarioId());
        cita.setMotivo(dto.getMotivo().trim());

        Cita actualizada = repository.save(cita);

        log.info("Cita actualizada correctamente ID: {}", actualizada.getId());

        return mapToResponse(actualizada);
    }

    @Override
    public CitaResponseDTO cancelar(Long id) {
        log.info("Cancelando cita ID: {}", id);

        Cita cita = obtenerEntidadPorId(id);

        if (!cita.getEstado().equals(ESTADO_AGENDADA)) {
            log.warn("Intento de cancelar cita no agendada ID: {}", id);
            throw new RuntimeException("Solo se pueden cancelar citas en estado AGENDADA");
        }

        cita.setEstado(ESTADO_CANCELADA);
        horarioClient.liberarHorario(cita.getHorarioId());

        Cita actualizada = repository.save(cita);

        log.info("Cita cancelada correctamente ID: {}", id);

        return mapToResponse(actualizada);
    }

    @Override
    public CitaResponseDTO realizar(Long id) {
        log.info("Marcando cita como realizada ID: {}", id);

        Cita cita = obtenerEntidadPorId(id);

        if (!cita.getEstado().equals(ESTADO_AGENDADA)) {
            log.warn("Intento de realizar cita no agendada ID: {}", id);
            throw new RuntimeException("Solo se pueden realizar citas en estado AGENDADA");
        }

        cita.setEstado(ESTADO_REALIZADA);

        Cita actualizada = repository.save(cita);

        log.info("Cita realizada correctamente ID: {}", id);

        return mapToResponse(actualizada);
    }

    @Override
    public void eliminar(Long id) {
        log.info("Eliminando cita ID: {}", id);

        Cita cita = obtenerEntidadPorId(id);

        if (cita.getEstado().equals(ESTADO_AGENDADA)) {
            horarioClient.liberarHorario(cita.getHorarioId());
        }

        repository.delete(cita);

        log.info("Cita eliminada correctamente ID: {}", id);
    }

    private Cita obtenerEntidadPorId(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Cita no encontrada con ID: {}", id);
                    return new CitaNotFoundException("Cita con ID " + id + " no encontrada");
                });
    }

    private CitaResponseDTO mapToResponse(Cita cita) {
        return CitaResponseDTO.builder()
                .id(cita.getId())
                .pacienteId(cita.getPacienteId())
                .medicoId(cita.getMedicoId())
                .horarioId(cita.getHorarioId())
                .motivo(cita.getMotivo())
                .estado(cita.getEstado())
                .fechaCreacion(cita.getFechaCreacion())
                .build();
    }
}
