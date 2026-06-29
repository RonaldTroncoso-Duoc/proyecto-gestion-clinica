package example.ms_pagos.config;

import example.ms_pagos.model.Pago;
import example.ms_pagos.repository.PagoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final PagoRepository pagoRepository;

    @Override
    public void run(String... args) {
        createPagoIfNotExists(1L, 1L, 25000, "TARJETA", "PAGADO", LocalDateTime.of(2026, 7, 6, 9, 45));
        createPagoIfNotExists(2L, 2L, 22000, "TRANSFERENCIA", "PENDIENTE", LocalDateTime.of(2026, 7, 1, 9, 5));
        createPagoIfNotExists(2L, 3L, 40000, "TARJETA", "PAGADO", LocalDateTime.of(2026, 7, 6, 12, 45));
        createPagoIfNotExists(1L, 4L, 40000, "TRANSFERENCIA", "ANULADO", LocalDateTime.of(2026, 7, 1, 10, 35));
        createPagoIfNotExists(1L, 5L, 40000, "EFECTIVO", "PENDIENTE", LocalDateTime.of(2026, 7, 1, 11, 5));
        createPagoIfNotExists(3L, 6L, 30000, "TARJETA", "PAGADO", LocalDateTime.of(2026, 7, 8, 9, 45));
        createPagoIfNotExists(3L, 7L, 30000, "TRANSFERENCIA", "PENDIENTE", LocalDateTime.of(2026, 7, 1, 12, 35));

        log.info("Datos iniciales de pagos verificados correctamente");
    }

    private void createPagoIfNotExists(
            Long pacienteId,
            Long citaId,
            Integer monto,
            String metodoPago,
            String estado,
            LocalDateTime fechaRegistro
    ) {
        if (pagoRepository.existsByCitaId(citaId)) {
            log.info("Pago ya existe para cita ID: {}", citaId);
            return;
        }

        Pago pago = Pago.builder()
                .pacienteId(pacienteId)
                .citaId(citaId)
                .monto(monto)
                .metodoPago(metodoPago)
                .estado(estado)
                .fechaRegistro(fechaRegistro)
                .build();

        pagoRepository.save(pago);

        log.info("Pago creado para cita ID: {}", citaId);
    }
}