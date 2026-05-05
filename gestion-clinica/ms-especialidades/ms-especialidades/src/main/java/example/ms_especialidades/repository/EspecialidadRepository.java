package example.ms_especialidades.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import example.ms_especialidades.model.Especialidad;

public interface EspecialidadRepository extends JpaRepository<Especialidad, Long>{

    Optional<Especialidad> findByNombre(String nombre);

    boolean existsByNombre(String nombre);

    List<Especialidad> findByActivoTrue();

}
