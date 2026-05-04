package example.ms_citas.exception;

// Esta excepción se lanzará cuando busquemos una cita por ID y no esté
public class CitaNotFoundException extends RuntimeException {
    public CitaNotFoundException(String mensaje) {
        super(mensaje);
    }
}