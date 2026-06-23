package example.ms_pacientes.repository;

import example.ms_pacientes.model.Paciente;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PacienteRepository extends JpaRepository<Paciente, Long> {

    Optional<Paciente> findByRun(String run);

    Optional<Paciente> findByEmailIgnoreCase(String email);

    Optional<Paciente> findByAuthUserId(Long authUserId);

    boolean existsByRun(String run);

    boolean existsByEmailIgnoreCase(String email);

    boolean existsByAuthUserId(Long authUserId);

    List<Paciente> findByActivoTrue();
}