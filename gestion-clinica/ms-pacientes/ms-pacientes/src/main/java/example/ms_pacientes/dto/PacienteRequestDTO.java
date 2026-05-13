package example.ms_pacientes.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDate;

@Data
public class PacienteRequestDTO {

    @NotBlank(message = "El RUN del paciente es obligatorio")
    @Size(min = 8, max = 10, message = "El RUN debe tener formato XXXXXXXX-X")
    private String run;

    @NotBlank(message = "El nombre del paciente es obligatorio")
    @Size(min = 2, max = 120, message = "El nombre debe tener entre 2 y 120 caracteres")
    private String nombre;

    @NotBlank(message = "El apellido del paciente es obligatorio")
    @Size(min = 2, max = 120, message = "El apellido debe tener entre 2 y 120 caracteres")
    private String apellido;

    @NotBlank(message = "El email del paciente es obligatorio")
    @Email(message = "Debe ingresar un email válido")
    @Size(max = 120, message = "El email no puede superar los 120 caracteres")
    private String email;

    @NotBlank(message = "El teléfono del médico es obligatorio")
    @Size(min = 12, max = 12, 
        message = "El teléfono debe tener 12 caracteres, incluya '+56' y el número sin espacios ni guiones")
    private String telefono;

    @NotNull(message = "La fecha de nacimiento es obligatoria")
    @Past(message = "La fecha de nacimiento debe ser anterior a la fecha actual")
    private LocalDate fechaNacimiento;

    @Size(max = 200, message = "La dirección no puede superar los 200 caracteres")
    private String direccion;
}