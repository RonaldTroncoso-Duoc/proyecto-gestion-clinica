package example.ms_citas.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class CitaRequestDTO {

    @NotNull(message = "El ID del paciente es obligatorio")
    private Long pacienteId;

    @NotNull(message = "El ID del médico es obligatorio")
    private Long medicoId;

    @NotNull(message = "El ID del horario es obligatorio")
    private Long horarioId;

    @NotBlank(message = "El motivo de la cita es obligatorio")
    @Size(min = 3, max = 200, message = "El motivo debe tener entre 3 y 200 caracteres")
    private String motivo;
}
