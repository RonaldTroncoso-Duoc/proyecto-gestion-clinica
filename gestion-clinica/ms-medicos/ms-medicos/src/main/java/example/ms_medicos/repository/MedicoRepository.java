package example.ms_medicos.repository;

import example.ms_medicos.model.Medico;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface MedicoRepository extends JpaRepository<Medico, Long> {

    Optional<Medico> findByRun(String run);

    Optional<Medico> findByEmailIgnoreCase(String email);

    Optional<Medico> findByAuthUserId(Long authUserId);

    boolean existsByRun(String run);

    boolean existsByEmailIgnoreCase(String email);

    boolean existsByAuthUserId(Long authUserId);

    List<Medico> findByActivoTrue();

    List<Medico> findByEspecialidadId(Long especialidadId);

    List<Medico> findByEspecialidadIdAndActivoTrue(Long especialidadId);
}