package example.ms_citas.service;

import example.ms_citas.client.HorarioClient;
import example.ms_citas.client.MedicoClient;
import example.ms_citas.client.PacienteClient;
import example.ms_citas.dto.CitaRequestDTO;
import example.ms_citas.dto.CitaResponseDTO;
import example.ms_citas.exception.CitaNotFoundException;
import example.ms_citas.model.Cita;
import example.ms_citas.repository.CitaRepository;
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
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

/**
 * Pruebas unitarias CitaServiceImpl (3 Feign Clients).
 * NOTA: Se eliminó MsCitasApplicationTests.java (@SpringBootTest).
 * COMANDO: mvn -pl ms-citas -Dtest=CitaServiceImplTest test
 * REPORTE: ms-citas/target/surefire-reports/
 */
@ExtendWith(MockitoExtension.class)
class CitaServiceImplTest {

    @Mock private CitaRepository repository;
    @Mock private PacienteClient pacienteClient;
    @Mock private MedicoClient medicoClient;
    @Mock private HorarioClient horarioClient;
    @InjectMocks private CitaServiceImpl service;

    private Cita crearCita(Long id, Long pacId, Long medId, Long horId,
                           String motivo, String estado) {
        return Cita.builder().id(id).pacienteId(pacId).medicoId(medId)
                .horarioId(horId).motivo(motivo).estado(estado)
                .fechaCreacion(LocalDateTime.of(2026, 7, 1, 10, 0)).build();
    }

    private CitaRequestDTO crearRequest(Long pacId, Long medId, Long horId, String motivo) {
        CitaRequestDTO dto = new CitaRequestDTO();
        dto.setPacienteId(pacId); dto.setMedicoId(medId);
        dto.setHorarioId(horId); dto.setMotivo(motivo);
        return dto;
    }

    // ═══════════════════ HAPPY PATH ══════════════════════════════════════════

    @Test void listarTodas_debeRetornarCitasMapeadas() {
        when(repository.findAll()).thenReturn(List.of(crearCita(1L,10L,20L,30L,"M","AGENDADA")));
        List<CitaResponseDTO> r = service.listarTodas();
        assertThat(r).hasSize(1);
        assertThat(r.get(0).getId()).isEqualTo(1L);
        verify(repository).findAll();
    }

    @Test void listarPorPaciente_debeRetornarCitasMapeadas() {
        when(pacienteClient.obtenerPaciente(10L)).thenReturn(new Object());
        when(repository.findByPacienteId(10L))
                .thenReturn(List.of(crearCita(2L,10L,20L,30L,"M","AGENDADA")));
        List<CitaResponseDTO> r = service.listarPorPaciente(10L);
        assertThat(r).hasSize(1);
        assertThat(r.get(0).getPacienteId()).isEqualTo(10L);
        verify(pacienteClient).obtenerPaciente(10L);
    }

    @Test void listarPorMedico_debeRetornarCitasMapeadas() {
        when(medicoClient.obtenerMedico(20L)).thenReturn(new Object());
        when(repository.findByMedicoId(20L))
                .thenReturn(List.of(crearCita(3L,10L,20L,30L,"M","AGENDADA")));
        List<CitaResponseDTO> r = service.listarPorMedico(20L);
        assertThat(r).hasSize(1);
        assertThat(r.get(0).getMedicoId()).isEqualTo(20L);
        verify(medicoClient).obtenerMedico(20L);
    }

    @Test void listarPorEstado_debeRetornarCitasMapeadas() {
        when(repository.findByEstado("AGENDADA"))
                .thenReturn(List.of(crearCita(4L,10L,20L,30L,"M","AGENDADA")));
        List<CitaResponseDTO> r = service.listarPorEstado("AGENDADA");
        assertThat(r).hasSize(1);
        assertThat(r.get(0).getEstado()).isEqualTo("AGENDADA");
        verify(repository).findByEstado("AGENDADA");
    }

    @Test void buscarPorId_debeRetornarCitaMapeada() {
        when(repository.findById(5L))
                .thenReturn(Optional.of(crearCita(5L,10L,20L,30L,"M","AGENDADA")));
        CitaResponseDTO r = service.buscarPorId(5L);
        assertThat(r.getId()).isEqualTo(5L);
        verify(repository).findById(5L);
    }

