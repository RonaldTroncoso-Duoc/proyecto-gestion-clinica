package example.ms_pagos.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class PagoResponseDTO {

    private Long id;
    private Long pacienteId;
    private Long citaId;
    private Integer monto;
    private String metodoPago;
    private String estado;
    private LocalDateTime fechaRegistro;
}