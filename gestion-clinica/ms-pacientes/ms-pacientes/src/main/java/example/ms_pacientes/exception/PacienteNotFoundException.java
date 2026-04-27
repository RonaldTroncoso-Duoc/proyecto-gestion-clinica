package example.ms_pacientes.exception;

public class PacienteNotFoundException extends RuntimeException {
    public PacienteNotFoundException(String mensaje) {
        super(mensaje);
    }
}

