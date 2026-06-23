package example.ms_pacientes.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder
public class PacienteResponseDTO {

    private Long id;
    private Long authUserId;
    private String run;
    private String nombre;
    private String apellido;
    private String email;
    private String telefono;
    private LocalDate fechaNacimiento;
    private String direccion;
    private Boolean activo;
}