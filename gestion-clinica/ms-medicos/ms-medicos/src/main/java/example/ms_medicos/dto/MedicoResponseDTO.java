package example.ms_medicos.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MedicoResponseDTO {

    private Long id;
    private String run;
    private String nombre;
    private String apellido;
    private String email;
    private String telefono;
    private Long especialidadId;
    private Boolean activo;
}
