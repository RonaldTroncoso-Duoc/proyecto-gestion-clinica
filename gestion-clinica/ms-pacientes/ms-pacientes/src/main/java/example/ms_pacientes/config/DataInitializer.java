package example.ms_pacientes.config;

import example.ms_pacientes.model.Paciente;
import example.ms_pacientes.repository.PacienteRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final PacienteRepository pacienteRepository;

    @Override
    public void run(String... args) {
        createPacienteIfNotExists(
                2L,
                "20111222-3",
                "Camila",
                "Torres",
                "camila.torres@clinicademo.cl",
                "+56991234501",
                LocalDate.of(1992, 4, 18),
                "Av. Providencia 1200, Santiago"
        );

        createPacienteIfNotExists(
                3L,
                "18777666-4",
                "Mario",
                "Reyes",
                "mario.reyes@clinicademo.cl",
                "+56991234502",
                LocalDate.of(1980, 9, 5),
                "Los Aromos 455, Ñuñoa"
        );

        createPacienteIfNotExists(
                4L,
                "22333444-5",
                "Valentina",
                "Soto",
                "valentina.soto@clinicademo.cl",
                "+56991234503",
                LocalDate.of(2016, 11, 22),
                "Pasaje Las Flores 78, Maipú"
        );

        log.info("Datos iniciales de pacientes verificados correctamente");
    }

    private void createPacienteIfNotExists(
            Long authUserId,
            String run,
            String nombre,
            String apellido,
            String email,
            String telefono,
            LocalDate fechaNacimiento,
            String direccion
    ) {
        if (pacienteRepository.existsByRun(run)) {
            log.info("Paciente ya existe con RUN: {}", run);
            return;
        }

        if (pacienteRepository.existsByEmailIgnoreCase(email)) {
            log.warn(
                    "Inconsistencia de datos semilla: el email {} ya existe pero el RUN {} no existe. No se creará el paciente.",
                    email,
                    run
            );
            return;
        }

        if (pacienteRepository.existsByAuthUserId(authUserId)) {
            log.warn(
                    "Inconsistencia de datos semilla: el authUserId {} ya existe pero el RUN {} no existe. No se creará el paciente.",
                    authUserId,
                    run
            );
            return;
        }

        Paciente paciente = Paciente.builder()
                .authUserId(authUserId)
                .run(run)
                .nombre(nombre)
                .apellido(apellido)
                .email(email)
                .telefono(telefono)
                .fechaNacimiento(fechaNacimiento)
                .direccion(direccion)
                .activo(true)
                .build();

        pacienteRepository.save(paciente);

        log.info("Paciente creado con RUN: {}", run);
    }
}