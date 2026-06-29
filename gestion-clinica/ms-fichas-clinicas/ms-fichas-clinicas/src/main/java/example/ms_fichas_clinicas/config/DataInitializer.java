package example.ms_fichas_clinicas.config;

import example.ms_fichas_clinicas.model.FichaClinica;
import example.ms_fichas_clinicas.repository.FichaClinicaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final FichaClinicaRepository fichaClinicaRepository;

    @Override
    public void run(String... args) {
        createFichaClinicaIfNotExists(
                1L,
                1L,
                1L,
                "Cefalea frecuente y cansancio",
                "Cefalea tensional asociada a estrés y sueño insuficiente",
                "Hidratación, higiene del sueño, control de estrés y analgesia simple si dolor",
                "Se recomienda reevaluar si aumenta frecuencia o intensidad",
                LocalDateTime.of(2026, 7, 6, 9, 35)
        );

        createFichaClinicaIfNotExists(
                2L,
                2L,
                3L,
                "Dolor torácico leve al esfuerzo",
                "Dolor torácico inespecífico, sin signos de alarma inmediatos",
                "Solicitud de electrocardiograma y control cardiológico posterior",
                "Paciente estable durante atención",
                LocalDateTime.of(2026, 7, 6, 12, 35)
        );

        createFichaClinicaIfNotExists(
                3L,
                3L,
                6L,
                "Fiebre, tos y congestión nasal",
                "Infección respiratoria alta probable viral",
                "Reposo, hidratación, control de temperatura y manejo sintomático",
                "Indicar consulta si fiebre persiste más de 48 horas",
                LocalDateTime.of(2026, 7, 8, 9, 35)
        );

        log.info("Datos iniciales de fichas clínicas verificados correctamente");
    }

    private void createFichaClinicaIfNotExists(
            Long pacienteId,
            Long medicoId,
            Long citaId,
            String motivoConsulta,
            String diagnostico,
            String tratamiento,
            String observaciones,
            LocalDateTime fechaRegistro
    ) {
        if (fichaClinicaRepository.existsByCitaId(citaId)) {
            log.info("Ficha clínica ya existe para cita ID: {}", citaId);
            return;
        }

        FichaClinica fichaClinica = FichaClinica.builder()
                .pacienteId(pacienteId)
                .medicoId(medicoId)
                .citaId(citaId)
                .motivoConsulta(motivoConsulta)
                .diagnostico(diagnostico)
                .tratamiento(tratamiento)
                .observaciones(observaciones)
                .fechaRegistro(fechaRegistro)
                .activo(true)
                .build();

        fichaClinicaRepository.save(fichaClinica);

        log.info("Ficha clínica creada para cita ID: {}", citaId);
    }
}