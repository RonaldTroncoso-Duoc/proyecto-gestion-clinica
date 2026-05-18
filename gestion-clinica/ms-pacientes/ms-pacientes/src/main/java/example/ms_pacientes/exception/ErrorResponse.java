package example.ms_pacientes.exception;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class ErrorResponse {

    private String error;

    private String message;

    private int status;

    private LocalDateTime timestamp;
}