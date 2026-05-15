package example.ms_recetas.service;

import example.ms_recetas.dto.RecetaRequestDTO;
import example.ms_recetas.dto.RecetaResponseDTO;

import java.util.List;

public interface RecetaService {

    List<RecetaResponseDTO> listarTodas();

    List<RecetaResponseDTO> listarActivas();

    List<RecetaResponseDTO> listarPorPaciente(Long pacienteId);

    List<RecetaResponseDTO> listarPorMedico(Long medicoId);

    List<RecetaResponseDTO> listarPorFichaClinica(Long fichaClinicaId);

    RecetaResponseDTO buscarPorId(Long id);

    RecetaResponseDTO crear(RecetaRequestDTO dto);

    RecetaResponseDTO actualizar(Long id, RecetaRequestDTO dto);

    RecetaResponseDTO activar(Long id);

    RecetaResponseDTO desactivar(Long id);

    void eliminar(Long id);
}
