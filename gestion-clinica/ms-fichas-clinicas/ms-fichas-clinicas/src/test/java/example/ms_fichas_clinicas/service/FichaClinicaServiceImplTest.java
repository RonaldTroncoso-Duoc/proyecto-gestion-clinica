package example.ms_fichas_clinicas.service;

import example.ms_fichas_clinicas.client.CitaClient;
import example.ms_fichas_clinicas.client.MedicoClient;
import example.ms_fichas_clinicas.client.PacienteClient;
import example.ms_fichas_clinicas.dto.FichaClinicaRequestDTO;
import example.ms_fichas_clinicas.dto.FichaClinicaResponseDTO;
import example.ms_fichas_clinicas.exception.FichaClinicaNotFoundException;
import example.ms_fichas_clinicas.model.FichaClinica;
import example.ms_fichas_clinicas.repository.FichaClinicaRepository;
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
 * Pruebas unitarias FichaClinicaServiceImpl.
 * NOTA: Se eliminó MsFichasClinicasApplicationTests.java (@SpringBootTest).
 * COMANDO: mvn -pl ms-fichas-clinicas -Dtest=FichaClinicaServiceImplTest test
 * REPORTE: ms-fichas-clinicas/target/surefire-reports/
 */
@ExtendWith(MockitoExtension.class)
class FichaClinicaServiceImplTest {

    @Mock private FichaClinicaRepository repository;
    @Mock private PacienteClient pacienteClient;
    @Mock private MedicoClient medicoClient;
    @Mock private CitaClient citaClient;
    @InjectMocks private FichaClinicaServiceImpl service;

    private FichaClinica ficha(Long id, Long pacienteId, Long medicoId, Long citaId, Boolean activo) {
        return FichaClinica.builder()
                .id(id).pacienteId(pacienteId).medicoId(medicoId).citaId(citaId)
                .motivoConsulta("Motivo").diagnostico("Diagnostico").tratamiento("Tratamiento")
                .observaciones("Observaciones")
                .fechaRegistro(LocalDateTime.of(2026,7,1,10,0))
                .activo(activo).build();
    }

    private FichaClinicaRequestDTO request(Long pacienteId, Long medicoId, Long citaId) {
        FichaClinicaRequestDTO dto = new FichaClinicaRequestDTO();
        dto.setPacienteId(pacienteId); dto.setMedicoId(medicoId); dto.setCitaId(citaId);
        dto.setMotivoConsulta("Motivo"); dto.setDiagnostico("Diagnostico");
        dto.setTratamiento("Tratamiento"); dto.setObservaciones("Observaciones");
        return dto;
    }

    @Test void listarTodas_debeRetornarFichasMapeadas() {
        when(repository.findAll()).thenReturn(List.of(ficha(1L,10L,20L,30L,true)));
        List<FichaClinicaResponseDTO> r = service.listarTodas();
        assertThat(r).hasSize(1);
        assertThat(r.get(0).getId()).isEqualTo(1L);
        verify(repository).findAll();
    }

    @Test void listarActivas_debeRetornarFichasActivas() {
        when(repository.findByActivoTrue()).thenReturn(List.of(ficha(2L,10L,20L,30L,true)));
        List<FichaClinicaResponseDTO> r = service.listarActivas();
        assertThat(r).hasSize(1);
        assertThat(r.get(0).getActivo()).isTrue();
        verify(repository).findByActivoTrue();
    }

    @Test void listarPorPaciente_debeValidarPacienteYRetornarFichas() {
        when(pacienteClient.obtenerPaciente(10L)).thenReturn(new Object());
        when(repository.findByPacienteId(10L)).thenReturn(List.of(ficha(3L,10L,20L,30L,true)));
        List<FichaClinicaResponseDTO> r = service.listarPorPaciente(10L);
        assertThat(r.get(0).getPacienteId()).isEqualTo(10L);
        verify(pacienteClient).obtenerPaciente(10L);
    }

    @Test void listarPorMedico_debeValidarMedicoYRetornarFichas() {
        when(medicoClient.obtenerMedico(20L)).thenReturn(new Object());
        when(repository.findByMedicoId(20L)).thenReturn(List.of(ficha(4L,10L,20L,30L,true)));
        List<FichaClinicaResponseDTO> r = service.listarPorMedico(20L);
        assertThat(r.get(0).getMedicoId()).isEqualTo(20L);
        verify(medicoClient).obtenerMedico(20L);
    }

    @Test void buscarPorId_debeRetornarFichaMapeada() {
        when(repository.findById(5L)).thenReturn(Optional.of(ficha(5L,10L,20L,30L,true)));
        FichaClinicaResponseDTO r = service.buscarPorId(5L);
        assertThat(r.getId()).isEqualTo(5L);
        verify(repository).findById(5L);
    }

    @Test void buscarPorCita_debeRetornarFichaMapeada() {
        when(repository.findByCitaId(30L)).thenReturn(Optional.of(ficha(6L,10L,20L,30L,true)));
        FichaClinicaResponseDTO r = service.buscarPorCita(30L);
        assertThat(r.getCitaId()).isEqualTo(30L);
        verify(repository).findByCitaId(30L);
    }

    @Test void crear_debeValidarFeignsGuardarYRetornarFicha() {
        FichaClinicaRequestDTO dto = request(10L,20L,30L);
        when(pacienteClient.obtenerPaciente(10L)).thenReturn(new Object());
        when(medicoClient.obtenerMedico(20L)).thenReturn(new Object());
        when(citaClient.obtenerCita(30L)).thenReturn(new Object());
        when(repository.existsByCitaId(30L)).thenReturn(false);
        when(repository.save(any(FichaClinica.class))).thenReturn(ficha(7L,10L,20L,30L,true));
        FichaClinicaResponseDTO r = service.crear(dto);
        assertThat(r.getId()).isEqualTo(7L);
        verify(repository).save(any(FichaClinica.class));
    }

