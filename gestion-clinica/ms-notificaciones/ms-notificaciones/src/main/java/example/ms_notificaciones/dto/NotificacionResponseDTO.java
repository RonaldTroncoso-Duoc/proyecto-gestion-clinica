package example.ms_notificaciones.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class NotificacionResponseDTO {

    private Long id;
    private Long pacienteId;
    private Long citaId;
    private String tipo;
    private String mensaje;
    private String estado;
    private LocalDateTime fechaEnvio;
}
