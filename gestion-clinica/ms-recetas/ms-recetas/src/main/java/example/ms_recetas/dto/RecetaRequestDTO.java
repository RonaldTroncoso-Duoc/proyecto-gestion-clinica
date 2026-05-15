package example.ms_recetas.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class RecetaRequestDTO {

    @NotNull(message = "El ID de la ficha clínica es obligatorio")
    private Long fichaClinicaId;

    @NotNull(message = "El ID del paciente es obligatorio")
    private Long pacienteId;

    @NotNull(message = "El ID del médico es obligatorio")
    private Long medicoId;

    @NotBlank(message = "El medicamento es obligatorio")
    @Size(min = 2, max = 150, message = "El medicamento debe tener entre 2 y 150 caracteres")
    private String medicamento;

    @NotBlank(message = "La dosis es obligatoria")
    @Size(min = 2, max = 150, message = "La dosis debe tener entre 2 y 150 caracteres")
    private String dosis;

    @NotBlank(message = "Las indicaciones son obligatorias")
    @Size(min = 2, max = 300, message = "Las indicaciones deben tener entre 2 y 300 caracteres")
    private String indicaciones;

    @NotNull(message = "La duración en días es obligatoria")
    @Min(value = 1, message = "La duración debe ser al menos 1 día")
    private Integer duracionDias;
}
