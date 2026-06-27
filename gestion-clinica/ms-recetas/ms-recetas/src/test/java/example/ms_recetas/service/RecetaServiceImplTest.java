package example.ms_recetas.service;

import example.ms_recetas.client.FichaClinicaClient;
import example.ms_recetas.client.MedicoClient;
import example.ms_recetas.client.PacienteClient;
import example.ms_recetas.dto.RecetaRequestDTO;
import example.ms_recetas.dto.RecetaResponseDTO;
import example.ms_recetas.exception.RecetaNotFoundException;
import example.ms_recetas.model.Receta;
import example.ms_recetas.repository.RecetaRepository;
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
 * Pruebas unitarias RecetaServiceImpl.
 * NOTA: Se eliminó MsRecetasApplicationTests.java (@SpringBootTest).
 * COMANDO: mvn -pl ms-recetas -Dtest=RecetaServiceImplTest test
 * REPORTE: ms-recetas/target/surefire-reports/
 */
@ExtendWith(MockitoExtension.class)
class RecetaServiceImplTest {

    @Mock private RecetaRepository repository;
    @Mock private FichaClinicaClient fichaClinicaClient;
    @Mock private PacienteClient pacienteClient;
    @Mock private MedicoClient medicoClient;
    @InjectMocks private RecetaServiceImpl service;

    private Receta receta(Long id, Long fichaId, Long pacienteId, Long medicoId, Boolean activa) {
        return Receta.builder().id(id).fichaClinicaId(fichaId).pacienteId(pacienteId).medicoId(medicoId)
                .medicamento("Paracetamol").dosis("500mg").indicaciones("Cada 8 horas")
                .duracionDias(5).fechaEmision(LocalDateTime.of(2026,7,1,10,0)).activa(activa).build();
    }

    private RecetaRequestDTO request(Long fichaId, Long pacienteId, Long medicoId) {
        RecetaRequestDTO dto = new RecetaRequestDTO();
        dto.setFichaClinicaId(fichaId); dto.setPacienteId(pacienteId); dto.setMedicoId(medicoId);
        dto.setMedicamento(" Paracetamol "); dto.setDosis(" 500mg ");
        dto.setIndicaciones(" Cada 8 horas "); dto.setDuracionDias(5);
        return dto;
    }

    @Test void listarTodas_debeRetornarRecetasMapeadas() {
        when(repository.findAll()).thenReturn(List.of(receta(1L,10L,20L,30L,true)));
        List<RecetaResponseDTO> r = service.listarTodas();
        assertThat(r).hasSize(1);
        assertThat(r.get(0).getId()).isEqualTo(1L);
        verify(repository).findAll();
    }

    @Test void listarActivas_debeRetornarRecetasActivas() {
        when(repository.findByActivaTrue()).thenReturn(List.of(receta(2L,10L,20L,30L,true)));
        List<RecetaResponseDTO> r = service.listarActivas();
        assertThat(r.get(0).getActiva()).isTrue();
        verify(repository).findByActivaTrue();
    }

    @Test void listarPorPaciente_debeValidarPacienteYRetornarRecetas() {
        when(pacienteClient.obtenerPaciente(20L)).thenReturn(new Object());
        when(repository.findByPacienteId(20L)).thenReturn(List.of(receta(3L,10L,20L,30L,true)));
        List<RecetaResponseDTO> r = service.listarPorPaciente(20L);
        assertThat(r.get(0).getPacienteId()).isEqualTo(20L);
        verify(pacienteClient).obtenerPaciente(20L);
    }

    @Test void listarPorMedico_debeValidarMedicoYRetornarRecetas() {
        when(medicoClient.obtenerMedico(30L)).thenReturn(new Object());
        when(repository.findByMedicoId(30L)).thenReturn(List.of(receta(4L,10L,20L,30L,true)));
        List<RecetaResponseDTO> r = service.listarPorMedico(30L);
        assertThat(r.get(0).getMedicoId()).isEqualTo(30L);
        verify(medicoClient).obtenerMedico(30L);
    }

    @Test void listarPorFichaClinica_debeValidarFichaYRetornarRecetas() {
        when(fichaClinicaClient.obtenerFicha(10L)).thenReturn(new Object());
        when(repository.findByFichaClinicaId(10L)).thenReturn(List.of(receta(5L,10L,20L,30L,true)));
        List<RecetaResponseDTO> r = service.listarPorFichaClinica(10L);
        assertThat(r.get(0).getFichaClinicaId()).isEqualTo(10L);
        verify(fichaClinicaClient).obtenerFicha(10L);
    }

    @Test void buscarPorId_debeRetornarRecetaMapeada() {
        when(repository.findById(6L)).thenReturn(Optional.of(receta(6L,10L,20L,30L,true)));
        RecetaResponseDTO r = service.buscarPorId(6L);
        assertThat(r.getId()).isEqualTo(6L);
        verify(repository).findById(6L);
    }

