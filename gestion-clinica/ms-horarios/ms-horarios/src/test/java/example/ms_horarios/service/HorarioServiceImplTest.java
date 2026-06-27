package example.ms_horarios.service;

import example.ms_horarios.client.MedicoClient;
import example.ms_horarios.dto.HorarioRequestDTO;
import example.ms_horarios.dto.HorarioResponseDTO;
import example.ms_horarios.exception.HorarioNotFoundException;
import example.ms_horarios.model.Horario;
import example.ms_horarios.repository.HorarioRepository;
import feign.FeignException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Pruebas unitarias para HorarioServiceImpl.
 *
 * NOTA: se usa MockitoExtension para probar solo la lógica del service,
 * sin levantar Spring Boot, MySQL, Eureka ni Feign real.
 *
 * COMANDO: mvn -pl ms-horarios/ms-horarios -Dtest=HorarioServiceImplTest test
 * REPORTE: ms-horarios/ms-horarios/target/surefire-reports/
 */
@ExtendWith(MockitoExtension.class)
class HorarioServiceImplTest {

    @Mock
    private HorarioRepository repository;

    @Mock
    private MedicoClient medicoClient;

    @InjectMocks
    private HorarioServiceImpl service;

    private Horario crearHorario(Long id, Long medicoId, LocalDate fecha,
                                 LocalTime horaInicio, LocalTime horaFin,
                                 Boolean disponible) {
        return Horario.builder()
                .id(id)
                .medicoId(medicoId)
                .fecha(fecha)
                .horaInicio(horaInicio)
                .horaFin(horaFin)
                .disponible(disponible)
                .build();
    }

    private HorarioRequestDTO crearRequest(Long medicoId, LocalDate fecha,
                                            LocalTime horaInicio, LocalTime horaFin) {
        HorarioRequestDTO dto = new HorarioRequestDTO();
        dto.setMedicoId(medicoId);
        dto.setFecha(fecha);
        dto.setHoraInicio(horaInicio);
        dto.setHoraFin(horaFin);
        return dto;
    }

