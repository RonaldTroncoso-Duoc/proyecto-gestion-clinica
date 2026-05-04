package example.ms_medicos.repository;

import example.ms_medicos.model.Medico;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface MedicoRepository extends JpaRepository<Medico, Long> {
    // Método extra para filtrar por especialidad si quieres
    List<Medico> findByEspecialidad(String especialidad);
}