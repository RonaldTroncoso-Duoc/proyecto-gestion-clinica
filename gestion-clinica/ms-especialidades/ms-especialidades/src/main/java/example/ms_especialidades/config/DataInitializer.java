package example.ms_especialidades.config;

import example.ms_especialidades.model.Especialidad;
import example.ms_especialidades.repository.EspecialidadRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final EspecialidadRepository especialidadRepository;

    @Override
    public void run(String... args) {
        createEspecialidadIfNotExists(
                "Medicina General",
                "Atención médica primaria, controles preventivos y morbilidad general"
        );

        createEspecialidadIfNotExists(
                "Pediatría",
                "Atención médica integral para niños y adolescentes"
        );

        createEspecialidadIfNotExists(
                "Cardiología",
                "Diagnóstico y tratamiento de enfermedades cardiovasculares"
        );

        createEspecialidadIfNotExists(
                "Dermatología",
                "Diagnóstico y tratamiento de enfermedades de la piel"
        );

        log.info("Datos iniciales de especialidades verificados correctamente");
    }

    private void createEspecialidadIfNotExists(String nombre, String descripcion) {
        if (especialidadRepository.existsByNombreIgnoreCase(nombre)) {
            log.info("Especialidad ya existe: {}", nombre);
            return;
        }

        Especialidad especialidad = Especialidad.builder()
                .nombre(nombre)
                .descripcion(descripcion)
                .activo(true)
                .build();

        especialidadRepository.save(especialidad);

        log.info("Especialidad creada: {}", nombre);
    }
}