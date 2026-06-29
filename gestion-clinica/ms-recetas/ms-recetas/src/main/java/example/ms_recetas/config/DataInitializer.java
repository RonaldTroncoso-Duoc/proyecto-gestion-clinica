package example.ms_recetas.config;

import example.ms_recetas.model.Receta;
import example.ms_recetas.repository.RecetaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final RecetaRepository recetaRepository;

    @Override
    public void run(String... args) {
        createRecetaIfNotExists(
                1L,
                1L,
                1L,
                "Paracetamol 500 mg",
                "1 comprimido cada 8 horas si dolor",
                "Tomar solo en caso de cefalea, máximo 3 comprimidos al día",
                3,
                LocalDateTime.of(2026, 7, 6, 9, 40)
        );

        createRecetaIfNotExists(
                2L,
                2L,
                2L,
                "Ácido acetilsalicílico 100 mg",
                "1 comprimido al día",
                "Tomar después del desayuno hasta control cardiológico",
                30,
                LocalDateTime.of(2026, 7, 6, 12, 40)
        );

        createRecetaIfNotExists(
                3L,
                3L,
                3L,
                "Paracetamol pediátrico 120 mg/5 ml",
                "10 ml cada 8 horas si fiebre",
                "Administrar solo si temperatura mayor o igual a 38°C",
                3,
                LocalDateTime.of(2026, 7, 8, 9, 40)
        );

        log.info("Datos iniciales de recetas verificados correctamente");
    }

    private void createRecetaIfNotExists(
            Long fichaClinicaId,
            Long pacienteId,
            Long medicoId,
            String medicamento,
            String dosis,
            String indicaciones,
            Integer duracionDias,
            LocalDateTime fechaEmision
    ) {
        if (!recetaRepository.findByFichaClinicaId(fichaClinicaId).isEmpty()) {
            log.info("Receta ya existe para ficha clínica ID: {}", fichaClinicaId);
            return;
        }

        Receta receta = Receta.builder()
                .fichaClinicaId(fichaClinicaId)
                .pacienteId(pacienteId)
                .medicoId(medicoId)
                .medicamento(medicamento)
                .dosis(dosis)
                .indicaciones(indicaciones)
                .duracionDias(duracionDias)
                .fechaEmision(fechaEmision)
                .activa(true)
                .build();

        recetaRepository.save(receta);

        log.info("Receta creada para ficha clínica ID: {}", fichaClinicaId);
    }
}