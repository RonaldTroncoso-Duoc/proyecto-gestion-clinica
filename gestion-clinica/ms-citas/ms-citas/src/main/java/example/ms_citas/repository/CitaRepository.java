package example.ms_citas.repository;

import example.ms_citas.model.Cita;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CitaRepository extends JpaRepository<Cita, Long> {
    // Aquí podrías agregar métodos como findByPacienteId si lo necesitaras
}