    @Test void crear_debeGuardarYRetornarCita() {
        CitaRequestDTO req = crearRequest(10L,20L,30L,"Control");
        when(pacienteClient.obtenerPaciente(10L)).thenReturn(new Object());
        when(medicoClient.obtenerMedico(20L)).thenReturn(new Object());
        when(horarioClient.obtenerHorario(30L)).thenReturn(new Object());
        when(repository.existsByHorarioIdAndEstado(30L,"AGENDADA")).thenReturn(false);
        when(horarioClient.ocuparHorario(30L)).thenReturn(new Object());
        when(repository.save(any())).thenReturn(crearCita(6L,10L,20L,30L,"Control","AGENDADA"));
        CitaResponseDTO r = service.crear(req);
        assertThat(r.getId()).isEqualTo(6L);
        verify(horarioClient).ocuparHorario(30L);
    }

    @Test void actualizar_conMismoHorario_debeGuardarCambios() {
        CitaRequestDTO req = crearRequest(10L,20L,30L,"Nuevo motivo");
        Cita e = crearCita(7L,10L,20L,30L,"Original","AGENDADA");
        when(repository.findById(7L)).thenReturn(Optional.of(e));
        when(pacienteClient.obtenerPaciente(10L)).thenReturn(new Object());
        when(medicoClient.obtenerMedico(20L)).thenReturn(new Object());
        when(horarioClient.obtenerHorario(30L)).thenReturn(new Object());
        when(repository.save(e)).thenReturn(e);
        CitaResponseDTO r = service.actualizar(7L, req);
        assertThat(r.getMotivo()).isEqualTo("Nuevo motivo");
        verify(horarioClient, never()).liberarHorario(any());
    }

    @Test void actualizar_conNuevoHorario_debeLiberarYOcupar() {
        CitaRequestDTO req = crearRequest(10L,20L,40L,"Nuevo");
        Cita e = crearCita(7L,10L,20L,30L,"Original","AGENDADA");
        when(repository.findById(7L)).thenReturn(Optional.of(e));
        when(pacienteClient.obtenerPaciente(10L)).thenReturn(new Object());
        when(medicoClient.obtenerMedico(20L)).thenReturn(new Object());
        when(horarioClient.obtenerHorario(40L)).thenReturn(new Object());
        when(repository.existsByHorarioIdAndEstado(40L,"AGENDADA")).thenReturn(false);
        when(horarioClient.liberarHorario(30L)).thenReturn(new Object());
        when(horarioClient.ocuparHorario(40L)).thenReturn(new Object());
        when(repository.save(e)).thenReturn(e);
        CitaResponseDTO r = service.actualizar(7L, req);
        assertThat(r).isNotNull();
        verify(horarioClient).liberarHorario(30L);
        verify(horarioClient).ocuparHorario(40L);
    }

    @Test void cancelar_debeCambiarEstadoYLiberarHorario() {
        Cita a = crearCita(8L,10L,20L,30L,"Control","AGENDADA");
        when(repository.findById(8L)).thenReturn(Optional.of(a));
        when(horarioClient.liberarHorario(30L)).thenReturn(new Object());
        when(repository.save(a)).thenReturn(a);
        CitaResponseDTO r = service.cancelar(8L);
        assertThat(r.getEstado()).isEqualTo("CANCELADA");
        verify(horarioClient).liberarHorario(30L);
    }

    @Test void realizar_debeCambiarEstado() {
        Cita a = crearCita(9L,10L,20L,30L,"Cirugia","AGENDADA");
        when(repository.findById(9L)).thenReturn(Optional.of(a));
        when(repository.save(a)).thenReturn(a);
        CitaResponseDTO r = service.realizar(9L);
        assertThat(r.getEstado()).isEqualTo("REALIZADA");
    }

    @Test void eliminar_citaAgendada_debeLiberarHorarioYDelete() {
        Cita c = crearCita(10L,10L,20L,30L,"Control","AGENDADA");
        when(repository.findById(10L)).thenReturn(Optional.of(c));
        when(horarioClient.liberarHorario(30L)).thenReturn(new Object());
        doNothing().when(repository).delete(c);
        service.eliminar(10L);
        verify(horarioClient).liberarHorario(30L);
        verify(repository).delete(c);
    }

    @Test void eliminar_citaNoAgendada_noLiberaHorario() {
        Cita c = crearCita(11L,10L,20L,30L,"Control","REALIZADA");
        when(repository.findById(11L)).thenReturn(Optional.of(c));
        doNothing().when(repository).delete(c);
        service.eliminar(11L);
        verify(horarioClient, never()).liberarHorario(any());
        verify(repository).delete(c);
    }

    // ═══════════════════ ERROR PATH ══════════════════════════════════════════

