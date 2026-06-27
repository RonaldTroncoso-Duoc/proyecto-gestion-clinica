package example.ms_notificaciones.service;

import example.ms_notificaciones.client.CitaClient;
import example.ms_notificaciones.client.PacienteClient;
import example.ms_notificaciones.dto.NotificacionRequestDTO;
import example.ms_notificaciones.dto.NotificacionResponseDTO;
import example.ms_notificaciones.exception.NotificacionNotFoundException;
import example.ms_notificaciones.model.Notificacion;
import example.ms_notificaciones.repository.NotificacionRepository;
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
 * Pruebas unitarias NotificacionServiceImpl.
 * NOTA: Se eliminó MsNotificacionesApplicationTests.java (@SpringBootTest).
 * COMANDO: mvn -pl ms-notificaciones -Dtest=NotificacionServiceImplTest test
 * REPORTE: ms-notificaciones/target/surefire-reports/
 */
@ExtendWith(MockitoExtension.class)
class NotificacionServiceImplTest {

    @Mock private NotificacionRepository repository;
    @Mock private PacienteClient pacienteClient;
    @Mock private CitaClient citaClient;
    @InjectMocks private NotificacionServiceImpl service;

    private Notificacion notif(Long id, Long pacienteId, Long citaId, String tipo, String estado) {
        return Notificacion.builder()
                .id(id).pacienteId(pacienteId).citaId(citaId).tipo(tipo)
                .mensaje("Mensaje de prueba").estado(estado)
                .fechaEnvio(LocalDateTime.of(2026,7,1,10,0)).build();
    }

    private NotificacionRequestDTO request(Long pacienteId, Long citaId, String tipo, String mensaje) {
        NotificacionRequestDTO dto = new NotificacionRequestDTO();
        dto.setPacienteId(pacienteId); dto.setCitaId(citaId);
        dto.setTipo(tipo); dto.setMensaje(mensaje);
        return dto;
    }

    @Test void listarTodas_debeRetornarNotificacionesMapeadas() {
        when(repository.findAll()).thenReturn(List.of(notif(1L,10L,20L,"CITA","PENDIENTE")));
        List<NotificacionResponseDTO> r = service.listarTodas();
        assertThat(r).hasSize(1);
        assertThat(r.get(0).getId()).isEqualTo(1L);
        verify(repository).findAll();
    }

    @Test void listarPorPaciente_debeValidarPacienteYRetornarNotificaciones() {
        when(pacienteClient.obtenerPaciente(10L)).thenReturn(new Object());
        when(repository.findByPacienteId(10L)).thenReturn(List.of(notif(2L,10L,20L,"CITA","PENDIENTE")));
        List<NotificacionResponseDTO> r = service.listarPorPaciente(10L);
        assertThat(r.get(0).getPacienteId()).isEqualTo(10L);
        verify(pacienteClient).obtenerPaciente(10L);
    }

    @Test void listarPorEstado_debeRetornarNotificacionesConEstadoUppercase() {
        when(repository.findByEstado("PENDIENTE")).thenReturn(List.of(notif(3L,10L,20L,"CITA","PENDIENTE")));
        List<NotificacionResponseDTO> r = service.listarPorEstado("pendiente");
        assertThat(r.get(0).getEstado()).isEqualTo("PENDIENTE");
        verify(repository).findByEstado("PENDIENTE");
    }

    @Test void listarPorTipo_debeRetornarNotificacionesConTipoUppercase() {
        when(repository.findByTipo("CITA")).thenReturn(List.of(notif(4L,10L,20L,"CITA","PENDIENTE")));
        List<NotificacionResponseDTO> r = service.listarPorTipo("cita");
        assertThat(r.get(0).getTipo()).isEqualTo("CITA");
        verify(repository).findByTipo("CITA");
    }

    @Test void buscarPorId_debeRetornarNotificacionMapeada() {
        when(repository.findById(5L)).thenReturn(Optional.of(notif(5L,10L,20L,"CITA","PENDIENTE")));
        NotificacionResponseDTO r = service.buscarPorId(5L);
        assertThat(r.getId()).isEqualTo(5L);
        verify(repository).findById(5L);
    }

