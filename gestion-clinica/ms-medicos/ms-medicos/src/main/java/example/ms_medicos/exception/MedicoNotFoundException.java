package example.ms_medicos.exception;

public class MedicoNotFoundException extends RuntimeException {
    public MedicoNotFoundException(String mensaje) {
        super(mensaje);
    }
}

