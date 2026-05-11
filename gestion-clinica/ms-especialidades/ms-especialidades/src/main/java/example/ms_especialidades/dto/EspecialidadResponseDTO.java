package example.ms_especialidades.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class EspecialidadResponseDTO {

    private Long id;
    private String nombre;
    private String descripcion;
    private Boolean activo;
}
