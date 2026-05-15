package example.ms_notificaciones.exception;

import feign.FeignException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(NotificacionNotFoundException.class)
    public ResponseEntity<Object> handleNotificacionNotFound(NotificacionNotFoundException ex) {
        log.warn("Notificacion no encontrado: {}", ex.getMessage());
        return buildResponse(ex.getMessage(), HttpStatus.NOT_FOUND, "Notificacion No Encontrada");
    }

    @ExceptionHandler(FeignException.NotFound.class)
    public ResponseEntity<Object> handleFeignNotFound(FeignException.NotFound ex) {
        log.warn("Recurso externo no encontrado desde otro microservicio");
        return buildResponse(
                "Paciente o cita no encontrada en el sistema",
                HttpStatus.NOT_FOUND,
                "Recurso Externo No Encontrado"
        );
    }

    @ExceptionHandler(FeignException.class)
    public ResponseEntity<Object> handleFeignException(FeignException ex) {
        log.error("Error Feign - Status: {}, Message: {}", ex.status(), ex.getMessage());
        return buildResponse(
                "Error Feign status: " + ex.status() + " - " + ex.getMessage(),
                HttpStatus.BAD_GATEWAY,
                "Error Feign"
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

    private ResponseEntity<Object> buildResponse(String message, HttpStatus status, String error) {
        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", status.value());
        body.put("error", error);
        body.put("message", message);
        return new ResponseEntity<>(body, status);
    }
}

