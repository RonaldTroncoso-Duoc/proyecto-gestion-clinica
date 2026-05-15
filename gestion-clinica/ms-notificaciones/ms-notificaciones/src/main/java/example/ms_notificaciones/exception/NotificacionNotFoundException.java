package example.ms_notificaciones.exception;

public class NotificacionNotFoundException extends RuntimeException {

    public NotificacionNotFoundException(String mensaje) {
        super(mensaje);
    }
}
