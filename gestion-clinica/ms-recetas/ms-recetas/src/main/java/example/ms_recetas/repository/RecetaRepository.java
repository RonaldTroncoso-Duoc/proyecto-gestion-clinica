package example.ms_recetas.repository;

import example.ms_recetas.model.Receta;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RecetaRepository extends JpaRepository<Receta, Long> {

    List<Receta> findByPacienteId(Long pacienteId);

    List<Receta> findByMedicoId(Long medicoId);

    List<Receta> findByFichaClinicaId(Long fichaClinicaId);

    List<Receta> findByActivaTrue();
}
