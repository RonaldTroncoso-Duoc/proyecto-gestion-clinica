package example.ms_medicos.service;

import java.util.List;

import org.springframework.stereotype.Service;

import example.ms_medicos.client.EspecialidadClient;
import example.ms_medicos.dto.MedicoRequestDTO;
import example.ms_medicos.dto.MedicoResponseDTO;
import example.ms_medicos.exception.MedicoNotFoundException;
import example.ms_medicos.model.Medico;
import example.ms_medicos.repository.MedicoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class MedicoServiceImpl implements MedicoService{

    private final MedicoRepository repository;
    private final EspecialidadClient especialidadClient;

    @Override
    public List<MedicoResponseDTO> listarTodos() {
        log.info("Listando todos los médicos");
        return repository.findAll()
                        .stream()
                        .map(this::mapToResponse)
                        .toList();
    }

    @Override
    public List<MedicoResponseDTO> listarActivos() {
        log.info("Listando médicos activos");
        return repository.findByActivoTrue()
                        .stream()
                        .map(this::mapToResponse)
                        .toList();
    }

    @Override
    public MedicoResponseDTO buscarPorId(Long id) {
        log.info("Buscando médico por ID: {}", id);
        Medico medico = obtenerEntidadPorId(id);
        return mapToResponse(medico);
    }

    @Override
    public List<MedicoResponseDTO> buscarPorEspecialidad(Long especialidadId) {
        log.info("Buscando médicos por especialidad ID: {}", especialidadId);

        especialidadClient.obtenerEspecialidad(especialidadId); // Verificar que la especialidad exista
        return repository.findByEspecialidadId(especialidadId)
                                                    .stream()
                                                    .map(this::mapToResponse)
                                                    .toList();
    }

    @Override
    public List<MedicoResponseDTO> buscarActivosPorEspecialidad(Long especialidadId) {
        log.info("Buscando médicos activos por especialidad ID: {}", especialidadId);

        especialidadClient.obtenerEspecialidad(especialidadId);
        return repository.findByEspecialidadIdAndActivoTrue(especialidadId)
                                                                .stream()
                                                                .map(this::mapToResponse)
                                                                .toList();
    }

    @Override
public MedicoResponseDTO crear(MedicoRequestDTO dto) {

    log.info("Creando médico con RUN: {}", dto.getRun());

    // Validar que la especialidad exista
    especialidadClient.obtenerEspecialidad(dto.getEspecialidadId());

    // Validar RUN único
    if (repository.existsByRun(dto.getRun())) {
        log.warn("Intento de crear médico con RUN duplicado: {}", dto.getRun());
        throw new RuntimeException("Ya existe un médico registrado con ese RUN");
    }

    // Validar email único
    if (repository.existsByEmailIgnoreCase(dto.getEmail())) {
        log.warn("Intento de crear médico con email duplicado: {}", dto.getEmail());
        throw new RuntimeException("Ya existe un médico registrado con ese email");
    }

    Medico medico = Medico.builder()
            .run(dto.getRun().trim())
            .nombre(dto.getNombre().trim())
            .apellido(dto.getApellido().trim())
            .email(dto.getEmail().trim())
            .telefono(dto.getTelefono().trim())
            .especialidadId(dto.getEspecialidadId())
            .activo(true)
            .build();

    Medico guardado = repository.save(medico);

    log.info("Médico creado correctamente con ID: {}", guardado.getId());

    return mapToResponse(guardado);
}

@Override
public MedicoResponseDTO actualizar(Long id, MedicoRequestDTO dto) {

    log.info("Actualizando médico con ID: {}", id);

    Medico medico = obtenerEntidadPorId(id);

    // Validar que la especialidad exista
    especialidadClient.obtenerEspecialidad(dto.getEspecialidadId());

    // Validar RUN único, pero permitiendo que sea el mismo del médico actual
    repository.findByRun(dto.getRun())
            .ifPresent(m -> {
                if (!m.getId().equals(id)) {
                    log.warn("Intento de actualizar médico con RUN duplicado: {}", dto.getRun());
                    throw new RuntimeException("Ya existe otro médico registrado con ese RUN");
                }
            });

    // Validar email único, pero permitiendo que sea el mismo del médico actual
    repository.findByEmailIgnoreCase(dto.getEmail())
            .ifPresent(m -> {
                if (!m.getId().equals(id)) {
                    log.warn("Intento de actualizar médico con email duplicado: {}", dto.getEmail());
                    throw new RuntimeException("Ya existe otro médico registrado con ese email");
                }
            });

    medico.setRun(dto.getRun().trim());
    medico.setNombre(dto.getNombre().trim());
    medico.setApellido(dto.getApellido().trim());
    medico.setEmail(dto.getEmail().trim());
    medico.setTelefono(dto.getTelefono().trim());
    medico.setEspecialidadId(dto.getEspecialidadId());

    Medico actualizado = repository.save(medico);

    log.info("Médico actualizado correctamente con ID: {}", actualizado.getId());

    return mapToResponse(actualizado);
}

@Override
    public void eliminar(Long id) {
        log.info("Eliminando médico con ID: {}", id);

        Medico medico = obtenerEntidadPorId(id);
        repository.delete(medico);

        log.info("Médico eliminado correctamente con ID: {}", id);
    }

    @Override
    public MedicoResponseDTO activar(Long id) {
        log.info("Activando médico con ID: {}", id);

        Medico medico = obtenerEntidadPorId(id);
        medico.setActivo(true);

        Medico actualizado = repository.save(medico);
        log.info("Médico activado correctamente con ID: {}", id);

        return mapToResponse(actualizado);
    }

    @Override
    public MedicoResponseDTO desactivar(Long id) {
        log.info("Desactivando médico con ID: {}", id);

        Medico medico = obtenerEntidadPorId(id);
        medico.setActivo(false);

        Medico actualizado = repository.save(medico);
        log.info("Médico desactivado correctamente con ID: {}", id);

        return mapToResponse(actualizado);
    }

    private Medico obtenerEntidadPorId(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Médico no encontrado con ID: {}", id);
                    return new MedicoNotFoundException("Médico con ID " + id + " no encontrado");
                });
    }

    private MedicoResponseDTO mapToResponse(Medico medico) {
        return MedicoResponseDTO.builder()
                .id(medico.getId())
                .run(medico.getRun())
                .nombre(medico.getNombre())
                .apellido(medico.getApellido())
                .email(medico.getEmail())
                .telefono(medico.getTelefono())
                .especialidadId(medico.getEspecialidadId())
                .activo(medico.getActivo())
                .build();
    }

}