    @Test void crear_debeValidarFeignsGuardarYRetornarReceta() {
        RecetaRequestDTO dto = request(10L,20L,30L);
        when(fichaClinicaClient.obtenerFicha(10L)).thenReturn(new Object());
        when(pacienteClient.obtenerPaciente(20L)).thenReturn(new Object());
        when(medicoClient.obtenerMedico(30L)).thenReturn(new Object());
        when(repository.save(any(Receta.class))).thenReturn(receta(7L,10L,20L,30L,true));
        RecetaResponseDTO r = service.crear(dto);
        assertThat(r.getId()).isEqualTo(7L);
        assertThat(r.getMedicamento()).isEqualTo("Paracetamol");
        verify(repository).save(any(Receta.class));
    }

    @Test void actualizar_debeGuardarCambios() {
        RecetaRequestDTO dto = request(11L,21L,31L);
        Receta existente = receta(8L,10L,20L,30L,true);
        when(repository.findById(8L)).thenReturn(Optional.of(existente));
        when(fichaClinicaClient.obtenerFicha(11L)).thenReturn(new Object());
        when(pacienteClient.obtenerPaciente(21L)).thenReturn(new Object());
        when(medicoClient.obtenerMedico(31L)).thenReturn(new Object());
        when(repository.save(existente)).thenReturn(existente);
        RecetaResponseDTO r = service.actualizar(8L, dto);
        assertThat(r.getFichaClinicaId()).isEqualTo(11L);
        assertThat(r.getPacienteId()).isEqualTo(21L);
        verify(repository).save(existente);
    }

    @Test void activarYDesactivar_debenCambiarActiva() {
        Receta r = receta(9L,10L,20L,30L,false);
        when(repository.findById(9L)).thenReturn(Optional.of(r));
        when(repository.save(r)).thenReturn(r);
        assertThat(service.activar(9L).getActiva()).isTrue();
        assertThat(service.desactivar(9L).getActiva()).isFalse();
        verify(repository, times(2)).save(r);
    }

    @Test void eliminar_debeBuscarYEliminar() {
        Receta r = receta(10L,10L,20L,30L,true);
        when(repository.findById(10L)).thenReturn(Optional.of(r));
        service.eliminar(10L);
        verify(repository).delete(r);
    }

    @Test void buscarPorId_cuandoNoExisteDebeLanzarNotFound() {
        when(repository.findById(99L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> service.buscarPorId(99L))
                .isInstanceOf(RecetaNotFoundException.class)
                .hasMessage("Receta con ID 99 no encontrada");
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

    @Test void listarPorFichaClinica_cuandoFichaNoExisteDebeLanzarFeignException() {
        when(fichaClinicaClient.obtenerFicha(999L)).thenThrow(FeignException.NotFound.class);
        assertThatThrownBy(() -> service.listarPorFichaClinica(999L)).isInstanceOf(FeignException.NotFound.class);
        verify(repository, never()).findByFichaClinicaId(any());
    }

    @Test void crear_cuandoFichaNoExisteDebeLanzarFeignException() {
        RecetaRequestDTO dto = request(999L,20L,30L);
        when(fichaClinicaClient.obtenerFicha(999L)).thenThrow(FeignException.NotFound.class);
        assertThatThrownBy(() -> service.crear(dto)).isInstanceOf(FeignException.NotFound.class);
        verify(repository, never()).save(any());
    }

    @Test void crear_cuandoPacienteNoExisteDebeLanzarFeignException() {
        RecetaRequestDTO dto = request(10L,999L,30L);
        when(fichaClinicaClient.obtenerFicha(10L)).thenReturn(new Object());
        when(pacienteClient.obtenerPaciente(999L)).thenThrow(FeignException.NotFound.class);
        assertThatThrownBy(() -> service.crear(dto)).isInstanceOf(FeignException.NotFound.class);
        verify(repository, never()).save(any());
    }

    @Test void crear_cuandoMedicoNoExisteDebeLanzarFeignException() {
        RecetaRequestDTO dto = request(10L,20L,999L);
        when(fichaClinicaClient.obtenerFicha(10L)).thenReturn(new Object());
        when(pacienteClient.obtenerPaciente(20L)).thenReturn(new Object());
        when(medicoClient.obtenerMedico(999L)).thenThrow(FeignException.NotFound.class);
        assertThatThrownBy(() -> service.crear(dto)).isInstanceOf(FeignException.NotFound.class);
        verify(repository, never()).save(any());
    }

    @Test void actualizar_cuandoRecetaNoExisteDebeLanzarNotFound() {
        when(repository.findById(77L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> service.actualizar(77L, request(10L,20L,30L)))
                .isInstanceOf(RecetaNotFoundException.class)
                .hasMessage("Receta con ID 77 no encontrada");
        verify(repository, never()).save(any());
    }

    @Test void eliminar_cuandoNoExisteDebeLanzarNotFound() {
        when(repository.findById(88L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> service.eliminar(88L))
                .isInstanceOf(RecetaNotFoundException.class)
                .hasMessage("Receta con ID 88 no encontrada");
        verify(repository, never()).delete(any());
    }
}