    @Test
    void listarTodos_debeRetornarHorariosMapeados() {
        // ARRANGE: preparar datos y mocks.
        Horario horario = crearHorario(1L, 10L, LocalDate.of(2026, 7, 1),
                LocalTime.of(9, 0), LocalTime.of(10, 0), true);

        when(repository.findAll()).thenReturn(List.of(horario));

        // ACT: ejecutar método o endpoint.
        List<HorarioResponseDTO> resultado = service.listarTodos();

        // ASSERT: verificar resultado esperado.
        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).getId()).isEqualTo(1L);
        assertThat(resultado.get(0).getMedicoId()).isEqualTo(10L);
        assertThat(resultado.get(0).getFecha()).isEqualTo(LocalDate.of(2026, 7, 1));
        assertThat(resultado.get(0).getHoraInicio()).isEqualTo(LocalTime.of(9, 0));
        assertThat(resultado.get(0).getHoraFin()).isEqualTo(LocalTime.of(10, 0));
        assertThat(resultado.get(0).getDisponible()).isTrue();

        // VERIFY: comprobar llamadas al mock si corresponde.
        verify(repository).findAll();
    }

    @Test
    void listarDisponibles_debeRetornarHorariosDisponiblesMapeados() {
        Horario horario = crearHorario(2L, 11L, LocalDate.of(2026, 7, 2),
                LocalTime.of(11, 0), LocalTime.of(12, 0), true);

        when(repository.findByDisponibleTrue()).thenReturn(List.of(horario));

        List<HorarioResponseDTO> resultado = service.listarDisponibles();

        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).getId()).isEqualTo(2L);
        assertThat(resultado.get(0).getDisponible()).isTrue();
        verify(repository).findByDisponibleTrue();
    }

    @Test
    void listarPorMedico_debeValidarMedicoYRetornarHorariosMapeados() {
        Long medicoId = 12L;
        Horario horario = crearHorario(3L, medicoId, LocalDate.of(2026, 7, 3),
                LocalTime.of(8, 0), LocalTime.of(9, 0), false);

        when(medicoClient.obtenerMedico(medicoId)).thenReturn(new Object());
        when(repository.findByMedicoId(medicoId)).thenReturn(List.of(horario));

        List<HorarioResponseDTO> resultado = service.listarPorMedico(medicoId);

        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).getMedicoId()).isEqualTo(medicoId);
        assertThat(resultado.get(0).getDisponible()).isFalse();
        verify(medicoClient).obtenerMedico(medicoId);
        verify(repository).findByMedicoId(medicoId);
    }

    @Test
    void listarDisponiblesPorMedico_debeValidarMedicoYRetornarHorariosDisponiblesMapeados() {
        Long medicoId = 13L;
        Horario horario = crearHorario(4L, medicoId, LocalDate.of(2026, 7, 4),
                LocalTime.of(14, 0), LocalTime.of(15, 0), true);

        when(medicoClient.obtenerMedico(medicoId)).thenReturn(new Object());
        when(repository.findByMedicoIdAndDisponibleTrue(medicoId)).thenReturn(List.of(horario));

        List<HorarioResponseDTO> resultado = service.listarDisponiblesPorMedico(medicoId);

        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).getMedicoId()).isEqualTo(medicoId);
        assertThat(resultado.get(0).getDisponible()).isTrue();
        verify(medicoClient).obtenerMedico(medicoId);
        verify(repository).findByMedicoIdAndDisponibleTrue(medicoId);
    }

    @Test
    void buscarPorId_debeRetornarHorarioMapeado() {
        Long id = 5L;
        Horario horario = crearHorario(id, 14L, LocalDate.of(2026, 7, 5),
                LocalTime.of(15, 0), LocalTime.of(16, 0), true);

        when(repository.findById(id)).thenReturn(Optional.of(horario));

        HorarioResponseDTO resultado = service.buscarPorId(id);

        assertThat(resultado.getId()).isEqualTo(id);
        assertThat(resultado.getMedicoId()).isEqualTo(14L);
        assertThat(resultado.getDisponible()).isTrue();
        verify(repository).findById(id);
    }

    @Test
    void crear_debeValidarMedicoGuardarHorarioDisponibleYRetornarMapeado() {
        HorarioRequestDTO request = crearRequest(15L, LocalDate.of(2026, 7, 6),
                LocalTime.of(9, 0), LocalTime.of(10, 0));
        Horario guardado = crearHorario(6L, 15L, request.getFecha(),
                request.getHoraInicio(), request.getHoraFin(), true);

        when(medicoClient.obtenerMedico(15L)).thenReturn(new Object());
        when(repository.existsByMedicoIdAndFechaAndHoraInicioAndHoraFin(
                15L, request.getFecha(), request.getHoraInicio(), request.getHoraFin()))
                .thenReturn(false);
        when(repository.save(any(Horario.class))).thenReturn(guardado);

        HorarioResponseDTO resultado = service.crear(request);

        assertThat(resultado.getId()).isEqualTo(6L);
        assertThat(resultado.getMedicoId()).isEqualTo(15L);
        assertThat(resultado.getDisponible()).isTrue();
        verify(medicoClient).obtenerMedico(15L);
        verify(repository).save(any(Horario.class));
    }

    @Test
    void actualizar_debeGuardarCambiosYRetornarHorarioActualizado() {
        Long id = 7L;
        HorarioRequestDTO request = crearRequest(16L, LocalDate.of(2026, 7, 7),
                LocalTime.of(10, 0), LocalTime.of(11, 0));
        Horario existente = crearHorario(id, 15L, LocalDate.of(2026, 7, 6),
                LocalTime.of(9, 0), LocalTime.of(10, 0), true);

        when(repository.findById(id)).thenReturn(Optional.of(existente));
        when(medicoClient.obtenerMedico(16L)).thenReturn(new Object());
        when(repository.existsByMedicoIdAndFechaAndHoraInicioAndHoraFin(
                16L, request.getFecha(), request.getHoraInicio(), request.getHoraFin()))
                .thenReturn(false);
        when(repository.save(existente)).thenReturn(existente);

        HorarioResponseDTO resultado = service.actualizar(id, request);

        assertThat(resultado.getId()).isEqualTo(id);
        assertThat(resultado.getMedicoId()).isEqualTo(16L);
        assertThat(resultado.getFecha()).isEqualTo(LocalDate.of(2026, 7, 7));
        assertThat(resultado.getHoraInicio()).isEqualTo(LocalTime.of(10, 0));
        assertThat(resultado.getHoraFin()).isEqualTo(LocalTime.of(11, 0));
        assertThat(resultado.getDisponible()).isTrue();
        verify(repository).save(existente);
    }

    @Test
    void ocupar_debeGuardarHorarioOcupadoYRetornarMapeado() {
        Long id = 8L;
        Horario disponible = crearHorario(id, 17L, LocalDate.of(2026, 7, 8),
                LocalTime.of(12, 0), LocalTime.of(13, 0), true);

        when(repository.findById(id)).thenReturn(Optional.of(disponible));
        when(repository.save(disponible)).thenReturn(disponible);

        HorarioResponseDTO resultado = service.ocupar(id);

        assertThat(resultado.getId()).isEqualTo(id);
        assertThat(resultado.getDisponible()).isFalse();
        verify(repository).save(disponible);
    }

    @Test
    void liberar_debeGuardarHorarioDisponibleYRetornarMapeado() {
        Long id = 9L;
        Horario ocupado = crearHorario(id, 18L, LocalDate.of(2026, 7, 9),
                LocalTime.of(13, 0), LocalTime.of(14, 0), false);

        when(repository.findById(id)).thenReturn(Optional.of(ocupado));
        when(repository.save(ocupado)).thenReturn(ocupado);

        HorarioResponseDTO resultado = service.liberar(id);

        assertThat(resultado.getId()).isEqualTo(id);
        assertThat(resultado.getDisponible()).isTrue();
        verify(repository).save(ocupado);
    }

    @Test
    void eliminar_debeBuscarHorarioYEliminarlo() {
        Long id = 10L;
        Horario horario = crearHorario(id, 19L, LocalDate.of(2026, 7, 10),
                LocalTime.of(16, 0), LocalTime.of(17, 0), true);

        when(repository.findById(id)).thenReturn(Optional.of(horario));

        service.eliminar(id);

        verify(repository).findById(id);
        verify(repository).delete(horario);
    }

    @Test
    void buscarPorId_cuandoNoExisteDebeLanzarHorarioNotFoundException() {
        Long id = 99L;
        when(repository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.buscarPorId(id))
                .isInstanceOf(HorarioNotFoundException.class)
                .hasMessage("Horario con ID 99 no encontrado");
    }

    @Test
    void listarPorMedico_cuandoMedicoNoExisteDebeLanzarFeignException() {
        Long medicoId = 999L;
        when(medicoClient.obtenerMedico(medicoId)).thenThrow(FeignException.NotFound.class);

        assertThatThrownBy(() -> service.listarPorMedico(medicoId))
                .isInstanceOf(FeignException.NotFound.class);
        verify(repository, never()).findByMedicoId(any());
    }

    @Test
    void crear_cuandoRangoHorarioEsInvalidoDebeLanzarRuntimeException() {
        HorarioRequestDTO request = crearRequest(20L, LocalDate.of(2026, 7, 11),
                LocalTime.of(10, 0), LocalTime.of(10, 0));

        when(medicoClient.obtenerMedico(20L)).thenReturn(new Object());

        assertThatThrownBy(() -> service.crear(request))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("La hora de término debe ser posterior a la hora de inicio");
        verify(repository, never()).save(any());
    }

    @Test
    void crear_cuandoHorarioDuplicadoDebeLanzarRuntimeException() {
        HorarioRequestDTO request = crearRequest(21L, LocalDate.of(2026, 7, 12),
                LocalTime.of(9, 0), LocalTime.of(10, 0));

        when(medicoClient.obtenerMedico(21L)).thenReturn(new Object());
        when(repository.existsByMedicoIdAndFechaAndHoraInicioAndHoraFin(
                21L, request.getFecha(), request.getHoraInicio(), request.getHoraFin()))
                .thenReturn(true);

        assertThatThrownBy(() -> service.crear(request))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Ya existe un horario registrado para ese médico en la misma fecha y hora");
        verify(repository, never()).save(any());
    }

    @Test
    void actualizar_cuandoHorarioNoExisteDebeLanzarHorarioNotFoundException() {
        HorarioRequestDTO request = crearRequest(22L, LocalDate.of(2026, 7, 13),
                LocalTime.of(9, 0), LocalTime.of(10, 0));

        when(repository.findById(77L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.actualizar(77L, request))
                .isInstanceOf(HorarioNotFoundException.class)
                .hasMessage("Horario con ID 77 no encontrado");
        verify(medicoClient, never()).obtenerMedico(any());
        verify(repository, never()).save(any());
    }

    @Test
    void actualizar_cuandoBloqueDuplicadoDebeLanzarRuntimeException() {
        Long id = 11L;
        Horario existente = crearHorario(id, 23L, LocalDate.of(2026, 7, 14),
                LocalTime.of(9, 0), LocalTime.of(10, 0), true);
        HorarioRequestDTO request = crearRequest(23L, LocalDate.of(2026, 7, 14),
                LocalTime.of(10, 0), LocalTime.of(11, 0));

        when(repository.findById(id)).thenReturn(Optional.of(existente));
        when(medicoClient.obtenerMedico(23L)).thenReturn(new Object());
        when(repository.existsByMedicoIdAndFechaAndHoraInicioAndHoraFin(
                23L, request.getFecha(), request.getHoraInicio(), request.getHoraFin()))
                .thenReturn(true);

        assertThatThrownBy(() -> service.actualizar(id, request))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Ya existe otro horario registrado para ese médico en la misma fecha y hora");
        verify(repository, never()).save(any());
    }

    @Test
    void ocupar_cuandoHorarioYaEstaOcupadoDebeLanzarRuntimeException() {
        Long id = 12L;
        Horario ocupado = crearHorario(id, 24L, LocalDate.of(2026, 7, 15),
                LocalTime.of(11, 0), LocalTime.of(12, 0), false);

        when(repository.findById(id)).thenReturn(Optional.of(ocupado));

        assertThatThrownBy(() -> service.ocupar(id))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("El horario ya se encuentra ocupado");
        verify(repository, never()).save(any());
    }
}