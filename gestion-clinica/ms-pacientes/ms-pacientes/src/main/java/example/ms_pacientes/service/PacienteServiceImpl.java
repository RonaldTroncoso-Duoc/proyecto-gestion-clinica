package example.ms_pacientes.service;

import example.ms_pacientes.client.AuthClient;
import example.ms_pacientes.dto.AuthRegisterRequestDTO;
import example.ms_pacientes.dto.AuthUserResponseDTO;
import example.ms_pacientes.dto.PacienteRequestDTO;
import example.ms_pacientes.dto.PacienteResponseDTO;
import example.ms_pacientes.exception.PacienteNotFoundException;
import example.ms_pacientes.model.Paciente;
import example.ms_pacientes.repository.PacienteRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class PacienteServiceImpl implements PacienteService {

    private final PacienteRepository repository;
    private final AuthClient authClient;

    @Override
    public List<PacienteResponseDTO> listarTodos() {
        log.info("Listando todos los pacientes");
        return repository.findAll()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public List<PacienteResponseDTO> listarActivos() {
        log.info("Listando pacientes activos");
        return repository.findByActivoTrue()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public PacienteResponseDTO buscarPorId(Long id) {
        log.info("Buscando paciente por ID: {}", id);
        Paciente paciente = obtenerEntidadPorId(id);
        return mapToResponse(paciente);
    }

    @Override
    public PacienteResponseDTO buscarPorRun(String run) {
        log.info("Buscando paciente por RUN: {}", run);

        Paciente paciente = repository.findByRun(run)
                .orElseThrow(() -> {
                    log.warn("Paciente no encontrado con RUN: {}", run);
                    return new PacienteNotFoundException("Paciente con RUN " + run + " no encontrado");
                });

        return mapToResponse(paciente);
    }

    @Override
    @Transactional
    public PacienteResponseDTO crear(PacienteRequestDTO dto) {
        log.info("Creando paciente con RUN: {}", dto.getRun());

        if (repository.existsByRun(dto.getRun())) {
            log.warn("Intento de crear paciente con RUN duplicado: {}", dto.getRun());
            throw new RuntimeException("Ya existe un paciente registrado con ese RUN");
        }

        if (repository.existsByEmailIgnoreCase(dto.getEmail())) {
            log.warn("Intento de crear paciente con email duplicado: {}", dto.getEmail());
            throw new RuntimeException("Ya existe un paciente registrado con ese email");
        }

        Paciente paciente = Paciente.builder()
                .run(dto.getRun().trim())
                .nombre(dto.getNombre().trim())
                .apellido(dto.getApellido().trim())
                .email(dto.getEmail().trim())
                .telefono(dto.getTelefono().trim())
                .fechaNacimiento(dto.getFechaNacimiento())
                .direccion(dto.getDireccion())
                .activo(true)
                .build();

        Paciente guardado = repository.save(paciente);

        log.info("Paciente creado correctamente con ID: {}", guardado.getId());

        return mapToResponse(guardado);
    }

    @Override
    @Transactional
    public PacienteResponseDTO registrar(PacienteRequestDTO dto) {
        log.info("Registrando nuevo paciente con RUN: {}", dto.getRun());

        // 1. Validar datos locales
        if (repository.existsByRun(dto.getRun())) {
            log.warn("Intento de registro con RUN duplicado: {}", dto.getRun());
            throw new RuntimeException("Ya existe un paciente registrado con ese RUN");
        }

        if (repository.existsByEmailIgnoreCase(dto.getEmail())) {
            log.warn("Intento de registro con email duplicado: {}", dto.getEmail());
            throw new RuntimeException("Ya existe un paciente registrado con ese email");
        }

        // 2. Crear usuario en ms-auth via Feign
        AuthRegisterRequestDTO authRequest = new AuthRegisterRequestDTO();
        authRequest.setUsername(dto.getUsername());
        authRequest.setEmail(dto.getEmail());
        authRequest.setPassword(dto.getPassword());

        log.info("Creando usuario en ms-auth para paciente: {}", dto.getUsername());

        AuthUserResponseDTO authResponse;
        try {
            authResponse = authClient.registerUser(authRequest, "PATIENT");
        } catch (Exception e) {
            log.error("Error al crear usuario en ms-auth: {}", e.getMessage());
            throw new RuntimeException("No se pudo crear el usuario de autenticación. " + e.getMessage());
        }

        Long authUserId = authResponse.getId();
        log.info("Usuario creado en ms-auth con ID: {}", authUserId);

        // 3. Crear paciente con authUserId
        Paciente paciente = Paciente.builder()
                .authUserId(authUserId)
                .run(dto.getRun().trim())
                .nombre(dto.getNombre().trim())
                .apellido(dto.getApellido().trim())
                .email(dto.getEmail().trim())
                .telefono(dto.getTelefono().trim())
                .fechaNacimiento(dto.getFechaNacimiento())
                .direccion(dto.getDireccion())
                .activo(true)
                .build();

        Paciente guardado = repository.save(paciente);

        log.info("Paciente registrado correctamente con ID: {} y authUserId: {}", guardado.getId(), authUserId);

        return mapToResponse(guardado);
    }

    @Override
    public PacienteResponseDTO actualizar(Long id, PacienteRequestDTO dto) {
        log.info("Actualizando paciente con ID: {}", id);

        Paciente paciente = obtenerEntidadPorId(id);

        repository.findByRun(dto.getRun())
                .ifPresent(p -> {
                    if (!p.getId().equals(id)) {
                        log.warn("Intento de actualizar paciente con RUN duplicado: {}", dto.getRun());
                        throw new RuntimeException("Ya existe otro paciente registrado con ese RUN");
                    }
                });

        repository.findByEmailIgnoreCase(dto.getEmail())
                .ifPresent(p -> {
                    if (!p.getId().equals(id)) {
                        log.warn("Intento de actualizar paciente con email duplicado: {}", dto.getEmail());
                        throw new RuntimeException("Ya existe otro paciente registrado con ese email");
                    }
                });

        paciente.setRun(dto.getRun().trim());
        paciente.setNombre(dto.getNombre().trim());
        paciente.setApellido(dto.getApellido().trim());
        paciente.setEmail(dto.getEmail().trim());
        paciente.setTelefono(dto.getTelefono().trim());
        paciente.setFechaNacimiento(dto.getFechaNacimiento());
        paciente.setDireccion(dto.getDireccion());

        Paciente actualizado = repository.save(paciente);

        log.info("Paciente actualizado correctamente con ID: {}", actualizado.getId());

        return mapToResponse(actualizado);
    }

    @Override
    public void eliminar(Long id) {
        log.info("Eliminando paciente con ID: {}", id);

        Paciente paciente = obtenerEntidadPorId(id);
        repository.delete(paciente);

        log.info("Paciente eliminado correctamente con ID: {}", id);
    }

    @Override
    public PacienteResponseDTO activar(Long id) {
        log.info("Activando paciente con ID: {}", id);

        Paciente paciente = obtenerEntidadPorId(id);
        paciente.setActivo(true);

        Paciente actualizado = repository.save(paciente);

        log.info("Paciente activado correctamente con ID: {}", id);

        return mapToResponse(actualizado);
    }

    @Override
    public PacienteResponseDTO desactivar(Long id) {
        log.info("Desactivando paciente con ID: {}", id);

        Paciente paciente = obtenerEntidadPorId(id);
        paciente.setActivo(false);

        Paciente actualizado = repository.save(paciente);

        log.info("Paciente desactivado correctamente con ID: {}", id);

        return mapToResponse(actualizado);
    }

    private Paciente obtenerEntidadPorId(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Paciente no encontrado con ID: {}", id);
                    return new PacienteNotFoundException("Paciente con ID " + id + " no encontrado");
                });
    }

    private PacienteResponseDTO mapToResponse(Paciente paciente) {
        return PacienteResponseDTO.builder()
                .id(paciente.getId())
                .authUserId(paciente.getAuthUserId())
                .run(paciente.getRun())
                .nombre(paciente.getNombre())
                .apellido(paciente.getApellido())
                .email(paciente.getEmail())
                .telefono(paciente.getTelefono())
                .fechaNacimiento(paciente.getFechaNacimiento())
                .direccion(paciente.getDireccion())
                .activo(paciente.getActivo())
                .build();
    }
}