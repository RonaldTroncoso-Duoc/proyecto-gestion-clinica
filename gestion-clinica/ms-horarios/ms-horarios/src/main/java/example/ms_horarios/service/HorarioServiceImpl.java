package example.ms_horarios.service;

import example.ms_horarios.client.MedicoClient;
import example.ms_horarios.dto.HorarioRequestDTO;
import example.ms_horarios.dto.HorarioResponseDTO;
import example.ms_horarios.exception.HorarioNotFoundException;
import example.ms_horarios.model.Horario;
import example.ms_horarios.repository.HorarioRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class HorarioServiceImpl implements HorarioService {

    private final HorarioRepository repository;
    private final MedicoClient medicoClient;

    @Override
    public List<HorarioResponseDTO> listarTodos() {
        log.info("Listando todos los horarios");
        return repository.findAll()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public List<HorarioResponseDTO> listarDisponibles() {
        log.info("Listando horarios disponibles");
        return repository.findByDisponibleTrue()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public List<HorarioResponseDTO> listarPorMedico(Long medicoId) {
        log.info("Listando horarios del médico ID: {}", medicoId);

        medicoClient.obtenerMedico(medicoId);

        return repository.findByMedicoId(medicoId)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public List<HorarioResponseDTO> listarDisponiblesPorMedico(Long medicoId) {
        log.info("Listando horarios disponibles del médico ID: {}", medicoId);

        medicoClient.obtenerMedico(medicoId);

        return repository.findByMedicoIdAndDisponibleTrue(medicoId)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public HorarioResponseDTO buscarPorId(Long id) {
        log.info("Buscando horario por ID: {}", id);
        return mapToResponse(obtenerEntidadPorId(id));
    }

    @Override
    public HorarioResponseDTO crear(HorarioRequestDTO dto) {
        log.info("Creando horario para médico ID: {}", dto.getMedicoId());

        medicoClient.obtenerMedico(dto.getMedicoId());

        validarRangoHorario(dto);

        boolean existe = repository.existsByMedicoIdAndFechaAndHoraInicioAndHoraFin(
                dto.getMedicoId(),
                dto.getFecha(),
                dto.getHoraInicio(),
                dto.getHoraFin()
        );

        if (existe) {
            log.warn("Intento de crear horario duplicado para médico ID: {}", dto.getMedicoId());
            throw new RuntimeException("Ya existe un horario registrado para ese médico en la misma fecha y hora");
        }

        Horario horario = Horario.builder()
                .medicoId(dto.getMedicoId())
                .fecha(dto.getFecha())
                .horaInicio(dto.getHoraInicio())
                .horaFin(dto.getHoraFin())
                .disponible(true)
                .build();

        Horario guardado = repository.save(horario);

        log.info("Horario creado correctamente con ID: {}", guardado.getId());

        return mapToResponse(guardado);
    }

    @Override
    public HorarioResponseDTO actualizar(Long id, HorarioRequestDTO dto) {
        log.info("Actualizando horario ID: {}", id);

        Horario horario = obtenerEntidadPorId(id);

        medicoClient.obtenerMedico(dto.getMedicoId());

        validarRangoHorario(dto);

        boolean existe = repository.existsByMedicoIdAndFechaAndHoraInicioAndHoraFin(
                dto.getMedicoId(),
                dto.getFecha(),
                dto.getHoraInicio(),
                dto.getHoraFin()
        );

        if (existe &&
                (!horario.getMedicoId().equals(dto.getMedicoId())
                        || !horario.getFecha().equals(dto.getFecha())
                        || !horario.getHoraInicio().equals(dto.getHoraInicio())
                        || !horario.getHoraFin().equals(dto.getHoraFin()))) {

            log.warn("Intento de actualizar horario a un bloque duplicado");
            throw new RuntimeException("Ya existe otro horario registrado para ese médico en la misma fecha y hora");
        }

        horario.setMedicoId(dto.getMedicoId());
        horario.setFecha(dto.getFecha());
        horario.setHoraInicio(dto.getHoraInicio());
        horario.setHoraFin(dto.getHoraFin());

        Horario actualizado = repository.save(horario);

        log.info("Horario actualizado correctamente con ID: {}", actualizado.getId());

        return mapToResponse(actualizado);
    }

    @Override
    public HorarioResponseDTO ocupar(Long id) {
        log.info("Marcando horario como ocupado ID: {}", id);

        Horario horario = obtenerEntidadPorId(id);

        if (!horario.getDisponible()) {
            log.warn("Intento de ocupar horario ya ocupado ID: {}", id);
            throw new RuntimeException("El horario ya se encuentra ocupado");
        }

        horario.setDisponible(false);

        Horario actualizado = repository.save(horario);

        log.info("Horario ocupado correctamente ID: {}", id);

        return mapToResponse(actualizado);
    }

    @Override
    public HorarioResponseDTO liberar(Long id) {
        log.info("Liberando horario ID: {}", id);

        Horario horario = obtenerEntidadPorId(id);
        horario.setDisponible(true);

        Horario actualizado = repository.save(horario);

        log.info("Horario liberado correctamente ID: {}", id);

        return mapToResponse(actualizado);
    }

    @Override
    public void eliminar(Long id) {
        log.info("Eliminando horario ID: {}", id);

        Horario horario = obtenerEntidadPorId(id);
        repository.delete(horario);

        log.info("Horario eliminado correctamente ID: {}", id);
    }

    private void validarRangoHorario(HorarioRequestDTO dto) {
        if (!dto.getHoraFin().isAfter(dto.getHoraInicio())) {
            log.warn("Rango horario inválido: inicio {} - fin {}", dto.getHoraInicio(), dto.getHoraFin());
            throw new RuntimeException("La hora de término debe ser posterior a la hora de inicio");
        }
    }

    private Horario obtenerEntidadPorId(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Horario no encontrado con ID: {}", id);
                    return new HorarioNotFoundException("Horario con ID " + id + " no encontrado");
                });
    }

    private HorarioResponseDTO mapToResponse(Horario horario) {
        return HorarioResponseDTO.builder()
                .id(horario.getId())
                .medicoId(horario.getMedicoId())
                .fecha(horario.getFecha())
                .horaInicio(horario.getHoraInicio())
                .horaFin(horario.getHoraFin())
                .disponible(horario.getDisponible())
                .build();
    }
}
