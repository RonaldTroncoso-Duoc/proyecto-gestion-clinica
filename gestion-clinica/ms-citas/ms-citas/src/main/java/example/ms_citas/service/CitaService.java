package example.ms_citas.service;

import example.ms_citas.dto.CitaRequestDTO;
import example.ms_citas.dto.CitaResponseDTO;

import java.util.List;

public interface CitaService {

    List<CitaResponseDTO> listarTodas();

    List<CitaResponseDTO> listarPorPaciente(Long pacienteId);

    List<CitaResponseDTO> listarPorMedico(Long medicoId);

    List<CitaResponseDTO> listarPorEstado(String estado);

    CitaResponseDTO buscarPorId(Long id);

    CitaResponseDTO crear(CitaRequestDTO dto);

    CitaResponseDTO actualizar(Long id, CitaRequestDTO dto);

    CitaResponseDTO cancelar(Long id);

    CitaResponseDTO realizar(Long id);

    void eliminar(Long id);
}