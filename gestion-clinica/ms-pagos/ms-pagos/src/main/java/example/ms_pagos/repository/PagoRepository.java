package example.ms_pagos.repository;

import example.ms_pagos.model.Pago;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PagoRepository extends JpaRepository<Pago, Long> {

    List<Pago> findByPacienteId(Long pacienteId);

    List<Pago> findByEstado(String estado);

    Optional<Pago> findByCitaId(Long citaId);

    boolean existsByCitaId(Long citaId);
}
