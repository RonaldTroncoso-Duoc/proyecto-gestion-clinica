package example.ms_horarios.exception;

import feign.FeignException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.security.access.AccessDeniedException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(HorarioNotFoundException.class)
    public ResponseEntity<Object> handleHorarioNotFound(HorarioNotFoundException ex) {
        log.warn("Horario no encontrado: {}", ex.getMessage());
        return buildResponse(ex.getMessage(), HttpStatus.NOT_FOUND, "Horario No Encontrado");
    }

    @ExceptionHandler(FeignException.NotFound.class)
    public ResponseEntity<Object> handleFeignNotFound(FeignException.NotFound ex) {
        log.warn("Médico no encontrado desde ms-medicos");
        return buildResponse(
                "El médico indicado no existe en el sistema",
                HttpStatus.NOT_FOUND,
                "Médico No Encontrado"
        );
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Object> handleValidation(MethodArgumentNotValidException ex) {
        String mensaje = ex.getBindingResult().getFieldError().getDefaultMessage();
        log.warn("Error de validación: {}", mensaje);
        return buildResponse(mensaje, HttpStatus.BAD_REQUEST, "Error de Validación");
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Object> handleRuntime(RuntimeException ex) {
        log.warn("Error de negocio: {}", ex.getMessage());
        return buildResponse(ex.getMessage(), HttpStatus.BAD_REQUEST, "Error de Negocio");
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleGlobal(Exception ex) {
        log.error("Error interno no controlado", ex);
        return buildResponse(
                "Ocurrió un error interno en el servidor",
                HttpStatus.INTERNAL_SERVER_ERROR,
                ex.getMessage()
        );
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAccessDenied(
        AccessDeniedException ex
) {

    ErrorResponse error = ErrorResponse.builder()
            .error("Acceso Denegado")
            .message(ex.getMessage())
            .status(403)
            .timestamp(LocalDateTime.now())
            .build();

    return ResponseEntity.status(403).body(error);
}
    private ResponseEntity<Object> buildResponse(String message, HttpStatus status, String error) {
        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", status.value());
        body.put("error", error);
        body.put("message", message);

        return new ResponseEntity<>(body, status);
    }
}