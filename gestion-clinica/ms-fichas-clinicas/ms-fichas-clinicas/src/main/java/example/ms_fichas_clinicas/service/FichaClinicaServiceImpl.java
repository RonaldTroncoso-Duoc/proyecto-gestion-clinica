package example.ms_fichas_clinicas.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;

import example.ms_fichas_clinicas.client.CitaClient;
import example.ms_fichas_clinicas.client.MedicoClient;
import example.ms_fichas_clinicas.client.PacienteClient;
import example.ms_fichas_clinicas.dto.FichaClinicaRequestDTO;
import example.ms_fichas_clinicas.dto.FichaClinicaResponseDTO;
import example.ms_fichas_clinicas.exception.FichaClinicaNotFoundException;
import example.ms_fichas_clinicas.model.FichaClinica;
import example.ms_fichas_clinicas.repository.FichaClinicaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class FichaClinicaServiceImpl implements FichaClinicaService {

    private final FichaClinicaRepository repository;
    private final PacienteClient pacienteClient;
    private final MedicoClient medicoClient;
    private final CitaClient citaClient;

    @Override
    public List<FichaClinicaResponseDTO> listarTodas() {
        log.info("Listando todas las fichas clínicas");
        return repository.findAll().stream().map(this::mapToResponse).toList();
    }

    @Override
    public List<FichaClinicaResponseDTO> listarActivas() {
        log.info("Listando fichas clínicas activas");
        return repository.findByActivoTrue().stream().map(this::mapToResponse).toList();
    }

    @Override
    public List<FichaClinicaResponseDTO> listarPorPaciente(Long pacienteId) {
        log.info("Listando fichas clínicas del paciente ID: {}", pacienteId);
        pacienteClient.obtenerPaciente(pacienteId);
        return repository.findByPacienteId(pacienteId).stream().map(this::mapToResponse).toList();
    }

    @Override
    public List<FichaClinicaResponseDTO> listarPorMedico(Long medicoId) {
        log.info("Listando fichas clínicas del médico ID: {}", medicoId);
        medicoClient.obtenerMedico(medicoId);
        return repository.findByMedicoId(medicoId).stream().map(this::mapToResponse).toList();
    }

    @Override
    public FichaClinicaResponseDTO buscarPorId(Long id) {
        log.info("Buscando ficha clínica por ID: {}", id);
        return mapToResponse(obtenerEntidadPorId(id));
    }

    @Override
    public FichaClinicaResponseDTO buscarPorCita(Long citaId) {
        log.info("Buscando ficha clínica por cita ID: {}", citaId);

        FichaClinica ficha = repository.findByCitaId(citaId)
                .orElseThrow(() -> {
                    log.warn("Ficha clínica no encontrada para cita ID: {}", citaId);
                    return new FichaClinicaNotFoundException("Ficha clínica para cita ID " + citaId + " no encontrada");
                });

        return mapToResponse(ficha);
    }

    @Override
    public FichaClinicaResponseDTO crear(FichaClinicaRequestDTO dto) {
        log.info("Creando ficha clínica para cita ID: {}", dto.getCitaId());

        pacienteClient.obtenerPaciente(dto.getPacienteId());
        medicoClient.obtenerMedico(dto.getMedicoId());
        citaClient.obtenerCita(dto.getCitaId());

        if (repository.existsByCitaId(dto.getCitaId())) {
            log.warn("Intento de crear ficha duplicada para cita ID: {}", dto.getCitaId());
            throw new RuntimeException("Ya existe una ficha clínica registrada para esta cita");
        }

        FichaClinica ficha = FichaClinica.builder()
                .pacienteId(dto.getPacienteId())
                .medicoId(dto.getMedicoId())
                .citaId(dto.getCitaId())
                .motivoConsulta(dto.getMotivoConsulta().trim())
                .diagnostico(dto.getDiagnostico().trim())
                .tratamiento(dto.getTratamiento().trim())
                .observaciones(dto.getObservaciones())
                .fechaRegistro(LocalDateTime.now())
                .activo(true)
                .build();

        FichaClinica guardada = repository.save(ficha);

        log.info("Ficha clínica creada correctamente con ID: {}", guardada.getId());

        return mapToResponse(guardada);
    }

    @Override
    public FichaClinicaResponseDTO actualizar(Long id, FichaClinicaRequestDTO dto) {
        log.info("Actualizando ficha clínica ID: {}", id);

        FichaClinica ficha = obtenerEntidadPorId(id);

        pacienteClient.obtenerPaciente(dto.getPacienteId());
        medicoClient.obtenerMedico(dto.getMedicoId());
        citaClient.obtenerCita(dto.getCitaId());

        repository.findByCitaId(dto.getCitaId())
                .ifPresent(f -> {
                    if (!f.getId().equals(id)) {
                        log.warn("Intento de actualizar ficha con cita duplicada ID: {}", dto.getCitaId());
                        throw new RuntimeException("Ya existe otra ficha clínica registrada para esta cita");
                    }
                });

        ficha.setPacienteId(dto.getPacienteId());
        ficha.setMedicoId(dto.getMedicoId());
        ficha.setCitaId(dto.getCitaId());
        ficha.setMotivoConsulta(dto.getMotivoConsulta().trim());
        ficha.setDiagnostico(dto.getDiagnostico().trim());
        ficha.setTratamiento(dto.getTratamiento().trim());
        ficha.setObservaciones(dto.getObservaciones());

        FichaClinica actualizada = repository.save(ficha);

        log.info("Ficha clínica actualizada correctamente ID: {}", actualizada.getId());

        return mapToResponse(actualizada);
    }

    @Override
    public FichaClinicaResponseDTO desactivar(Long id) {
        log.info("Desactivando ficha clínica ID: {}", id);

        FichaClinica ficha = obtenerEntidadPorId(id);
        ficha.setActivo(false);

        return mapToResponse(repository.save(ficha));
    }

    @Override
    public FichaClinicaResponseDTO activar(Long id) {
        log.info("Activando ficha clínica ID: {}", id);

        FichaClinica ficha = obtenerEntidadPorId(id);
        ficha.setActivo(true);

        return mapToResponse(repository.save(ficha));
    }

    @Override
    public void eliminar(Long id) {
        log.info("Eliminando ficha clínica ID: {}", id);

        FichaClinica ficha = obtenerEntidadPorId(id);
        repository.delete(ficha);

        log.info("Ficha clínica eliminada correctamente ID: {}", id);
    }

    private FichaClinica obtenerEntidadPorId(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Ficha clínica no encontrada con ID: {}", id);
                    return new FichaClinicaNotFoundException("Ficha clínica con ID " + id + " no encontrada");
                });
    }

    private FichaClinicaResponseDTO mapToResponse(FichaClinica ficha) {
        return FichaClinicaResponseDTO.builder()
                .id(ficha.getId())
                .pacienteId(ficha.getPacienteId())
                .medicoId(ficha.getMedicoId())
                .citaId(ficha.getCitaId())
                .motivoConsulta(ficha.getMotivoConsulta())
                .diagnostico(ficha.getDiagnostico())
                .tratamiento(ficha.getTratamiento())
                .observaciones(ficha.getObservaciones())
                .fechaRegistro(ficha.getFechaRegistro())
                .activo(ficha.getActivo())
                .build();
    }
}
