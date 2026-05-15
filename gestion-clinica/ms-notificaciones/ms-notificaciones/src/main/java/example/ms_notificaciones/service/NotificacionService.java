package example.ms_notificaciones.service;

import example.ms_notificaciones.dto.NotificacionRequestDTO;
import example.ms_notificaciones.dto.NotificacionResponseDTO;

import java.util.List;

public interface NotificacionService {

    List<NotificacionResponseDTO> listarTodas();

    List<NotificacionResponseDTO> listarPorPaciente(Long pacienteId);

    List<NotificacionResponseDTO> listarPorEstado(String estado);

    List<NotificacionResponseDTO> listarPorTipo(String tipo);

    NotificacionResponseDTO buscarPorId(Long id);

    NotificacionResponseDTO crear(NotificacionRequestDTO dto);

    NotificacionResponseDTO marcarEnviada(Long id);

    NotificacionResponseDTO marcarFallida(Long id);

    void eliminar(Long id);
}
