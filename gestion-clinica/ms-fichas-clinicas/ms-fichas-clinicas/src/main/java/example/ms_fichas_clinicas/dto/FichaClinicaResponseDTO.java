package example.ms_fichas_clinicas.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class FichaClinicaResponseDTO {

    private Long id;
    private Long pacienteId;
    private Long medicoId;
    private Long citaId;
    private String motivoConsulta;
    private String diagnostico;
    private String tratamiento;
    private String observaciones;
    private LocalDateTime fechaRegistro;
    private Boolean activo;
}
