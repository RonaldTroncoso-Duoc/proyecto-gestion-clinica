package example.ms_horarios.service;

import example.ms_horarios.dto.HorarioRequestDTO;
import example.ms_horarios.dto.HorarioResponseDTO;

import java.util.List;

public interface HorarioService {

    List<HorarioResponseDTO> listarTodos();

    List<HorarioResponseDTO> listarDisponibles();

    List<HorarioResponseDTO> listarPorMedico(Long medicoId);

    List<HorarioResponseDTO> listarDisponiblesPorMedico(Long medicoId);

    HorarioResponseDTO buscarPorId(Long id);

    HorarioResponseDTO crear(HorarioRequestDTO dto);

    HorarioResponseDTO actualizar(Long id, HorarioRequestDTO dto);

    HorarioResponseDTO ocupar(Long id);

    HorarioResponseDTO liberar(Long id);

    void eliminar(Long id);
}