    @Test void actualizar_debeValidarGuardarYRetornarFicha() {
        FichaClinicaRequestDTO dto = request(11L,21L,31L);
        FichaClinica existente = ficha(8L,10L,20L,30L,true);
        when(repository.findById(8L)).thenReturn(Optional.of(existente));
        when(pacienteClient.obtenerPaciente(11L)).thenReturn(new Object());
        when(medicoClient.obtenerMedico(21L)).thenReturn(new Object());
        when(citaClient.obtenerCita(31L)).thenReturn(new Object());
        when(repository.findByCitaId(31L)).thenReturn(Optional.empty());
        when(repository.save(existente)).thenReturn(existente);
        FichaClinicaResponseDTO r = service.actualizar(8L, dto);
        assertThat(r.getPacienteId()).isEqualTo(11L);
        assertThat(r.getCitaId()).isEqualTo(31L);
        verify(repository).save(existente);
    }

    @Test void desactivarYActivar_debenCambiarActivo() {
        FichaClinica f = ficha(9L,10L,20L,30L,true);
        when(repository.findById(9L)).thenReturn(Optional.of(f));
        when(repository.save(f)).thenReturn(f);
        assertThat(service.desactivar(9L).getActivo()).isFalse();
        assertThat(service.activar(9L).getActivo()).isTrue();
        verify(repository, times(2)).save(f);
    }

    @Test void eliminar_debeBuscarYEliminar() {
        FichaClinica f = ficha(10L,10L,20L,30L,true);
        when(repository.findById(10L)).thenReturn(Optional.of(f));
        service.eliminar(10L);
        verify(repository).delete(f);
    }

    @Test void buscarPorId_cuandoNoExisteDebeLanzarNotFound() {
        when(repository.findById(99L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> service.buscarPorId(99L))
                .isInstanceOf(FichaClinicaNotFoundException.class)
                .hasMessage("Ficha clínica con ID 99 no encontrada");
    }

    @Test void buscarPorCita_cuandoNoExisteDebeLanzarNotFound() {
        when(repository.findByCitaId(999L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> service.buscarPorCita(999L))
                .isInstanceOf(FichaClinicaNotFoundException.class)
                .hasMessage("Ficha clínica para cita ID 999 no encontrada");
    }

    @Test void listarPorPaciente_cuandoPacienteNoExisteDebeLanzarFeignException() {
        when(pacienteClient.obtenerPaciente(999L)).thenThrow(FeignException.NotFound.class);
        assertThatThrownBy(() -> service.listarPorPaciente(999L)).isInstanceOf(FeignException.NotFound.class);
        verify(repository, never()).findByPacienteId(any());
    }

    @Test void listarPorMedico_cuandoMedicoNoExisteDebeLanzarFeignException() {
        when(medicoClient.obtenerMedico(999L)).thenThrow(FeignException.NotFound.class);
        assertThatThrownBy(() -> service.listarPorMedico(999L)).isInstanceOf(FeignException.NotFound.class);
        verify(repository, never()).findByMedicoId(any());
    }

    @Test void crear_cuandoCitaDuplicadaDebeLanzarRuntimeException() {
        FichaClinicaRequestDTO dto = request(10L,20L,30L);
        when(pacienteClient.obtenerPaciente(10L)).thenReturn(new Object());
        when(medicoClient.obtenerMedico(20L)).thenReturn(new Object());
        when(citaClient.obtenerCita(30L)).thenReturn(new Object());
        when(repository.existsByCitaId(30L)).thenReturn(true);
        assertThatThrownBy(() -> service.crear(dto))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Ya existe una ficha clínica registrada para esta cita");
        verify(repository, never()).save(any());
    }

    @Test void crear_cuandoCitaNoExisteDebeLanzarFeignException() {
        FichaClinicaRequestDTO dto = request(10L,20L,999L);
        when(pacienteClient.obtenerPaciente(10L)).thenReturn(new Object());
        when(medicoClient.obtenerMedico(20L)).thenReturn(new Object());
        when(citaClient.obtenerCita(999L)).thenThrow(FeignException.NotFound.class);
        assertThatThrownBy(() -> service.crear(dto)).isInstanceOf(FeignException.NotFound.class);
        verify(repository, never()).save(any());
    }

    @Test void actualizar_cuandoFichaNoExisteDebeLanzarNotFound() {
        when(repository.findById(77L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> service.actualizar(77L, request(10L,20L,30L)))
                .isInstanceOf(FichaClinicaNotFoundException.class)
                .hasMessage("Ficha clínica con ID 77 no encontrada");
        verify(repository, never()).save(any());
    }

    @Test void actualizar_cuandoCitaDuplicadaEnOtraFichaDebeLanzarRuntimeException() {
        FichaClinica existente = ficha(8L,10L,20L,30L,true);
        FichaClinica otra = ficha(99L,11L,21L,31L,true);
        when(repository.findById(8L)).thenReturn(Optional.of(existente));
        when(pacienteClient.obtenerPaciente(11L)).thenReturn(new Object());
        when(medicoClient.obtenerMedico(21L)).thenReturn(new Object());
        when(citaClient.obtenerCita(31L)).thenReturn(new Object());
        when(repository.findByCitaId(31L)).thenReturn(Optional.of(otra));
        assertThatThrownBy(() -> service.actualizar(8L, request(11L,21L,31L)))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Ya existe otra ficha clínica registrada para esta cita");
        verify(repository, never()).save(any());
    }
}