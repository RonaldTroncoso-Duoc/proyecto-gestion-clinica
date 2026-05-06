package example.ms_horarios.repository;

import java.time.*;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import example.ms_horarios.model.Horario;

public interface HorarioRepository extends JpaRepository<Horario, Long> {
    
    List<Horario> findByMedicoId(Long medicoId);

    List<Horario> findByMedicoIdDisponibleTrue(Long medicoId);

    List<Horario> findByDisponibleTrue();

    boolean existsByMedicoIdFechaHorario(
        Long medicoId,
        LocalDate fecha,
        LocalTime horaInicio,
        LocalTime horaTermino
    );

}
