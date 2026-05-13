package example.ms_medicos.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class MedicoRequestDTO {

    @NotBlank(message = "El RUN del médico es obligatorio")
    private String run;

    @NotBlank(message = "El nombre del médico es obligatorio")
    @Size(min = 2, max = 120, message = "El nombre debe tener entre 2 y 120 caracteres")
    private String nombre;

    @NotBlank(message = "El apellido del médico es obligatorio")
    @Size(min = 2, max = 120, message = "El apellido debe tener entre 2 y 120 caracteres")
    private String apellido;

    @NotBlank(message = "El email del médico es obligatorio")
    @Email(message = "Debe ingresar un email válido")
    @Size(max = 120, message = "El email no puede superar los 120 caracteres")
    private String email;

    @NotBlank(message = "El teléfono del médico es obligatorio")
    @Size(min = 12, max = 12, 
        message = "El teléfono debe tener 12 caracteres, incluya '+56' y el número sin espacios ni guiones")
    private String telefono;

    @NotNull(message = "El ID de la especialidad es obligatorio")
    private Long especialidadId;
}
