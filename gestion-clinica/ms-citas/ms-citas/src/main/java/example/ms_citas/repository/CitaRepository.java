package example.ms_citas.repository;

import example.ms_citas.model.Cita;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CitaRepository extends JpaRepository<Cita, Long> {

    List<Cita> findByPacienteId(Long pacienteId);

    List<Cita> findByMedicoId(Long medicoId);

    List<Cita> findByEstado(String estado);

    Optional<Cita> findByHorarioId(Long horarioId);

    boolean existsByHorarioIdAndEstado(Long horarioId, String estado);
}