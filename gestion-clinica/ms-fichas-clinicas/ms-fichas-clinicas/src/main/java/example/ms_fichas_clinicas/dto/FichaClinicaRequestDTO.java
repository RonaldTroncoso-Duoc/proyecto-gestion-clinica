package example.ms_fichas_clinicas.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class FichaClinicaRequestDTO {

    @NotNull(message = "El ID del paciente es obligatorio")
    private Long pacienteId;

    @NotNull(message = "El ID del médico es obligatorio")
    private Long medicoId;

    @NotNull(message = "El ID de la cita es obligatorio")
    private Long citaId;

    @NotBlank(message = "El motivo de consulta es obligatorio")
    @Size(min = 3, max = 500, message = "El motivo debe tener entre 3 y 500 caracteres")
    private String motivoConsulta;

    @NotBlank(message = "El diagnóstico es obligatorio")
    @Size(min = 3, max = 1000, message = "El diagnóstico debe tener entre 3 y 1000 caracteres")
    private String diagnostico;

    @NotBlank(message = "El tratamiento es obligatorio")
    @Size(min = 3, max = 1000, message = "El tratamiento debe tener entre 3 y 1000 caracteres")
    private String tratamiento;

    @Size(max = 1000, message = "Las observaciones no pueden superar los 1000 caracteres")
    private String observaciones;
}
