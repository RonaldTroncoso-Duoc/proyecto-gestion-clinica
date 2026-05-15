package example.ms_pagos.service;

import example.ms_pagos.dto.PagoRequestDTO;
import example.ms_pagos.dto.PagoResponseDTO;

import java.util.List;

public interface PagoService {

    List<PagoResponseDTO> listarTodos();

    List<PagoResponseDTO> listarPorPaciente(Long pacienteId);

    List<PagoResponseDTO> listarPorEstado(String estado);

    PagoResponseDTO buscarPorId(Long id);

    PagoResponseDTO buscarPorCita(Long citaId);

    PagoResponseDTO crear(PagoRequestDTO dto);

    PagoResponseDTO actualizar(Long id, PagoRequestDTO dto);

    PagoResponseDTO confirmar(Long id);

    PagoResponseDTO anular(Long id);

    void eliminar(Long id);
}
