package example.ms_pagos.service;

import example.ms_pagos.client.CitaClient;
import example.ms_pagos.client.PacienteClient;
import example.ms_pagos.dto.PagoRequestDTO;
import example.ms_pagos.dto.PagoResponseDTO;
import example.ms_pagos.exception.PagoNotFoundException;
import example.ms_pagos.model.Pago;
import example.ms_pagos.repository.PagoRepository;
import feign.FeignException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Pruebas unitarias PagoServiceImpl.
 * NOTA: Se eliminó MsPagosApplicationTests.java (@SpringBootTest).
 * COMANDO: mvn -pl ms-pagos -Dtest=PagoServiceImplTest test
 * REPORTE: ms-pagos/target/surefire-reports/
 */
@ExtendWith(MockitoExtension.class)
class PagoServiceImplTest {

    @Mock private PagoRepository repository;
    @Mock private PacienteClient pacienteClient;
    @Mock private CitaClient citaClient;
    @InjectMocks private PagoServiceImpl service;

    private Pago pago(Long id, Long pacienteId, Long citaId, Integer monto, String metodo, String estado) {
        return Pago.builder().id(id).pacienteId(pacienteId).citaId(citaId)
                .monto(monto).metodoPago(metodo).estado(estado)
                .fechaRegistro(LocalDateTime.of(2026,7,1,10,0)).build();
    }

    private PagoRequestDTO request(Long pacienteId, Long citaId, Integer monto, String metodo) {
        PagoRequestDTO dto = new PagoRequestDTO();
        dto.setPacienteId(pacienteId); dto.setCitaId(citaId);
        dto.setMonto(monto); dto.setMetodoPago(metodo);
        return dto;
    }

    @Test void listarTodos_debeRetornarPagosMapeados() {
        when(repository.findAll()).thenReturn(List.of(pago(1L,10L,20L,25000,"TARJETA","PENDIENTE")));
        List<PagoResponseDTO> r = service.listarTodos();
        assertThat(r).hasSize(1);
        assertThat(r.get(0).getId()).isEqualTo(1L);
        verify(repository).findAll();
    }

    @Test void listarPorPaciente_debeValidarPacienteYRetornarPagos() {
        when(pacienteClient.obtenerPaciente(10L)).thenReturn(new Object());
        when(repository.findByPacienteId(10L)).thenReturn(List.of(pago(2L,10L,20L,25000,"TARJETA","PENDIENTE")));
        List<PagoResponseDTO> r = service.listarPorPaciente(10L);
        assertThat(r.get(0).getPacienteId()).isEqualTo(10L);
        verify(pacienteClient).obtenerPaciente(10L);
    }

    @Test void listarPorEstado_debeRetornarPagosConEstadoUppercase() {
        when(repository.findByEstado("PENDIENTE")).thenReturn(List.of(pago(3L,10L,20L,25000,"TARJETA","PENDIENTE")));
        List<PagoResponseDTO> r = service.listarPorEstado("pendiente");
        assertThat(r.get(0).getEstado()).isEqualTo("PENDIENTE");
        verify(repository).findByEstado("PENDIENTE");
    }

    @Test void buscarPorId_debeRetornarPagoMapeado() {
        when(repository.findById(4L)).thenReturn(Optional.of(pago(4L,10L,20L,25000,"TARJETA","PENDIENTE")));
        PagoResponseDTO r = service.buscarPorId(4L);
        assertThat(r.getId()).isEqualTo(4L);
        verify(repository).findById(4L);
    }

    @Test void buscarPorCita_debeValidarCitaYRetornarPago() {
        when(citaClient.obtenerCita(20L)).thenReturn(new Object());
        when(repository.findByCitaId(20L)).thenReturn(Optional.of(pago(5L,10L,20L,25000,"TARJETA","PENDIENTE")));
        PagoResponseDTO r = service.buscarPorCita(20L);
        assertThat(r.getCitaId()).isEqualTo(20L);
        verify(citaClient).obtenerCita(20L);
    }

    @Test void crear_debeValidarFeignsGuardarYRetornarPendiente() {
        PagoRequestDTO dto = request(10L,20L,25000," tarjeta ");
        when(pacienteClient.obtenerPaciente(10L)).thenReturn(new Object());
        when(citaClient.obtenerCita(20L)).thenReturn(new Object());
        when(repository.existsByCitaId(20L)).thenReturn(false);
        when(repository.save(any(Pago.class))).thenReturn(pago(6L,10L,20L,25000,"TARJETA","PENDIENTE"));
        PagoResponseDTO r = service.crear(dto);
        assertThat(r.getMetodoPago()).isEqualTo("TARJETA");
        assertThat(r.getEstado()).isEqualTo("PENDIENTE");
        verify(repository).save(any(Pago.class));
    }

    @Test void actualizar_debeGuardarCambios() {
        PagoRequestDTO dto = request(11L,21L,30000," efectivo ");
        Pago existente = pago(7L,10L,20L,25000,"TARJETA","PENDIENTE");
        when(repository.findById(7L)).thenReturn(Optional.of(existente));
        when(pacienteClient.obtenerPaciente(11L)).thenReturn(new Object());
        when(citaClient.obtenerCita(21L)).thenReturn(new Object());
        when(repository.findByCitaId(21L)).thenReturn(Optional.empty());
        when(repository.save(existente)).thenReturn(existente);
        PagoResponseDTO r = service.actualizar(7L, dto);
        assertThat(r.getPacienteId()).isEqualTo(11L);
        assertThat(r.getMetodoPago()).isEqualTo("EFECTIVO");
        verify(repository).save(existente);
    }

