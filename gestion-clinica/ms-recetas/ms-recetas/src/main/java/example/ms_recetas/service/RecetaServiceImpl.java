package example.ms_recetas.service;

import example.ms_recetas.client.FichaClinicaClient;
import example.ms_recetas.client.MedicoClient;
import example.ms_recetas.client.PacienteClient;
import example.ms_recetas.dto.RecetaRequestDTO;
import example.ms_recetas.dto.RecetaResponseDTO;
import example.ms_recetas.exception.RecetaNotFoundException;
import example.ms_recetas.model.Receta;
import example.ms_recetas.repository.RecetaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class RecetaServiceImpl implements RecetaService {

    private final RecetaRepository repository;
    private final FichaClinicaClient fichaClinicaClient;
    private final PacienteClient pacienteClient;
    private final MedicoClient medicoClient;

    @Override
    public List<RecetaResponseDTO> listarTodas() {
        log.info("Listando todas las recetas");
        return repository.findAll()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public List<RecetaResponseDTO> listarActivas() {
        log.info("Listando recetas activas");
        return repository.findByActivaTrue()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public List<RecetaResponseDTO> listarPorPaciente(Long pacienteId) {
        log.info("Listando recetas del paciente ID: {}", pacienteId);
        pacienteClient.obtenerPaciente(pacienteId);

        return repository.findByPacienteId(pacienteId)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public List<RecetaResponseDTO> listarPorMedico(Long medicoId) {
        log.info("Listando recetas del médico ID: {}", medicoId);
        medicoClient.obtenerMedico(medicoId);

        return repository.findByMedicoId(medicoId)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public List<RecetaResponseDTO> listarPorFichaClinica(Long fichaClinicaId) {
        log.info("Listando recetas de la ficha clínica ID: {}", fichaClinicaId);
        fichaClinicaClient.obtenerFicha(fichaClinicaId);

        return repository.findByFichaClinicaId(fichaClinicaId)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public RecetaResponseDTO buscarPorId(Long id) {
        log.info("Buscando receta por ID: {}", id);
        Receta receta = obtenerEntidadPorId(id);
        return mapToResponse(receta);
    }

    @Override
    public RecetaResponseDTO crear(RecetaRequestDTO dto) {
        log.info("Creando receta para paciente ID: {}, médico ID: {}, ficha ID: {}",
                dto.getPacienteId(), dto.getMedicoId(), dto.getFichaClinicaId());

        fichaClinicaClient.obtenerFicha(dto.getFichaClinicaId());
        pacienteClient.obtenerPaciente(dto.getPacienteId());
        medicoClient.obtenerMedico(dto.getMedicoId());

        Receta receta = Receta.builder()
                .fichaClinicaId(dto.getFichaClinicaId())
                .pacienteId(dto.getPacienteId())
                .medicoId(dto.getMedicoId())
                .medicamento(dto.getMedicamento().trim())
                .dosis(dto.getDosis().trim())
                .indicaciones(dto.getIndicaciones().trim())
                .duracionDias(dto.getDuracionDias())
                .fechaEmision(LocalDateTime.now())
                .activa(true)
                .build();

        Receta guardada = repository.save(receta);

        log.info("Receta creada correctamente con ID: {}", guardada.getId());

        return mapToResponse(guardada);
    }

    @Override
    public RecetaResponseDTO actualizar(Long id, RecetaRequestDTO dto) {
        log.info("Actualizando receta ID: {}", id);

        Receta receta = obtenerEntidadPorId(id);

        fichaClinicaClient.obtenerFicha(dto.getFichaClinicaId());
        pacienteClient.obtenerPaciente(dto.getPacienteId());
        medicoClient.obtenerMedico(dto.getMedicoId());

        receta.setFichaClinicaId(dto.getFichaClinicaId());
        receta.setPacienteId(dto.getPacienteId());
        receta.setMedicoId(dto.getMedicoId());
        receta.setMedicamento(dto.getMedicamento().trim());
        receta.setDosis(dto.getDosis().trim());
        receta.setIndicaciones(dto.getIndicaciones().trim());
        receta.setDuracionDias(dto.getDuracionDias());

        Receta actualizada = repository.save(receta);

        log.info("Receta actualizada correctamente ID: {}", actualizada.getId());

        return mapToResponse(actualizada);
    }

    @Override
    public RecetaResponseDTO activar(Long id) {
        log.info("Activando receta ID: {}", id);

        Receta receta = obtenerEntidadPorId(id);
        receta.setActiva(true);

        Receta actualizada = repository.save(receta);

        log.info("Receta activada correctamente ID: {}", id);

        return mapToResponse(actualizada);
    }

    @Override
    public RecetaResponseDTO desactivar(Long id) {
        log.info("Desactivando receta ID: {}", id);

        Receta receta = obtenerEntidadPorId(id);
        receta.setActiva(false);

        Receta actualizada = repository.save(receta);

        log.info("Receta desactivada correctamente ID: {}", id);

        return mapToResponse(actualizada);
    }

    @Override
    public void eliminar(Long id) {
        log.info("Eliminando receta ID: {}", id);

        Receta receta = obtenerEntidadPorId(id);
        repository.delete(receta);

        log.info("Receta eliminada correctamente ID: {}", id);
    }

    private Receta obtenerEntidadPorId(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Receta no encontrada con ID: {}", id);
                    return new RecetaNotFoundException("Receta con ID " + id + " no encontrada");
                });
    }

    private RecetaResponseDTO mapToResponse(Receta receta) {
        return RecetaResponseDTO.builder()
                .id(receta.getId())
                .fichaClinicaId(receta.getFichaClinicaId())
                .pacienteId(receta.getPacienteId())
                .medicoId(receta.getMedicoId())
                .medicamento(receta.getMedicamento())
                .dosis(receta.getDosis())
                .indicaciones(receta.getIndicaciones())
                .duracionDias(receta.getDuracionDias())
                .fechaEmision(receta.getFechaEmision())
                .activa(receta.getActiva())
                .build();
    }
}
