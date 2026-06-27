package example.ms_medicos.service;

import example.ms_medicos.client.AuthClient;
import example.ms_medicos.client.EspecialidadClient;
import example.ms_medicos.dto.AuthUserResponseDTO;
import example.ms_medicos.dto.MedicoRequestDTO;
import example.ms_medicos.dto.MedicoResponseDTO;
import example.ms_medicos.exception.MedicoNotFoundException;
import example.ms_medicos.model.Medico;
import example.ms_medicos.repository.MedicoRepository;
import feign.FeignException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Pruebas unitarias para MedicoServiceImpl.
 *
 * ═══════════════════════════════════════════════════════════════════════════════
 *  NOTA SOBRE EL TEST AUTOMÁTICO ELIMINADO
 * ═══════════════════════════════════════════════════════════════════════════════
 *  Se eliminó MsMedicosApplicationTests.java porque contenía @SpringBootTest,
 *  lo que intenta levantar MySQL, Eureka, API Gateway y todo el contexto
 *  Spring Boot completo. Para pruebas unitarias NO necesitamos nada de eso.
 *
 * ───────────────────────────────────────────────────────────────────────────────
 *  CÓMO EJECUTAR ESTE TEST
 * ───────────────────────────────────────────────────────────────────────────────
 *  mvn -pl ms-medicos -Dtest=MedicoServiceImplTest test
 *
 *  Para ejecutar TODOS los tests del microservicio:
 *    mvn -pl ms-medicos test
 *
 * ───────────────────────────────────────────────────────────────────────────────
 *  DÓNDE REVISAR EL REPORTE
 * ───────────────────────────────────────────────────────────────────────────────
 *  ms-medicos/target/surefire-reports/
 *  Archivo: TEST-example.ms_medicos.service.MedicoServiceImplTest.xml
 * ═══════════════════════════════════════════════════════════════════════════════
 */
@ExtendWith(MockitoExtension.class)
class MedicoServiceImplTest {

    @Mock
    private MedicoRepository repository;

    @Mock
    private EspecialidadClient especialidadClient;

    @Mock
    private AuthClient authClient;

    @InjectMocks
    private MedicoServiceImpl service;

    private Medico crearMedico(Long id, Long authUserId, String run, String nombre,
                                String apellido, String email, String telefono,
                                Long especialidadId, Boolean activo) {
        return Medico.builder()
                .id(id).authUserId(authUserId).run(run).nombre(nombre)
                .apellido(apellido).email(email).telefono(telefono)
                .especialidadId(especialidadId).activo(activo)
                .build();
    }

    private MedicoRequestDTO crearRequest(String run, String nombre, String apellido,
                                           String email, String telefono,
                                           Long especialidadId) {
        MedicoRequestDTO dto = new MedicoRequestDTO();
        dto.setRun(run);
        dto.setNombre(nombre);
        dto.setApellido(apellido);
        dto.setEmail(email);
        dto.setTelefono(telefono);
        dto.setEspecialidadId(especialidadId);
        return dto;
    }

    // ═════════════════════════════════════════════════════════════════════════
    //  HAPPY PATH
    // ═════════════════════════════════════════════════════════════════════════

    // ── TEST 1: listarTodos ──────────────────────────────────────────────────
    @Test
    void listarTodos_debeRetornarMedicosMapeados() {
        Medico m = crearMedico(1L, 10L, "12345678-9", "Carlos", "Lopez",
                "carlos@test.cl", "+56912345678", 1L, true);
        when(repository.findAll()).thenReturn(List.of(m));
        List<MedicoResponseDTO> r = service.listarTodos();
        assertThat(r).hasSize(1);
        assertThat(r.get(0).getId()).isEqualTo(1L);
        assertThat(r.get(0).getNombre()).isEqualTo("Carlos");
        verify(repository).findAll();
    }

