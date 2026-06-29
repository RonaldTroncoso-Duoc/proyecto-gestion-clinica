package example.ms_horarios.config;

import example.ms_horarios.model.Horario;
import example.ms_horarios.repository.HorarioRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalTime;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final HorarioRepository horarioRepository;

    @Override
    public void run(String... args) {
        createHorarioIfNotExists(1L, LocalDate.of(2026, 7, 6), LocalTime.of(9, 0), LocalTime.of(9, 30), false);
        createHorarioIfNotExists(1L, LocalDate.of(2026, 7, 6), LocalTime.of(9, 30), LocalTime.of(10, 0), false);
        createHorarioIfNotExists(1L, LocalDate.of(2026, 7, 6), LocalTime.of(10, 0), LocalTime.of(10, 30), true);
        createHorarioIfNotExists(1L, LocalDate.of(2026, 7, 7), LocalTime.of(11, 0), LocalTime.of(11, 30), true);

        createHorarioIfNotExists(2L, LocalDate.of(2026, 7, 6), LocalTime.of(12, 0), LocalTime.of(12, 30), false);
        createHorarioIfNotExists(2L, LocalDate.of(2026, 7, 6), LocalTime.of(12, 30), LocalTime.of(13, 0), true);
        createHorarioIfNotExists(2L, LocalDate.of(2026, 7, 7), LocalTime.of(15, 0), LocalTime.of(15, 30), false);
        createHorarioIfNotExists(2L, LocalDate.of(2026, 7, 7), LocalTime.of(15, 30), LocalTime.of(16, 0), true);

        createHorarioIfNotExists(3L, LocalDate.of(2026, 7, 8), LocalTime.of(9, 0), LocalTime.of(9, 30), false);
        createHorarioIfNotExists(3L, LocalDate.of(2026, 7, 8), LocalTime.of(9, 30), LocalTime.of(10, 0), false);
        createHorarioIfNotExists(3L, LocalDate.of(2026, 7, 8), LocalTime.of(10, 0), LocalTime.of(10, 30), true);
        createHorarioIfNotExists(3L, LocalDate.of(2026, 7, 9), LocalTime.of(16, 0), LocalTime.of(16, 30), true);

        log.info("Datos iniciales de horarios verificados correctamente");
    }

    private void createHorarioIfNotExists(
            Long medicoId,
            LocalDate fecha,
            LocalTime horaInicio,
            LocalTime horaFin,
            Boolean disponible
    ) {
        if (horarioRepository.existsByMedicoIdAndFechaAndHoraInicioAndHoraFin(
                medicoId,
                fecha,
                horaInicio,
                horaFin
        )) {
            log.info(
                    "Horario ya existe para médico {} el {} de {} a {}",
                    medicoId,
                    fecha,
                    horaInicio,
                    horaFin
            );
            return;
        }

        Horario horario = Horario.builder()
                .medicoId(medicoId)
                .fecha(fecha)
                .horaInicio(horaInicio)
                .horaFin(horaFin)
                .disponible(disponible)
                .build();

        horarioRepository.save(horario);

        log.info(
                "Horario creado para médico {} el {} de {} a {}",
                medicoId,
                fecha,
                horaInicio,
                horaFin
        );
    }
}