package example.ms_pacientes.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import example.ms_pacientes.model.Paciente;

public interface PacienteRepository extends JpaRepository<Paciente, Long> {

    Optional<Paciente> findByRut(String rut);

}