    // ── TEST 2: listarActivos ────────────────────────────────────────────────
    @Test
    void listarActivos_debeRetornarMedicosActivosMapeados() {
        Medico m = crearMedico(2L, 20L, "98765432-1", "Ana", "Martinez",
                "ana@test.cl", "+56987654321", 2L, true);
        when(repository.findByActivoTrue()).thenReturn(List.of(m));
        List<MedicoResponseDTO> r = service.listarActivos();
        assertThat(r).hasSize(1);
        assertThat(r.get(0).getActivo()).isTrue();
        verify(repository).findByActivoTrue();
    }

    // ── TEST 3: buscarPorId ──────────────────────────────────────────────────
    @Test
    void buscarPorId_debeRetornarMedicoMapeado() {
        Long id = 3L;
        Medico m = crearMedico(id, null, "11111111-1", "Pedro", "Gonzalez",
                "pedro@test.cl", "+56911111111", 1L, true);
        when(repository.findById(id)).thenReturn(Optional.of(m));
        MedicoResponseDTO r = service.buscarPorId(id);
        assertThat(r.getId()).isEqualTo(3L);
        assertThat(r.getNombre()).isEqualTo("Pedro");
        verify(repository).findById(id);
    }

    // ── TEST 4: buscarPorEspecialidad ────────────────────────────────────────
    @Test
    void buscarPorEspecialidad_debeRetornarMedicosMapeados() {
        Long espId = 1L;
        Medico m = crearMedico(4L, null, "22222222-2", "Laura", "Diaz",
                "laura@test.cl", "+56922222222", espId, true);
        when(especialidadClient.obtenerEspecialidad(espId)).thenReturn(new Object());
        when(repository.findByEspecialidadId(espId)).thenReturn(List.of(m));
        List<MedicoResponseDTO> r = service.buscarPorEspecialidad(espId);
        assertThat(r).hasSize(1);
        assertThat(r.get(0).getEspecialidadId()).isEqualTo(1L);
        verify(especialidadClient).obtenerEspecialidad(espId);
        verify(repository).findByEspecialidadId(espId);
    }

    // ── TEST 5: buscarActivosPorEspecialidad ─────────────────────────────────
    @Test
    void buscarActivosPorEspecialidad_debeRetornarMedicosActivosMapeados() {
        Long espId = 2L;
        Medico m = crearMedico(5L, null, "33333333-3", "Sofia", "Ramirez",
                "sofia@test.cl", "+56933333333", espId, true);
        when(especialidadClient.obtenerEspecialidad(espId)).thenReturn(new Object());
        when(repository.findByEspecialidadIdAndActivoTrue(espId)).thenReturn(List.of(m));
        List<MedicoResponseDTO> r = service.buscarActivosPorEspecialidad(espId);
        assertThat(r).hasSize(1);
        assertThat(r.get(0).getActivo()).isTrue();
        verify(especialidadClient).obtenerEspecialidad(espId);
        verify(repository).findByEspecialidadIdAndActivoTrue(espId);
    }

    // ── TEST 6: crear (con auth) ─────────────────────────────────────────────
    @Test
    void crear_debeGuardarMedicoYRetornarMapeado() {
        MedicoRequestDTO req = crearRequest("12345678-9", "Carlos", "Lopez",
                "carlos@test.cl", "+56912345678", 1L);
        req.setUsername("carlos.lopez");
        req.setPassword("pass123");
        AuthUserResponseDTO authResp = AuthUserResponseDTO.builder()
                .id(100L).username("carlos.lopez").email("carlos@test.cl").build();
        Medico guardado = crearMedico(6L, 100L, "12345678-9", "Carlos", "Lopez",
                "carlos@test.cl", "+56912345678", 1L, true);
        when(especialidadClient.obtenerEspecialidad(1L)).thenReturn(new Object());
        when(repository.existsByRun("12345678-9")).thenReturn(false);
        when(repository.existsByEmailIgnoreCase("carlos@test.cl")).thenReturn(false);
        when(authClient.registerUser(any(), eq("DOCTOR"))).thenReturn(authResp);
        when(repository.save(any(Medico.class))).thenReturn(guardado);

        MedicoResponseDTO r = service.crear(req);
        assertThat(r.getId()).isEqualTo(6L);
        assertThat(r.getAuthUserId()).isEqualTo(100L);
        assertThat(r.getActivo()).isTrue();
        verify(authClient).registerUser(any(), eq("DOCTOR"));
        verify(repository).save(any(Medico.class));
    }

