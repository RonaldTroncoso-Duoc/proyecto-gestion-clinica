package example.ms_notificaciones.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class NotificacionRequestDTO {

    @NotNull(message = "El ID del paciente es obligatorio")
    private Long pacienteId;

    @NotNull(message = "El ID de la cita es obligatorio")
    private Long citaId;

    @NotBlank(message = "El tipo de notificación es obligatorio")
    @Size(min = 3, max = 50)
    private String tipo;

    @NotBlank(message = "El mensaje es obligatorio")
    @Size(min = 5, max = 500)
    private String mensaje;
}
