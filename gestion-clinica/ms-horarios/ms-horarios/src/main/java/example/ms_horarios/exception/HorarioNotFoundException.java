package example.ms_horarios.exception;

public class HorarioNotFoundException extends RuntimeException {
    public HorarioNotFoundException(String message) {
        super(message);
    }

}
