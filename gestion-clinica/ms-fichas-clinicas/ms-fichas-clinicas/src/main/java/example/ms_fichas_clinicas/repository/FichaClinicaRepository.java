package example.ms_fichas_clinicas.repository;

import example.ms_fichas_clinicas.model.FichaClinica;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FichaClinicaRepository extends JpaRepository<FichaClinica, Long> {

    List<FichaClinica> findByPacienteId(Long pacienteId);

    List<FichaClinica> findByMedicoId(Long medicoId);

    Optional<FichaClinica> findByCitaId(Long citaId);

    boolean existsByCitaId(Long citaId);

    List<FichaClinica> findByActivoTrue();
}
