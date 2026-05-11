package example.ms_especialidades.service;

import example.ms_especialidades.dto.EspecialidadRequestDTO;
import example.ms_especialidades.dto.EspecialidadResponseDTO;
import example.ms_especialidades.exception.EspecialidadNotFoundException;
import example.ms_especialidades.model.Especialidad;
import example.ms_especialidades.repository.EspecialidadRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class EspecialidadServiceImpl implements EspecialidadService {

    private final EspecialidadRepository repository;

    @Override
    public List<EspecialidadResponseDTO> listarTodas() {
        log.info("Listando todas las especialidades");
        return repository.findAll()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public List<EspecialidadResponseDTO> listarActivas() {
        log.info("Listando especialidades activas");
        return repository.findByActivoTrue()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public EspecialidadResponseDTO buscarPorId(Long id) {
        log.info("Buscando especialidad por ID: {}", id);
        Especialidad especialidad = obtenerEntidadPorId(id);
        return mapToResponse(especialidad);
    }

    @Override
    public EspecialidadResponseDTO crear(EspecialidadRequestDTO dto) {
        log.info("Creando especialidad con nombre: {}", dto.getNombre());

        if (repository.existsByNombreIgnoreCase(dto.getNombre())) {
            log.warn("Intento de crear especialidad duplicada: {}", dto.getNombre());
            throw new RuntimeException("Ya existe una especialidad registrada con ese nombre");
        }

        Especialidad especialidad = Especialidad.builder()
                .nombre(dto.getNombre().trim())
                .descripcion(dto.getDescripcion())
                .activo(true)
                .build();

        Especialidad guardada = repository.save(especialidad);
        log.info("Especialidad creada correctamente con ID: {}", guardada.getId());

        return mapToResponse(guardada);
    }

    @Override
    public EspecialidadResponseDTO actualizar(Long id, EspecialidadRequestDTO dto) {
        log.info("Actualizando especialidad con ID: {}", id);

        Especialidad especialidad = obtenerEntidadPorId(id);

        repository.findByNombreIgnoreCase(dto.getNombre())
                .ifPresent(e -> {
                    if (!e.getId().equals(id)) {
                        log.warn("Nombre de especialidad duplicado al actualizar: {}", dto.getNombre());
                        throw new RuntimeException("Ya existe otra especialidad con ese nombre");
                    }
                });

        especialidad.setNombre(dto.getNombre().trim());
        especialidad.setDescripcion(dto.getDescripcion());

        Especialidad actualizada = repository.save(especialidad);
        log.info("Especialidad actualizada correctamente con ID: {}", actualizada.getId());

        return mapToResponse(actualizada);
    }

    @Override
    public void eliminar(Long id) {
        log.info("Eliminando especialidad con ID: {}", id);

        Especialidad especialidad = obtenerEntidadPorId(id);
        repository.delete(especialidad);

        log.info("Especialidad eliminada correctamente con ID: {}", id);
    }

    @Override
    public EspecialidadResponseDTO activar(Long id) {
        log.info("Activando especialidad con ID: {}", id);

        Especialidad especialidad = obtenerEntidadPorId(id);
        especialidad.setActivo(true);

        return mapToResponse(repository.save(especialidad));
    }

    @Override
    public EspecialidadResponseDTO desactivar(Long id) {
        log.info("Desactivando especialidad con ID: {}", id);

        Especialidad especialidad = obtenerEntidadPorId(id);
        especialidad.setActivo(false);

        return mapToResponse(repository.save(especialidad));
    }

    private Especialidad obtenerEntidadPorId(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Especialidad no encontrada con ID: {}", id);
                    return new EspecialidadNotFoundException("Especialidad con ID " + id + " no encontrada");
                });
    }

    private EspecialidadResponseDTO mapToResponse(Especialidad especialidad) {
        return EspecialidadResponseDTO.builder()
                .id(especialidad.getId())
                .nombre(especialidad.getNombre())
                .descripcion(especialidad.getDescripcion())
                .activo(especialidad.getActivo())
                .build();
    }
}