    @Test void crear_debeValidarFeignsGuardarYRetornarPendiente() {
        NotificacionRequestDTO dto = request(10L,20L," cita "," Mensaje de prueba ");
        when(pacienteClient.obtenerPaciente(10L)).thenReturn(new Object());
        when(citaClient.obtenerCita(20L)).thenReturn(new Object());
        when(repository.save(any(Notificacion.class))).thenReturn(notif(6L,10L,20L,"CITA","PENDIENTE"));
        NotificacionResponseDTO r = service.crear(dto);
        assertThat(r.getId()).isEqualTo(6L);
        assertThat(r.getTipo()).isEqualTo("CITA");
        assertThat(r.getEstado()).isEqualTo("PENDIENTE");
        verify(repository).save(any(Notificacion.class));
    }

    @Test void marcarEnviada_debeCambiarEstado() {
        Notificacion n = notif(7L,10L,20L,"CITA","PENDIENTE");
        when(repository.findById(7L)).thenReturn(Optional.of(n));
        when(repository.save(n)).thenReturn(n);
        NotificacionResponseDTO r = service.marcarEnviada(7L);
        assertThat(r.getEstado()).isEqualTo("ENVIADA");
        verify(repository).save(n);
    }

    @Test void marcarFallida_debeCambiarEstado() {
        Notificacion n = notif(8L,10L,20L,"CITA","PENDIENTE");
        when(repository.findById(8L)).thenReturn(Optional.of(n));
        when(repository.save(n)).thenReturn(n);
        NotificacionResponseDTO r = service.marcarFallida(8L);
        assertThat(r.getEstado()).isEqualTo("FALLIDA");
        verify(repository).save(n);
    }

    @Test void eliminar_debeBuscarYEliminar() {
        Notificacion n = notif(9L,10L,20L,"CITA","PENDIENTE");
        when(repository.findById(9L)).thenReturn(Optional.of(n));
        service.eliminar(9L);
        verify(repository).delete(n);
    }

    @Test void buscarPorId_cuandoNoExisteDebeLanzarNotFound() {
        when(repository.findById(99L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> service.buscarPorId(99L))
                .isInstanceOf(NotificacionNotFoundException.class)
                .hasMessage("Notificación con ID 99 no encontrada");
    }

    @Test void listarPorPaciente_cuandoPacienteNoExisteDebeLanzarFeignException() {
        when(pacienteClient.obtenerPaciente(999L)).thenThrow(FeignException.NotFound.class);
        assertThatThrownBy(() -> service.listarPorPaciente(999L)).isInstanceOf(FeignException.NotFound.class);
        verify(repository, never()).findByPacienteId(any());
    }

    @Test void crear_cuandoPacienteNoExisteDebeLanzarFeignException() {
        NotificacionRequestDTO dto = request(999L,20L,"CITA","Mensaje");
        when(pacienteClient.obtenerPaciente(999L)).thenThrow(FeignException.NotFound.class);
        assertThatThrownBy(() -> service.crear(dto)).isInstanceOf(FeignException.NotFound.class);
        verify(citaClient, never()).obtenerCita(any());
        verify(repository, never()).save(any());
    }

    @Test void crear_cuandoCitaNoExisteDebeLanzarFeignException() {
        NotificacionRequestDTO dto = request(10L,999L,"CITA","Mensaje");
        when(pacienteClient.obtenerPaciente(10L)).thenReturn(new Object());
        when(citaClient.obtenerCita(999L)).thenThrow(FeignException.NotFound.class);
        assertThatThrownBy(() -> service.crear(dto)).isInstanceOf(FeignException.NotFound.class);
        verify(repository, never()).save(any());
    }

    @Test void marcarEnviada_cuandoNoExisteDebeLanzarNotFound() {
        when(repository.findById(77L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> service.marcarEnviada(77L))
                .isInstanceOf(NotificacionNotFoundException.class)
                .hasMessage("Notificación con ID 77 no encontrada");
        verify(repository, never()).save(any());
    }

    @Test void marcarFallida_cuandoNoExisteDebeLanzarNotFound() {
        when(repository.findById(78L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> service.marcarFallida(78L))
                .isInstanceOf(NotificacionNotFoundException.class)
                .hasMessage("Notificación con ID 78 no encontrada");
        verify(repository, never()).save(any());
    }

    @Test void eliminar_cuandoNoExisteDebeLanzarNotFound() {
        when(repository.findById(88L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> service.eliminar(88L))
                .isInstanceOf(NotificacionNotFoundException.class)
                .hasMessage("Notificación con ID 88 no encontrada");
        verify(repository, never()).delete(any());
    }
}