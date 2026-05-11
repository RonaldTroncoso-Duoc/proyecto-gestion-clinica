package example.ms_especialidades.service;

import example.ms_especialidades.dto.EspecialidadRequestDTO;
import example.ms_especialidades.dto.EspecialidadResponseDTO;

import java.util.List;

public interface EspecialidadService {

    List<EspecialidadResponseDTO> listarTodas();

    List<EspecialidadResponseDTO> listarActivas();

    EspecialidadResponseDTO buscarPorId(Long id);

    EspecialidadResponseDTO crear(EspecialidadRequestDTO dto);

    EspecialidadResponseDTO actualizar(Long id, EspecialidadRequestDTO dto);

    void eliminar(Long id);

    EspecialidadResponseDTO activar(Long id);

    EspecialidadResponseDTO desactivar(Long id);
}
