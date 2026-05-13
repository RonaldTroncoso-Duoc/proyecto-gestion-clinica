package example.ms_medicos.service;

import java.util.List;

import example.ms_medicos.dto.MedicoRequestDTO;
import example.ms_medicos.dto.MedicoResponseDTO;

public interface MedicoService {
    
    List<MedicoResponseDTO> listarTodos();

    List<MedicoResponseDTO> listarActivos();

    MedicoResponseDTO buscarPorId(Long id);

    List<MedicoResponseDTO> buscarPorEspecialidad(Long especialidadId);

    List<MedicoResponseDTO> buscarActivosPorEspecialidad(Long especialidadId);

    MedicoResponseDTO crear(MedicoRequestDTO dto);

    MedicoResponseDTO actualizar(Long id, MedicoRequestDTO dto);

    void eliminar(Long id);

    MedicoResponseDTO activar(Long id);

    MedicoResponseDTO desactivar(Long id);
}