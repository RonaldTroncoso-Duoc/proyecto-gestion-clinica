package example.ms_recetas.exception;

public class RecetaNotFoundException extends RuntimeException {
    public RecetaNotFoundException(String mensaje) {
        super(mensaje);
    }
}