    // ── TEST 7: crear (sin auth) ─────────────────────────────────────────────
    @Test
    void crear_sinUsernameYPassword_debeGuardarSinAuthUser() {
        MedicoRequestDTO req = crearRequest("44444444-4", "Miguel", "Torres",
                "miguel@test.cl", "+56944444444", 1L);
        Medico guardado = crearMedico(7L, null, "44444444-4", "Miguel", "Torres",
                "miguel@test.cl", "+56944444444", 1L, true);
        when(especialidadClient.obtenerEspecialidad(1L)).thenReturn(new Object());
        when(repository.existsByRun("44444444-4")).thenReturn(false);
        when(repository.existsByEmailIgnoreCase("miguel@test.cl")).thenReturn(false);
        when(repository.save(any(Medico.class))).thenReturn(guardado);

        MedicoResponseDTO r = service.crear(req);
        assertThat(r.getAuthUserId()).isNull();
        verify(authClient, never()).registerUser(any(), any());
        verify(repository).save(any(Medico.class));
    }

    // ── TEST 8: actualizar ───────────────────────────────────────────────────
    @Test
    void actualizar_debeGuardarCambiosYRetornarMedicoActualizado() {
        Long id = 8L;
        MedicoRequestDTO req = crearRequest("55555555-5", "Elena", "Vargas",
                "elena@test.cl", "+56955555555", 2L);
        Medico existente = crearMedico(id, 100L, "55555555-5", "Nom Antiguo",
                "Ape Antiguo", "antiguo@test.cl", "+56900000000", 1L, true);
        when(repository.findById(id)).thenReturn(Optional.of(existente));
        when(especialidadClient.obtenerEspecialidad(2L)).thenReturn(new Object());
        when(repository.findByRun("55555555-5")).thenReturn(Optional.of(existente));
        when(repository.findByEmailIgnoreCase("elena@test.cl")).thenReturn(Optional.of(existente));
        when(repository.save(existente)).thenReturn(existente);

        MedicoResponseDTO r = service.actualizar(id, req);
        assertThat(r.getNombre()).isEqualTo("Elena");
        assertThat(r.getApellido()).isEqualTo("Vargas");
        assertThat(r.getEspecialidadId()).isEqualTo(2L);
        verify(repository).save(existente);
    }

    // ── TEST 9: eliminar ─────────────────────────────────────────────────────
    @Test
    void eliminar_debeBuscarMedicoYEliminarlo() {
        Long id = 9L;
        Medico m = crearMedico(id, null, "66666666-6", "Jorge", "Muñoz",
                "jorge@test.cl", "+56966666666", 1L, true);
        when(repository.findById(id)).thenReturn(Optional.of(m));
        service.eliminar(id);
        verify(repository).findById(id);
        verify(repository).delete(m);
    }

    // ── TEST 10: activar ─────────────────────────────────────────────────────
    @Test
    void activar_debeGuardarMedicoActivoYRetornarMapeado() {
        Long id = 10L;
        Medico inactivo = crearMedico(id, null, "77777777-7", "Paula", "Castro",
                "paula@test.cl", "+56977777777", 1L, false);
        when(repository.findById(id)).thenReturn(Optional.of(inactivo));
        when(repository.save(inactivo)).thenReturn(inactivo);
        MedicoResponseDTO r = service.activar(id);
        assertThat(r.getActivo()).isTrue();
        verify(repository).save(inactivo);
    }

