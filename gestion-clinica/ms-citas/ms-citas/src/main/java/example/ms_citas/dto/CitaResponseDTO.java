package example.ms_citas.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class CitaResponseDTO {

    private Long id;
    private Long pacienteId;
    private Long medicoId;
    private Long horarioId;
    private String motivo;
    private String estado;
    private LocalDateTime fechaCreacion;
}