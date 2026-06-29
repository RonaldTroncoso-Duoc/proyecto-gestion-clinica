package example.ms_medicos.config;

import example.ms_medicos.model.Medico;
import example.ms_medicos.repository.MedicoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final MedicoRepository medicoRepository;

    @Override
    public void run(String... args) {
        createMedicoIfNotExists(
                5L,
                "12345678-5",
                "Andrea",
                "Morales",
                "andrea.morales@clinicademo.cl",
                "+56981234501",
                1L
        );

        createMedicoIfNotExists(
                6L,
                "13456789-6",
                "Rodrigo",
                "Fuentes",
                "rodrigo.fuentes@clinicademo.cl",
                "+56981234502",
                3L
        );

        createMedicoIfNotExists(
                7L,
                "14567890-7",
                "Paula",
                "Herrera",
                "paula.herrera@clinicademo.cl",
                "+56981234503",
                2L
        );

        log.info("Datos iniciales de médicos verificados correctamente");
    }

    private void createMedicoIfNotExists(
            Long authUserId,
            String run,
            String nombre,
            String apellido,
            String email,
            String telefono,
            Long especialidadId
    ) {
        if (medicoRepository.existsByRun(run)) {
            log.info("Médico ya existe con RUN: {}", run);
            return;
        }

        if (medicoRepository.existsByEmailIgnoreCase(email)) {
            log.warn(
                    "Inconsistencia de datos semilla: el email {} ya existe pero el RUN {} no existe. No se creará el médico.",
                    email,
                    run
            );
            return;
        }

        if (medicoRepository.existsByAuthUserId(authUserId)) {
            log.warn(
                    "Inconsistencia de datos semilla: el authUserId {} ya existe pero el RUN {} no existe. No se creará el médico.",
                    authUserId,
                    run
            );
            return;
        }

        Medico medico = Medico.builder()
                .authUserId(authUserId)
                .run(run)
                .nombre(nombre)
                .apellido(apellido)
                .email(email)
                .telefono(telefono)
                .especialidadId(especialidadId)
                .activo(true)
                .build();

        medicoRepository.save(medico);

        log.info("Médico creado con RUN: {}", run);
    }
}