    @Test void confirmar_debeCambiarEstadoAPagado() {
        Pago p = pago(8L,10L,20L,25000,"TARJETA","PENDIENTE");
        when(repository.findById(8L)).thenReturn(Optional.of(p));
        when(repository.save(p)).thenReturn(p);
        assertThat(service.confirmar(8L).getEstado()).isEqualTo("PAGADO");
    }

    @Test void anular_debeCambiarEstadoAAnulado() {
        Pago p = pago(9L,10L,20L,25000,"TARJETA","PENDIENTE");
        when(repository.findById(9L)).thenReturn(Optional.of(p));
        when(repository.save(p)).thenReturn(p);
        assertThat(service.anular(9L).getEstado()).isEqualTo("ANULADO");
    }

    @Test void eliminar_debeBuscarYEliminar() {
        Pago p = pago(10L,10L,20L,25000,"TARJETA","PENDIENTE");
        when(repository.findById(10L)).thenReturn(Optional.of(p));
        service.eliminar(10L);
        verify(repository).delete(p);
    }

    @Test void buscarPorId_cuandoNoExisteDebeLanzarNotFound() {
        when(repository.findById(99L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> service.buscarPorId(99L))
                .isInstanceOf(PagoNotFoundException.class)
                .hasMessage("Pago con ID 99 no encontrado");
    }

    @Test void buscarPorCita_cuandoNoHayPagoDebeLanzarNotFound() {
        when(citaClient.obtenerCita(999L)).thenReturn(new Object());
        when(repository.findByCitaId(999L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> service.buscarPorCita(999L))
                .isInstanceOf(PagoNotFoundException.class)
                .hasMessage("Pago para cita ID 999 no encontrado");
    }

    @Test void listarPorPaciente_cuandoPacienteNoExisteDebeLanzarFeignException() {
        when(pacienteClient.obtenerPaciente(999L)).thenThrow(FeignException.NotFound.class);
        assertThatThrownBy(() -> service.listarPorPaciente(999L)).isInstanceOf(FeignException.NotFound.class);
        verify(repository, never()).findByPacienteId(any());
    }

    @Test void crear_cuandoCitaDuplicadaDebeLanzarRuntimeException() {
        PagoRequestDTO dto = request(10L,20L,25000,"tarjeta");
        when(pacienteClient.obtenerPaciente(10L)).thenReturn(new Object());
        when(citaClient.obtenerCita(20L)).thenReturn(new Object());
        when(repository.existsByCitaId(20L)).thenReturn(true);
        assertThatThrownBy(() -> service.crear(dto))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Ya existe un pago registrado para esta cita");
        verify(repository, never()).save(any());
    }

    @Test void crear_cuandoCitaNoExisteDebeLanzarFeignException() {
        PagoRequestDTO dto = request(10L,999L,25000,"tarjeta");
        when(pacienteClient.obtenerPaciente(10L)).thenReturn(new Object());
        when(citaClient.obtenerCita(999L)).thenThrow(FeignException.NotFound.class);
        assertThatThrownBy(() -> service.crear(dto)).isInstanceOf(FeignException.NotFound.class);
        verify(repository, never()).save(any());
    }

    @Test void actualizar_cuandoPagoConfirmadoDebeLanzarRuntimeException() {
        PagoRequestDTO dto = request(10L,20L,25000,"tarjeta");
        when(repository.findById(7L)).thenReturn(Optional.of(pago(7L,10L,20L,25000,"TARJETA","PAGADO")));
        assertThatThrownBy(() -> service.actualizar(7L, dto))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("No se puede actualizar un pago ya confirmado");
        verify(repository, never()).save(any());
    }

    @Test void actualizar_cuandoCitaDuplicadaEnOtroPagoDebeLanzarRuntimeException() {
        PagoRequestDTO dto = request(11L,21L,30000,"efectivo");
        Pago existente = pago(7L,10L,20L,25000,"TARJETA","PENDIENTE");
        Pago otro = pago(99L,11L,21L,30000,"EFECTIVO","PENDIENTE");
        when(repository.findById(7L)).thenReturn(Optional.of(existente));
        when(pacienteClient.obtenerPaciente(11L)).thenReturn(new Object());
        when(citaClient.obtenerCita(21L)).thenReturn(new Object());
        when(repository.findByCitaId(21L)).thenReturn(Optional.of(otro));
        assertThatThrownBy(() -> service.actualizar(7L, dto))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Ya existe otro pago registrado para esta cita");
        verify(repository, never()).save(any());
    }

    @Test void confirmar_cuandoPagoAnuladoDebeLanzarRuntimeException() {
        when(repository.findById(8L)).thenReturn(Optional.of(pago(8L,10L,20L,25000,"TARJETA","ANULADO")));
        assertThatThrownBy(() -> service.confirmar(8L))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("No se puede confirmar un pago anulado");
    }

    @Test void anular_cuandoPagoConfirmadoDebeLanzarRuntimeException() {
        when(repository.findById(9L)).thenReturn(Optional.of(pago(9L,10L,20L,25000,"TARJETA","PAGADO")));
        assertThatThrownBy(() -> service.anular(9L))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("No se puede anular un pago ya confirmado");
    }

    @Test void eliminar_cuandoNoExisteDebeLanzarNotFound() {
        when(repository.findById(88L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> service.eliminar(88L))
                .isInstanceOf(PagoNotFoundException.class)
                .hasMessage("Pago con ID 88 no encontrado");
        verify(repository, never()).delete(any());
    }
}