    @Test void buscarPorId_cuandoNoExisteDebeLanzarCitaNotFoundException() {
        when(repository.findById(99L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> service.buscarPorId(99L))
                .isInstanceOf(CitaNotFoundException.class)
                .hasMessage("Cita con ID 99 no encontrada");
    }

    @Test void listarPorPaciente_cuandoPacienteNoExisteDebeLanzarFeignException() {
        when(pacienteClient.obtenerPaciente(999L)).thenThrow(FeignException.NotFound.class);
        assertThatThrownBy(() -> service.listarPorPaciente(999L))
                .isInstanceOf(FeignException.NotFound.class);
        verify(repository, never()).findByPacienteId(any());
    }

    @Test void listarPorMedico_cuandoMedicoNoExisteDebeLanzarFeignException() {
        when(medicoClient.obtenerMedico(999L)).thenThrow(FeignException.NotFound.class);
        assertThatThrownBy(() -> service.listarPorMedico(999L))
                .isInstanceOf(FeignException.NotFound.class);
        verify(repository, never()).findByMedicoId(any());
    }

    @Test void crear_cuandoHorarioYaTieneCitaAgendadaDebeLanzarRuntimeException() {
        CitaRequestDTO req = crearRequest(10L,20L,30L,"Control");
        when(pacienteClient.obtenerPaciente(10L)).thenReturn(new Object());
        when(medicoClient.obtenerMedico(20L)).thenReturn(new Object());
        when(horarioClient.obtenerHorario(30L)).thenReturn(new Object());
        when(repository.existsByHorarioIdAndEstado(30L,"AGENDADA")).thenReturn(true);
        assertThatThrownBy(() -> service.crear(req))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Ya existe una cita agendada para ese horario");
        verify(horarioClient, never()).ocuparHorario(any());
        verify(repository, never()).save(any());
    }

    @Test void actualizar_cuandoCitaNoExisteDebeLanzarCitaNotFoundException() {
        CitaRequestDTO req = crearRequest(10L,20L,30L,"Control");
        when(repository.findById(77L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> service.actualizar(77L, req))
                .isInstanceOf(CitaNotFoundException.class)
                .hasMessage("Cita con ID 77 no encontrada");
        verify(repository, never()).save(any());
    }

    @Test void actualizar_cuandoCitaNoEstaAgendadaDebeLanzarRuntimeException() {
        CitaRequestDTO req = crearRequest(10L,20L,30L,"Control");
        Cita cancelada = crearCita(7L,10L,20L,30L,"Original","CANCELADA");
        when(repository.findById(7L)).thenReturn(Optional.of(cancelada));
        assertThatThrownBy(() -> service.actualizar(7L, req))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Solo se pueden actualizar citas en estado AGENDADA");
        verify(repository, never()).save(any());
    }

    @Test void actualizar_conNuevoHorarioOcupadoDebeLanzarRuntimeException() {
        CitaRequestDTO req = crearRequest(10L,20L,40L,"Nuevo");
        Cita existente = crearCita(7L,10L,20L,30L,"Original","AGENDADA");
        when(repository.findById(7L)).thenReturn(Optional.of(existente));
        when(pacienteClient.obtenerPaciente(10L)).thenReturn(new Object());
        when(medicoClient.obtenerMedico(20L)).thenReturn(new Object());
        when(horarioClient.obtenerHorario(40L)).thenReturn(new Object());
        when(repository.existsByHorarioIdAndEstado(40L,"AGENDADA")).thenReturn(true);
        assertThatThrownBy(() -> service.actualizar(7L, req))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Ya existe una cita agendada para el nuevo horario");
        verify(horarioClient, never()).liberarHorario(any());
        verify(horarioClient, never()).ocuparHorario(any());
        verify(repository, never()).save(any());
    }

    @Test void cancelar_cuandoCitaNoAgendadaDebeLanzarRuntimeException() {
        Cita realizada = crearCita(8L,10L,20L,30L,"Control","REALIZADA");
        when(repository.findById(8L)).thenReturn(Optional.of(realizada));
        assertThatThrownBy(() -> service.cancelar(8L))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Solo se pueden cancelar citas en estado AGENDADA");
        verify(horarioClient, never()).liberarHorario(any());
        verify(repository, never()).save(any());
    }

    @Test void realizar_cuandoCitaNoAgendadaDebeLanzarRuntimeException() {
        Cita cancelada = crearCita(9L,10L,20L,30L,"Control","CANCELADA");
        when(repository.findById(9L)).thenReturn(Optional.of(cancelada));
        assertThatThrownBy(() -> service.realizar(9L))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Solo se pueden realizar citas en estado AGENDADA");
        verify(repository, never()).save(any());
    }

    @Test void eliminar_cuandoCitaNoExisteDebeLanzarCitaNotFoundException() {
        when(repository.findById(88L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> service.eliminar(88L))
                .isInstanceOf(CitaNotFoundException.class)
                .hasMessage("Cita con ID 88 no encontrada");
        verify(repository, never()).delete(any());
    }
}

