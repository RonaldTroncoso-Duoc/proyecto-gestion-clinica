package example.ms_pagos.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class PagoRequestDTO {

    @NotNull(message = "El ID del paciente es obligatorio")
    private Long pacienteId;

    @NotNull(message = "El ID de la cita es obligatorio")
    private Long citaId;

    @NotNull(message = "El monto es obligatorio")
    @Min(value = 1, message = "El monto debe ser mayor a 0")
    private Integer monto;

    @NotBlank(message = "El método de pago es obligatorio")
    @Size(min = 3, max = 50, message = "El método de pago debe tener entre 3 y 50 caracteres")
    private String metodoPago;
}