    // ── TEST 11: desactivar ──────────────────────────────────────────────────
    @Test
    void desactivar_debeGuardarMedicoInactivoYRetornarMapeado() {
        Long id = 11L;
        Medico activo = crearMedico(id, null, "88888888-8", "Ricardo", "Vega",
                "ricardo@test.cl", "+56988888888", 1L, true);
        when(repository.findById(id)).thenReturn(Optional.of(activo));
        when(repository.save(activo)).thenReturn(activo);
        MedicoResponseDTO r = service.desactivar(id);
        assertThat(r.getActivo()).isFalse();
        verify(repository).save(activo);
    }

    // ═════════════════════════════════════════════════════════════════════════
    //  ERROR PATH
    // ═════════════════════════════════════════════════════════════════════════

    // ── TEST 12: buscarPorId — no existe ─────────────────────────────────────
    @Test
    void buscarPorId_cuandoNoExisteDebeLanzarMedicoNotFoundException() {
        Long id = 99L;
        when(repository.findById(id)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> service.buscarPorId(id))
                .isInstanceOf(MedicoNotFoundException.class)
                .hasMessage("Médico con ID 99 no encontrado");
    }

    // ── TEST 13: buscarPorEspecialidad — especialidad no existe ──────────────
    @Test
    void buscarPorEspecialidad_cuandoEspecialidadNoExisteDebeLanzarFeignException() {
        Long espId = 999L;
        when(especialidadClient.obtenerEspecialidad(espId))
                .thenThrow(FeignException.NotFound.class);
        assertThatThrownBy(() -> service.buscarPorEspecialidad(espId))
                .isInstanceOf(FeignException.NotFound.class);
        verify(repository, never()).findByEspecialidadId(any());
    }

    // ── TEST 14: buscarActivosPorEspecialidad — especialidad no existe ───────
    @Test
    void buscarActivosPorEspecialidad_cuandoEspecialidadNoExisteDebeLanzarFeignException() {
        Long espId = 999L;
        when(especialidadClient.obtenerEspecialidad(espId))
                .thenThrow(FeignException.NotFound.class);
        assertThatThrownBy(() -> service.buscarActivosPorEspecialidad(espId))
                .isInstanceOf(FeignException.NotFound.class);
        verify(repository, never()).findByEspecialidadIdAndActivoTrue(any());
    }

    // ── TEST 15: crear — RUN duplicado ───────────────────────────────────────
    @Test
    void crear_cuandoRunDuplicadoDebeLanzarRuntimeException() {
        MedicoRequestDTO req = crearRequest("12345678-9", "Carlos", "Lopez",
                "carlos@test.cl", "+56912345678", 1L);
        when(especialidadClient.obtenerEspecialidad(1L)).thenReturn(new Object());
        when(repository.existsByRun("12345678-9")).thenReturn(true);
        assertThatThrownBy(() -> service.crear(req))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Ya existe un médico registrado con ese RUN");
        verify(repository, never()).save(any(Medico.class));
    }

    // ── TEST 16: crear — email duplicado ─────────────────────────────────────
    @Test
    void crear_cuandoEmailDuplicadoDebeLanzarRuntimeException() {
        MedicoRequestDTO req = crearRequest("12345678-9", "Carlos", "Lopez",
                "carlos@test.cl", "+56912345678", 1L);
        when(especialidadClient.obtenerEspecialidad(1L)).thenReturn(new Object());
        when(repository.existsByRun("12345678-9")).thenReturn(false);
        when(repository.existsByEmailIgnoreCase("carlos@test.cl")).thenReturn(true);
        assertThatThrownBy(() -> service.crear(req))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Ya existe un médico registrado con ese email");
        verify(repository, never()).save(any(Medico.class));
    }

