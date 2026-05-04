package example.ms_citas.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

    // 1. Captura cuando la CITA no existe
    @ExceptionHandler(CitaNotFoundException.class)
    public ResponseEntity<Object> handleCitaNotFound(CitaNotFoundException ex) {
        return buildResponse(ex.getMessage(), HttpStatus.NOT_FOUND, "Cita No Encontrada");
    }

    // 2. Captura cuando FEIGN recibe un 404 de otro microservicio (Paciente/Médico)
    @ExceptionHandler(feign.FeignException.NotFound.class)
    public ResponseEntity<Object> handleFeignNotFound(feign.FeignException.NotFound ex) {
        return buildResponse("El Paciente o Médico solicitado no existe en el sistema.", 
                             HttpStatus.NOT_FOUND, "Recurso Externo No Encontrado");
    }

    // 3. Error genérico
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleGlobal(Exception ex) {
        return buildResponse("Ocurrió un error inesperado", 
                             HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage());
    }

    // Método auxiliar para no repetir código de respuesta
    private ResponseEntity<Object> buildResponse(String message, HttpStatus status, String error) {
        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", status.value());
        body.put("error", error);
        body.put("message", message);
        return new ResponseEntity<>(body, status);
    }
}