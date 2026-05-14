package example.ms_horarios.repository;

import example.ms_horarios.model.Horario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public interface HorarioRepository extends JpaRepository<Horario, Long> {

    List<Horario> findByMedicoId(Long medicoId);

    List<Horario> findByDisponibleTrue();

    List<Horario> findByMedicoIdAndDisponibleTrue(Long medicoId);

    boolean existsByMedicoIdAndFechaAndHoraInicioAndHoraFin(
            Long medicoId,
            LocalDate fecha,
            LocalTime horaInicio,
            LocalTime horaFin
    );
}
