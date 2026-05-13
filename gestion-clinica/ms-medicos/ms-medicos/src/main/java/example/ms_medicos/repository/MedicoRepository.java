package example.ms_medicos.repository;

import example.ms_medicos.model.Medico;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface MedicoRepository extends JpaRepository<Medico, Long> {
    // Métodos
    Optional<Medico> findByRun(String run);

    Optional<Medico> findByEmailIgnoreCase(String email);

    boolean existsByRun(String run);

    boolean existsByEmailIgnoreCase(String email);

    List<Medico> findByActivoTrue();

    List<Medico> findByEspecialidadId(Long especialidadId);

    List<Medico> findByEspecialidadIdAndActivoTrue(Long especialidadId);
}