    // ── TEST 17: crear — ms-auth falla ───────────────────────────────────────
    @Test
    void crear_cuandoAuthFallaDebeLanzarRuntimeException() {
        MedicoRequestDTO req = crearRequest("12345678-9", "Carlos", "Lopez",
                "carlos@test.cl", "+56912345678", 1L);
        req.setUsername("carlos.lopez");
        req.setPassword("pass123");
        when(especialidadClient.obtenerEspecialidad(1L)).thenReturn(new Object());
        when(repository.existsByRun("12345678-9")).thenReturn(false);
        when(repository.existsByEmailIgnoreCase("carlos@test.cl")).thenReturn(false);
        when(authClient.registerUser(any(), eq("DOCTOR")))
                .thenThrow(new RuntimeException("ms-auth no disponible"));
        assertThatThrownBy(() -> service.crear(req))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("No se pudo crear el usuario DOCTOR en ms-auth");
        verify(repository, never()).save(any(Medico.class));
    }

    // ── TEST 18: actualizar — RUN duplicado en otro médico ───────────────────
    @Test
    void actualizar_cuandoRunDuplicadoEnOtroMedicoDebeLanzarRuntimeException() {
        Long id = 8L;
        MedicoRequestDTO req = crearRequest("55555555-5", "Elena", "Vargas",
                "elena@test.cl", "+56955555555", 2L);
        Medico existente = crearMedico(id, null, "ORIGINAL", "Original", "Orig",
                "orig@test.cl", "+56900000000", 1L, true);
        Medico otro = crearMedico(99L, null, "55555555-5", "Otro", "Med",
                "otro@test.cl", "+56999999999", 1L, true);
        when(repository.findById(id)).thenReturn(Optional.of(existente));
        when(especialidadClient.obtenerEspecialidad(2L)).thenReturn(new Object());
        when(repository.findByRun("55555555-5")).thenReturn(Optional.of(otro));
        assertThatThrownBy(() -> service.actualizar(id, req))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Ya existe otro médico registrado con ese RUN");
        verify(repository, never()).save(any(Medico.class));
    }

    // ── TEST 19: actualizar — email duplicado en otro médico ─────────────────
    @Test
    void actualizar_cuandoEmailDuplicadoEnOtroMedicoDebeLanzarRuntimeException() {
        Long id = 8L;
        MedicoRequestDTO req = crearRequest("55555555-5", "Elena", "Vargas",
                "elena@test.cl", "+56955555555", 2L);
        Medico existente = crearMedico(id, null, "55555555-5", "Elena", "Vargas",
                "ORIGINAL@test.cl", "+56900000000", 1L, true);
        Medico otro = crearMedico(99L, null, "99999999-9", "Otro", "Med",
                "elena@test.cl", "+56999999999", 1L, true);
        when(repository.findById(id)).thenReturn(Optional.of(existente));
        when(especialidadClient.obtenerEspecialidad(2L)).thenReturn(new Object());
        when(repository.findByRun("55555555-5")).thenReturn(Optional.of(existente));
        when(repository.findByEmailIgnoreCase("elena@test.cl")).thenReturn(Optional.of(otro));
        assertThatThrownBy(() -> service.actualizar(id, req))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Ya existe otro médico registrado con ese email");
        verify(repository, never()).save(any(Medico.class));
    }

    // ── TEST 20: actualizar — médico no existe ──────────────────────────────
    @Test
    void actualizar_cuandoMedicoNoExisteDebeLanzarMedicoNotFoundException() {
        Long id = 77L;
        MedicoRequestDTO req = crearRequest("55555555-5", "Elena", "Vargas",
                "elena@test.cl", "+56955555555", 2L);
        when(repository.findById(id)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> service.actualizar(id, req))
                .isInstanceOf(MedicoNotFoundException.class)
                .hasMessage("Médico con ID 77 no encontrado");
        verify(repository, never()).findByRun(any());
        verify(repository, never()).save(any(Medico.class));
    }

    // ── TEST 21: eliminar — médico no existe ─────────────────────────────────
    @Test
    void eliminar_cuandoMedicoNoExisteDebeLanzarMedicoNotFoundException() {
        Long id = 88L;
        when(repository.findById(id)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> service.eliminar(id))
                .isInstanceOf(MedicoNotFoundException.class)
                .hasMessage("Médico con ID 88 no encontrado");
        verify(repository, never()).delete(any(Medico.class));
    }
}
