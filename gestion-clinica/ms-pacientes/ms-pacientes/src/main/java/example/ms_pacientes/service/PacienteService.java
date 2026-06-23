package example.ms_pacientes.service;

import example.ms_pacientes.dto.PacienteRequestDTO;
import example.ms_pacientes.dto.PacienteResponseDTO;

import java.util.List;

public interface PacienteService {

    List<PacienteResponseDTO> listarTodos();

    List<PacienteResponseDTO> listarActivos();

    PacienteResponseDTO buscarPorId(Long id);

    PacienteResponseDTO buscarPorRun(String run);

    PacienteResponseDTO crear(PacienteRequestDTO dto);

    PacienteResponseDTO registrar(PacienteRequestDTO dto);

    PacienteResponseDTO actualizar(Long id, PacienteRequestDTO dto);

    void eliminar(Long id);

    PacienteResponseDTO activar(Long id);

    PacienteResponseDTO desactivar(Long id);
}