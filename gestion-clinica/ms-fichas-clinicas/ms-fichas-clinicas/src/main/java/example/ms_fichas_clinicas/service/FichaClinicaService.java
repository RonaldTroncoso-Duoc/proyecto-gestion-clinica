package example.ms_fichas_clinicas.service;

import java.util.List;

import example.ms_fichas_clinicas.dto.FichaClinicaRequestDTO;
import example.ms_fichas_clinicas.dto.FichaClinicaResponseDTO;

public interface FichaClinicaService {

    List<FichaClinicaResponseDTO> listarTodas();

    List<FichaClinicaResponseDTO> listarActivas();

    List<FichaClinicaResponseDTO> listarPorPaciente(Long pacienteId);

    List<FichaClinicaResponseDTO> listarPorMedico(Long medicoId);

    FichaClinicaResponseDTO buscarPorId(Long id);

    FichaClinicaResponseDTO buscarPorCita(Long citaId);

    FichaClinicaResponseDTO crear(FichaClinicaRequestDTO dto);

    FichaClinicaResponseDTO actualizar(Long id, FichaClinicaRequestDTO dto);

    FichaClinicaResponseDTO desactivar(Long id);

    FichaClinicaResponseDTO activar(Long id);

    void eliminar(Long id);

}
