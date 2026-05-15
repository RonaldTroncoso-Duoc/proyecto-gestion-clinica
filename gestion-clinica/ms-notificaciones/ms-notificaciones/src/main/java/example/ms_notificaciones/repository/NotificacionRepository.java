package example.ms_notificaciones.repository;

import example.ms_notificaciones.model.Notificacion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificacionRepository extends JpaRepository<Notificacion, Long> {

    List<Notificacion> findByPacienteId(Long pacienteId);

    List<Notificacion> findByEstado(String estado);

    List<Notificacion> findByTipo(String tipo);
}
