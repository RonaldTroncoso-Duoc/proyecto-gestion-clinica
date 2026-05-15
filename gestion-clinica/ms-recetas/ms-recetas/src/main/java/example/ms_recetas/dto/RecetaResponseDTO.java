package example.ms_recetas.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class RecetaResponseDTO {

    private Long id;
    private Long fichaClinicaId;
    private Long pacienteId;
    private Long medicoId;
    private String medicamento;
    private String dosis;
    private String indicaciones;
    private Integer duracionDias;
    private LocalDateTime fechaEmision;
    private Boolean activa;
}
