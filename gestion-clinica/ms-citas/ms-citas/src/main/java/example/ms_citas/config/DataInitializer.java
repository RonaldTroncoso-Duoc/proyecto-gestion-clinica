package example.ms_citas.config;

import example.ms_citas.model.Cita;
import example.ms_citas.repository.CitaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final CitaRepository citaRepository;

    @Override
    public void run(String... args) {
        createCitaIfNotExists(
                1L,
                1L,
                1L,
                "Control general por cefalea frecuente y cansancio",
                "REALIZADA",
                LocalDateTime.of(2026, 7, 1, 8, 30)
        );

        createCitaIfNotExists(
                2L,
                1L,
                2L,
                "Control preventivo anual",
                "AGENDADA",
                LocalDateTime.of(2026, 7, 1, 9, 0)
        );

        createCitaIfNotExists(
                2L,
                2L,
                5L,
                "Evaluación por dolor torácico leve al esfuerzo",
                "REALIZADA",
                LocalDateTime.of(2026, 7, 1, 10, 0)
        );

        createCitaIfNotExists(
                1L,
                2L,
                6L,
                "Control cardiológico solicitado por antecedentes familiares",
                "CANCELADA",
                LocalDateTime.of(2026, 7, 1, 10, 30)
        );

        createCitaIfNotExists(
                1L,
                2L,
                7L,
                "Revisión de resultados de exámenes cardiovasculares",
                "AGENDADA",
                LocalDateTime.of(2026, 7, 1, 11, 0)
        );

        createCitaIfNotExists(
                3L,
                3L,
                9L,
                "Fiebre, tos y congestión nasal de tres días",
                "REALIZADA",
                LocalDateTime.of(2026, 7, 1, 12, 0)
        );

        createCitaIfNotExists(
                3L,
                3L,
                10L,
                "Control pediátrico posterior a cuadro respiratorio",
                "AGENDADA",
                LocalDateTime.of(2026, 7, 1, 12, 30)
        );

        log.info("Datos iniciales de citas verificados correctamente");
    }

    private void createCitaIfNotExists(
            Long pacienteId,
            Long medicoId,
            Long horarioId,
            String motivo,
            String estado,
            LocalDateTime fechaCreacion
    ) {
        if (citaRepository.findByHorarioId(horarioId).isPresent()) {
            log.info("Cita ya existe para horario ID: {}", horarioId);
            return;
        }

        Cita cita = Cita.builder()
                .pacienteId(pacienteId)
                .medicoId(medicoId)
                .horarioId(horarioId)
                .motivo(motivo)
                .estado(estado)
                .fechaCreacion(fechaCreacion)
                .build();

        citaRepository.save(cita);

        log.info("Cita creada para horario ID: {}", horarioId);
    }
}