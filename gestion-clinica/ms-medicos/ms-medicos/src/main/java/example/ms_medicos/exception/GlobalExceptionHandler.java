package example.ms_medicos.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

    // 1. Captura la excepción específica del Microservicio (Paciente, Medico o Usuario)
    @ExceptionHandler({MedicoNotFoundException.class, MedicoNotFoundException.class}) 
    public ResponseEntity<Object> handleNotFound(RuntimeException ex) {
        return buildResponse(ex.getMessage(), HttpStatus.NOT_FOUND, "Recurso No Encontrado");
    }

    // 2. Captura errores de validación (como el @Email o @NotBlank que pusiste)
    @ExceptionHandler(org.springframework.web.bind.MethodArgumentNotValidException.class)
    public ResponseEntity<Object> handleValidation(org.springframework.web.bind.MethodArgumentNotValidException ex) {
        String mensaje = ex.getBindingResult().getFieldError().getDefaultMessage();
        return buildResponse(mensaje, HttpStatus.BAD_REQUEST, "Error de Validación");
    }

    // 3. Error genérico para cualquier otra cosa
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleGlobal(Exception ex) {
        return buildResponse("Ocurrió un error interno en el servidor", 
                             HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage());
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