package example.ms_horarios.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(HorarioNotFoundException.class)
    public ResponseEntity<Object> handleHorarioNotFound(HorarioNotFoundException ex) {
        return buildResponse(ex.getMessage(), HttpStatus.NOT_FOUND, "Horario No Encontrado");
    }

    @ExceptionHandler(feign.FeignException.NotFound.class)
    public ResponseEntity<Object> handleFeignNotFound(feign.FeignException.NotFound ex) {
        return buildResponse(
                "El médico solicitado no existe en el sistema",
                HttpStatus.NOT_FOUND,
                "Médico No Encontrado"
        );
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Object> handleValidation(MethodArgumentNotValidException ex) {
        String mensaje = ex.getBindingResult().getFieldError().getDefaultMessage();
        return buildResponse(mensaje, HttpStatus.BAD_REQUEST, "Error de Validación");
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Object> handleRuntime(RuntimeException ex) {
        return buildResponse(ex.getMessage(), HttpStatus.BAD_REQUEST, "Error de Negocio");
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleGlobal(Exception ex) {
        return buildResponse(
                "Ocurrió un error interno en el servidor",
                HttpStatus.INTERNAL_SERVER_ERROR,
                ex.getMessage()
        );
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