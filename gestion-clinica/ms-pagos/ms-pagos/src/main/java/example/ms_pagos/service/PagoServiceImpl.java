package example.ms_pagos.service;

import example.ms_pagos.client.CitaClient;
import example.ms_pagos.client.PacienteClient;
import example.ms_pagos.dto.PagoRequestDTO;
import example.ms_pagos.dto.PagoResponseDTO;
import example.ms_pagos.exception.PagoNotFoundException;
import example.ms_pagos.model.Pago;
import example.ms_pagos.repository.PagoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class PagoServiceImpl implements PagoService {

    private final PagoRepository repository;
    private final PacienteClient pacienteClient;
    private final CitaClient citaClient;

    private static final String ESTADO_PENDIENTE = "PENDIENTE";
    private static final String ESTADO_PAGADO = "PAGADO";
    private static final String ESTADO_ANULADO = "ANULADO";

    @Override
    public List<PagoResponseDTO> listarTodos() {
        log.info("Listando todos los pagos");
        return repository.findAll()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public List<PagoResponseDTO> listarPorPaciente(Long pacienteId) {
        log.info("Listando pagos del paciente ID: {}", pacienteId);

        pacienteClient.obtenerPaciente(pacienteId);

        return repository.findByPacienteId(pacienteId)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public List<PagoResponseDTO> listarPorEstado(String estado) {
        log.info("Listando pagos por estado: {}", estado);

        return repository.findByEstado(estado.toUpperCase())
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public PagoResponseDTO buscarPorId(Long id) {
        log.info("Buscando pago por ID: {}", id);

        Pago pago = obtenerEntidadPorId(id);

        return mapToResponse(pago);
    }

    @Override
    public PagoResponseDTO buscarPorCita(Long citaId) {
        log.info("Buscando pago por cita ID: {}", citaId);

        citaClient.obtenerCita(citaId);

        Pago pago = repository.findByCitaId(citaId)
                .orElseThrow(() -> {
                    log.warn("Pago no encontrado para cita ID: {}", citaId);
                    return new PagoNotFoundException("Pago para cita ID " + citaId + " no encontrado");
                });

        return mapToResponse(pago);
    }

    @Override
    public PagoResponseDTO crear(PagoRequestDTO dto) {
        log.info("Creando pago para paciente ID: {} y cita ID: {}", dto.getPacienteId(), dto.getCitaId());

        pacienteClient.obtenerPaciente(dto.getPacienteId());
        citaClient.obtenerCita(dto.getCitaId());

        if (repository.existsByCitaId(dto.getCitaId())) {
            log.warn("Intento de crear pago duplicado para cita ID: {}", dto.getCitaId());
            throw new RuntimeException("Ya existe un pago registrado para esta cita");
        }

        Pago pago = Pago.builder()
                .pacienteId(dto.getPacienteId())
                .citaId(dto.getCitaId())
                .monto(dto.getMonto())
                .metodoPago(dto.getMetodoPago().trim().toUpperCase())
                .estado(ESTADO_PENDIENTE)
                .fechaRegistro(LocalDateTime.now())
                .build();

        Pago guardado = repository.save(pago);

        log.info("Pago creado correctamente con ID: {}", guardado.getId());

        return mapToResponse(guardado);
    }

    @Override
    public PagoResponseDTO actualizar(Long id, PagoRequestDTO dto) {
        log.info("Actualizando pago ID: {}", id);

        Pago pago = obtenerEntidadPorId(id);

        if (pago.getEstado().equals(ESTADO_PAGADO)) {
            log.warn("Intento de actualizar pago ya confirmado ID: {}", id);
            throw new RuntimeException("No se puede actualizar un pago ya confirmado");
        }

        pacienteClient.obtenerPaciente(dto.getPacienteId());
        citaClient.obtenerCita(dto.getCitaId());

        repository.findByCitaId(dto.getCitaId())
                .ifPresent(p -> {
                    if (!p.getId().equals(id)) {
                        log.warn("Intento de actualizar pago con cita duplicada ID: {}", dto.getCitaId());
                        throw new RuntimeException("Ya existe otro pago registrado para esta cita");
                    }
                });

        pago.setPacienteId(dto.getPacienteId());
        pago.setCitaId(dto.getCitaId());
        pago.setMonto(dto.getMonto());
        pago.setMetodoPago(dto.getMetodoPago().trim().toUpperCase());

        Pago actualizado = repository.save(pago);

        log.info("Pago actualizado correctamente ID: {}", actualizado.getId());

        return mapToResponse(actualizado);
    }

    @Override
    public PagoResponseDTO confirmar(Long id) {
        log.info("Confirmando pago ID: {}", id);

        Pago pago = obtenerEntidadPorId(id);

        if (pago.getEstado().equals(ESTADO_ANULADO)) {
            log.warn("Intento de confirmar pago anulado ID: {}", id);
            throw new RuntimeException("No se puede confirmar un pago anulado");
        }

        pago.setEstado(ESTADO_PAGADO);

        Pago actualizado = repository.save(pago);

        log.info("Pago confirmado correctamente ID: {}", id);

        return mapToResponse(actualizado);
    }

    @Override
    public PagoResponseDTO anular(Long id) {
        log.info("Anulando pago ID: {}", id);

        Pago pago = obtenerEntidadPorId(id);

        if (pago.getEstado().equals(ESTADO_PAGADO)) {
            log.warn("Intento de anular pago ya confirmado ID: {}", id);
            throw new RuntimeException("No se puede anular un pago ya confirmado");
        }

        pago.setEstado(ESTADO_ANULADO);

        Pago actualizado = repository.save(pago);

        log.info("Pago anulado correctamente ID: {}", id);

        return mapToResponse(actualizado);
    }

    @Override
    public void eliminar(Long id) {
        log.info("Eliminando pago ID: {}", id);

        Pago pago = obtenerEntidadPorId(id);

        repository.delete(pago);

        log.info("Pago eliminado correctamente ID: {}", id);
    }

    private Pago obtenerEntidadPorId(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Pago no encontrado con ID: {}", id);
                    return new PagoNotFoundException("Pago con ID " + id + " no encontrado");
                });
    }

    private PagoResponseDTO mapToResponse(Pago pago) {
        return PagoResponseDTO.builder()
                .id(pago.getId())
                .pacienteId(pago.getPacienteId())
                .citaId(pago.getCitaId())
                .monto(pago.getMonto())
                .metodoPago(pago.getMetodoPago())
                .estado(pago.getEstado())
                .fechaRegistro(pago.getFechaRegistro())
                .build();
    }
}
