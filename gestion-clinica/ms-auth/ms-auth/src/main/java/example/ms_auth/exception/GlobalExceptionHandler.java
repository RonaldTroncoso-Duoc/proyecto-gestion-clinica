package example.ms_auth.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

    // 1. Cuando el usuario no existe en la base de datos
    @ExceptionHandler(UsuarioNotFoundException.class)
    public ResponseEntity<Object> handleUsuarioNotFound(UsuarioNotFoundException ex) {
        return buildResponse(ex.getMessage(), HttpStatus.NOT_FOUND, "Usuario No Encontrado");
    }

    // 2. Errores de credenciales o de lógica de negocio (RuntimeException)
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Object> handleRuntimeException(RuntimeException ex) {
        return buildResponse(ex.getMessage(), HttpStatus.BAD_REQUEST, "Error de Autenticación");
    }

    // 3. Captura errores de validación (por si usas @Valid en el registro)
    @ExceptionHandler(org.springframework.web.bind.MethodArgumentNotValidException.class)
    public ResponseEntity<Object> handleValidation(org.springframework.web.bind.MethodArgumentNotValidException ex) {
        String mensaje = ex.getBindingResult().getFieldError().getDefaultMessage();
        return buildResponse(mensaje, HttpStatus.BAD_REQUEST, "Datos Inválidos");
    }

    // 4. Error genérico
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleGlobal(Exception ex) {
        return buildResponse("Error interno en el servicio de seguridad", 
                             HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage());
    }

    // El método "limpiador" que unifica todo
    private ResponseEntity<Object> buildResponse(String message, HttpStatus status, String error) {
        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", status.value());
        body.put("error", error);
        body.put("message", message);
        return new ResponseEntity<>(body, status);
    }
}