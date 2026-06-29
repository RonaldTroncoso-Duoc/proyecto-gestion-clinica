package example.ms_notificaciones.config;

import example.ms_notificaciones.model.Notificacion;
import example.ms_notificaciones.repository.NotificacionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final NotificacionRepository notificacionRepository;

    @Override
    public void run(String... args) {
        createNotificacionIfNotExists(
                1L,
                1L,
                "CONFIRMACION_CITA",
                "Su cita con Dra. Andrea Morales fue registrada para el 2026-07-06 09:00.",
                "ENVIADA",
                LocalDateTime.of(2026, 7, 1, 8, 31)
        );

        createNotificacionIfNotExists(
                1L,
                1L,
                "PAGO",
                "Pago recibido correctamente para su cita de Medicina General.",
                "ENVIADA",
                LocalDateTime.of(2026, 7, 6, 9, 46)
        );

        createNotificacionIfNotExists(
                2L,
                2L,
                "RECORDATORIO_CITA",
                "Recuerde su cita de control preventivo con Dra. Andrea Morales el 2026-07-06 09:30.",
                "PENDIENTE",
                LocalDateTime.of(2026, 7, 5, 9, 0)
        );

        createNotificacionIfNotExists(
                2L,
                3L,
                "PAGO",
                "Pago recibido correctamente para su atención cardiológica.",
                "ENVIADA",
                LocalDateTime.of(2026, 7, 6, 12, 46)
        );

        createNotificacionIfNotExists(
                1L,
                4L,
                "CANCELACION_CITA",
                "Su cita cardiológica del 2026-07-06 12:30 fue cancelada.",
                "ENVIADA",
                LocalDateTime.of(2026, 7, 1, 10, 40)
        );

        createNotificacionIfNotExists(
                1L,
                5L,
                "RECORDATORIO_CITA",
                "Recuerde su cita con Dr. Rodrigo Fuentes el 2026-07-07 15:00.",
                "PENDIENTE",
                LocalDateTime.of(2026, 7, 6, 15, 0)
        );

        createNotificacionIfNotExists(
                3L,
                6L,
                "PAGO",
                "Pago recibido correctamente para la atención pediátrica.",
                "ENVIADA",
                LocalDateTime.of(2026, 7, 8, 9, 46)
        );

        createNotificacionIfNotExists(
                3L,
                7L,
                "RECORDATORIO_CITA",
                "Recuerde el control pediátrico con Dra. Paula Herrera el 2026-07-08 09:30.",
                "FALLIDA",
                LocalDateTime.of(2026, 7, 7, 9, 30)
        );

        log.info("Datos iniciales de notificaciones verificados correctamente");
    }

    private void createNotificacionIfNotExists(
            Long pacienteId,
            Long citaId,
            String tipo,
            String mensaje,
            String estado,
            LocalDateTime fechaEnvio
    ) {
        boolean exists = notificacionRepository.findAll()
                .stream()
                .anyMatch(notificacion ->
                        notificacion.getPacienteId().equals(pacienteId)
                                && notificacion.getCitaId().equals(citaId)
                                && notificacion.getTipo().equals(tipo)
                                && notificacion.getMensaje().equals(mensaje)
                );

        if (exists) {
            log.info("Notificación ya existe para cita ID: {} y tipo: {}", citaId, tipo);
            return;
        }

        Notificacion notificacion = Notificacion.builder()
                .pacienteId(pacienteId)
                .citaId(citaId)
                .tipo(tipo)
                .mensaje(mensaje)
                .estado(estado)
                .fechaEnvio(fechaEnvio)
                .build();

        notificacionRepository.save(notificacion);

        log.info("Notificación creada para cita ID: {} y tipo: {}", citaId, tipo);
    }
}