package example.ms_pacientes.service;

import example.ms_pacientes.client.AuthClient;
import example.ms_pacientes.dto.AuthUserResponseDTO;
import example.ms_pacientes.dto.PacienteRequestDTO;
import example.ms_pacientes.dto.PacienteResponseDTO;
import example.ms_pacientes.exception.PacienteNotFoundException;
import example.ms_pacientes.model.Paciente;
import example.ms_pacientes.repository.PacienteRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PacienteServiceImplTest {

    @Mock
    private PacienteRepository repository;

    @Mock
    private AuthClient authClient;

    @InjectMocks
    private PacienteServiceImpl service;

    @Test
    void listarTodos_debeRetornarPacientesMapeados() {
        // ARRANGE: preparar datos y mocks.
        Paciente paciente = Paciente.builder()
                .id(1L)
                .authUserId(10L)
                .run("12345678-9")
                .nombre("Ana")
                .apellido("Perez")
                .email("ana.perez@test.cl")
                .telefono("+56912345678")
                .fechaNacimiento(LocalDate.of(1991, 1, 15))
                .direccion("Direccion 123")
                .activo(true)
                .build();

        when(repository.findAll()).thenReturn(List.of(paciente));

        // ACT: ejecutar método o endpoint.
        List<PacienteResponseDTO> resultado = service.listarTodos();

        // ASSERT: verificar resultado esperado.
        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).getId()).isEqualTo(1L);
        assertThat(resultado.get(0).getAuthUserId()).isEqualTo(10L);
        assertThat(resultado.get(0).getRun()).isEqualTo("12345678-9");
        assertThat(resultado.get(0).getNombre()).isEqualTo("Ana");
        assertThat(resultado.get(0).getApellido()).isEqualTo("Perez");
        assertThat(resultado.get(0).getEmail()).isEqualTo("ana.perez@test.cl");
        assertThat(resultado.get(0).getActivo()).isTrue();

        // VERIFY: comprobar llamadas al mock si corresponde.
        verify(repository).findAll();

        // Un ejemplo de falla posible.
        // Se esperaba: lista con 1 paciente.
        // Se obtuvo: lista vacia.
        // QA deberia reportarlo asi: El metodo listarTodos() no retorna los pacientes existentes.
        // Desarrollo deberia revisar: PacienteServiceImpl.listarTodos(), PacienteRepository.findAll() y el mapeo a PacienteResponseDTO.
    }

    @Test
    void listarActivos_debeRetornarPacientesActivosMapeados() {
        // ARRANGE: preparar datos y mocks.
        Paciente pacienteActivo = Paciente.builder()
                .id(2L)
                .authUserId(20L)
                .run("98765432-1")
                .nombre("Luis")
                .apellido("Gomez")
                .email("luis.gomez@test.cl")
                .telefono("+56987654321")
                .fechaNacimiento(LocalDate.of(1985, 6, 20))
                .direccion("Calle Activa 456")
                .activo(true)
                .build();

        when(repository.findByActivoTrue()).thenReturn(List.of(pacienteActivo));

        // ACT: ejecutar método o endpoint.
        List<PacienteResponseDTO> resultado = service.listarActivos();

        // ASSERT: verificar resultado esperado.
        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).getId()).isEqualTo(2L);
        assertThat(resultado.get(0).getRun()).isEqualTo("98765432-1");
        assertThat(resultado.get(0).getNombre()).isEqualTo("Luis");
        assertThat(resultado.get(0).getApellido()).isEqualTo("Gomez");
        assertThat(resultado.get(0).getEmail()).isEqualTo("luis.gomez@test.cl");
        assertThat(resultado.get(0).getActivo()).isTrue();

        // VERIFY: comprobar llamadas al mock si corresponde.
        verify(repository).findByActivoTrue();

        // Un ejemplo de falla posible.
        // Se esperaba: lista con pacientes activos.
        // Se obtuvo: lista vacia.
        // QA deberia reportarlo asi: El metodo listarActivos() no retorna pacientes activos existentes.
        // Desarrollo deberia revisar: PacienteServiceImpl.listarActivos(), PacienteRepository.findByActivoTrue() y el mapeo a PacienteResponseDTO.
    }

    @Test
    void buscarPorId_debeRetornarPacienteMapeado() {
        // ARRANGE: preparar datos y mocks.
        Long pacienteId = 3L;
        Paciente paciente = Paciente.builder()
                .id(pacienteId)
                .authUserId(30L)
                .run("11111111-1")
                .nombre("Marta")
                .apellido("Rojas")
                .email("marta.rojas@test.cl")
                .telefono("+56911111111")
                .fechaNacimiento(LocalDate.of(1992, 4, 12))
                .direccion("Pasaje 789")
                .activo(true)
                .build();

        when(repository.findById(pacienteId)).thenReturn(Optional.of(paciente));

        // ACT: ejecutar método o endpoint.
        PacienteResponseDTO resultado = service.buscarPorId(pacienteId);

        // ASSERT: verificar resultado esperado.
        assertThat(resultado.getId()).isEqualTo(3L);
        assertThat(resultado.getAuthUserId()).isEqualTo(30L);
        assertThat(resultado.getRun()).isEqualTo("11111111-1");
        assertThat(resultado.getNombre()).isEqualTo("Marta");
        assertThat(resultado.getApellido()).isEqualTo("Rojas");
        assertThat(resultado.getEmail()).isEqualTo("marta.rojas@test.cl");
        assertThat(resultado.getActivo()).isTrue();

        // VERIFY: comprobar llamadas al mock si corresponde.
        verify(repository).findById(pacienteId);

        // Un ejemplo de falla posible.
        // Se esperaba: paciente con ID 3.
        // Se obtuvo: paciente con otro ID.
        // QA deberia reportarlo asi: El metodo buscarPorId(3) retorna un paciente distinto al solicitado.
        // Desarrollo deberia revisar: PacienteServiceImpl.buscarPorId(), obtenerEntidadPorId() y PacienteRepository.findById().
    }

    @Test
    void buscarPorRun_debeRetornarPacienteMapeado() {
        // ARRANGE: preparar datos y mocks.
        String run = "22222222-2";
        Paciente paciente = Paciente.builder()
                .id(4L)
                .authUserId(40L)
                .run(run)
                .nombre("Carlos")
                .apellido("Munoz")
                .email("carlos.munoz@test.cl")
                .telefono("+56922222222")
                .fechaNacimiento(LocalDate.of(1993, 8, 5))
                .direccion("Avenida 100")
                .activo(true)
                .build();

        when(repository.findByRun(run)).thenReturn(Optional.of(paciente));

        // ACT: ejecutar método o endpoint.
        PacienteResponseDTO resultado = service.buscarPorRun(run);

        // ASSERT: verificar resultado esperado.
        assertThat(resultado.getId()).isEqualTo(4L);
        assertThat(resultado.getAuthUserId()).isEqualTo(40L);
        assertThat(resultado.getRun()).isEqualTo("22222222-2");
        assertThat(resultado.getNombre()).isEqualTo("Carlos");
        assertThat(resultado.getApellido()).isEqualTo("Munoz");
        assertThat(resultado.getEmail()).isEqualTo("carlos.munoz@test.cl");
        assertThat(resultado.getActivo()).isTrue();

        // VERIFY: comprobar llamadas al mock si corresponde.
        verify(repository).findByRun(run);

        // Un ejemplo de falla posible.
        // Se esperaba: paciente con RUN 22222222-2.
        // Se obtuvo: paciente con otro RUN.
        // QA deberia reportarlo asi: El metodo buscarPorRun("22222222-2") retorna un paciente distinto al solicitado.
        // Desarrollo deberia revisar: PacienteServiceImpl.buscarPorRun() y PacienteRepository.findByRun().
    }

    @Test
    void crear_debeGuardarPacienteYRetornarPacienteMapeado() {
        // ARRANGE: preparar datos y mocks.
        PacienteRequestDTO request = new PacienteRequestDTO();
        request.setRun("44444444-4");
        request.setNombre("Diego");
        request.setApellido("Silva");
        request.setEmail("diego.silva@test.cl");
        request.setTelefono("+56944444444");
        request.setFechaNacimiento(LocalDate.of(1990, 3, 10));
        request.setDireccion("Calle Admin 456");

        Paciente pacienteGuardado = Paciente.builder()
                .id(5L)
                .run("44444444-4")
                .nombre("Diego")
                .apellido("Silva")
                .email("diego.silva@test.cl")
                .telefono("+56944444444")
                .fechaNacimiento(LocalDate.of(1990, 3, 10))
                .direccion("Calle Admin 456")
                .activo(true)
                .build();

        when(repository.existsByRun("44444444-4")).thenReturn(false);
        when(repository.existsByEmailIgnoreCase("diego.silva@test.cl")).thenReturn(false);
        when(repository.save(any(Paciente.class))).thenReturn(pacienteGuardado);

        // ACT: ejecutar método o endpoint.
        PacienteResponseDTO resultado = service.crear(request);

        // ASSERT: verificar resultado esperado.
        assertThat(resultado.getId()).isEqualTo(5L);
        assertThat(resultado.getRun()).isEqualTo("44444444-4");
        assertThat(resultado.getNombre()).isEqualTo("Diego");
        assertThat(resultado.getApellido()).isEqualTo("Silva");
        assertThat(resultado.getEmail()).isEqualTo("diego.silva@test.cl");
        assertThat(resultado.getActivo()).isTrue();

        // VERIFY: comprobar llamadas al mock si corresponde.
        verify(repository).existsByRun("44444444-4");
        verify(repository).existsByEmailIgnoreCase("diego.silva@test.cl");
        verify(repository).save(any(Paciente.class));

        // Un ejemplo de falla posible.
        // Se esperaba: paciente creado con activo=true.
        // Se obtuvo: paciente creado con activo=false.
        // QA deberia reportarlo asi: El metodo crear() guarda el paciente, pero queda inactivo despues de la creacion.
        // Desarrollo deberia revisar: PacienteServiceImpl.crear(), la construccion de Paciente y PacienteRepository.save().
    }

    @Test
    void registrar_debeCrearUsuarioAuthGuardarPacienteYRetornarPacienteMapeado() {
        // ARRANGE: preparar datos y mocks.
        PacienteRequestDTO request = new PacienteRequestDTO();
        request.setRun("33333333-3");
        request.setNombre("Sofia");
        request.setApellido("Diaz");
        request.setEmail("sofia.diaz@test.cl");
        request.setTelefono("+56933333333");
        request.setFechaNacimiento(LocalDate.of(1995, 5, 20));
        request.setDireccion("Calle Registro 123");
        request.setUsername("sofia.diaz");
        request.setPassword("password123");

        AuthUserResponseDTO authResponse = AuthUserResponseDTO.builder()
                .id(50L)
                .username("sofia.diaz")
                .email("sofia.diaz@test.cl")
                .build();

        Paciente pacienteGuardado = Paciente.builder()
                .id(6L)
                .authUserId(50L)
                .run("33333333-3")
                .nombre("Sofia")
                .apellido("Diaz")
                .email("sofia.diaz@test.cl")
                .telefono("+56933333333")
                .fechaNacimiento(LocalDate.of(1995, 5, 20))
                .direccion("Calle Registro 123")
                .activo(true)
                .build();

        when(repository.existsByRun("33333333-3")).thenReturn(false);
        when(repository.existsByEmailIgnoreCase("sofia.diaz@test.cl")).thenReturn(false);
        when(authClient.registerUser(any(), org.mockito.ArgumentMatchers.eq("PATIENT"))).thenReturn(authResponse);
        when(repository.save(any(Paciente.class))).thenReturn(pacienteGuardado);

        // ACT: ejecutar método o endpoint.
        PacienteResponseDTO resultado = service.registrar(request);

        // ASSERT: verificar resultado esperado.
        assertThat(resultado.getId()).isEqualTo(6L);
        assertThat(resultado.getAuthUserId()).isEqualTo(50L);
        assertThat(resultado.getRun()).isEqualTo("33333333-3");
        assertThat(resultado.getNombre()).isEqualTo("Sofia");
        assertThat(resultado.getApellido()).isEqualTo("Diaz");
        assertThat(resultado.getEmail()).isEqualTo("sofia.diaz@test.cl");
        assertThat(resultado.getActivo()).isTrue();

        // VERIFY: comprobar llamadas al mock si corresponde.
        verify(repository).existsByRun("33333333-3");
        verify(repository).existsByEmailIgnoreCase("sofia.diaz@test.cl");
        verify(authClient).registerUser(any(), org.mockito.ArgumentMatchers.eq("PATIENT"));
        verify(repository).save(any(Paciente.class));

        // Un ejemplo de falla posible.
        // Se esperaba: paciente registrado con authUserId=50.
        // Se obtuvo: paciente registrado sin authUserId.
        // QA deberia reportarlo asi: El metodo registrar() crea el paciente, pero no asocia el usuario de autenticacion.
        // Desarrollo deberia revisar: PacienteServiceImpl.registrar(), AuthClient.registerUser() y la asignacion de authUserId.
    }

    @Test
    void actualizar_debeGuardarCambiosYRetornarPacienteActualizado() {
        // ARRANGE: preparar datos y mocks.
        Long pacienteId = 7L;
        PacienteRequestDTO request = new PacienteRequestDTO();
        request.setRun("55555555-5");
        request.setNombre("Elena");
        request.setApellido("Torres");
        request.setEmail("elena.torres@test.cl");
        request.setTelefono("+56955555555");
        request.setFechaNacimiento(LocalDate.of(1988, 7, 15));
        request.setDireccion("Direccion Actualizada 789");

        Paciente pacienteExistente = Paciente.builder()
                .id(pacienteId)
                .authUserId(70L)
                .run("55555555-0")
                .nombre("Nombre Antiguo")
                .apellido("Apellido Antiguo")
                .email("antiguo@test.cl")
                .telefono("+56900000000")
                .fechaNacimiento(LocalDate.of(1988, 7, 15))
                .direccion("Direccion Antigua")
                .activo(true)
                .build();

        Paciente pacienteActualizado = Paciente.builder()
                .id(pacienteId)
                .authUserId(70L)
                .run("55555555-5")
                .nombre("Elena")
                .apellido("Torres")
                .email("elena.torres@test.cl")
                .telefono("+56955555555")
                .fechaNacimiento(LocalDate.of(1988, 7, 15))
                .direccion("Direccion Actualizada 789")
                .activo(true)
                .build();

        when(repository.findById(pacienteId)).thenReturn(Optional.of(pacienteExistente));
        when(repository.findByRun("55555555-5")).thenReturn(Optional.empty());
        when(repository.findByEmailIgnoreCase("elena.torres@test.cl")).thenReturn(Optional.empty());
        when(repository.save(pacienteExistente)).thenReturn(pacienteActualizado);

        // ACT: ejecutar método o endpoint.
        PacienteResponseDTO resultado = service.actualizar(pacienteId, request);

        // ASSERT: verificar resultado esperado.
        assertThat(resultado.getId()).isEqualTo(7L);
        assertThat(resultado.getAuthUserId()).isEqualTo(70L);
        assertThat(resultado.getRun()).isEqualTo("55555555-5");
        assertThat(resultado.getNombre()).isEqualTo("Elena");
        assertThat(resultado.getApellido()).isEqualTo("Torres");
        assertThat(resultado.getEmail()).isEqualTo("elena.torres@test.cl");
        assertThat(resultado.getActivo()).isTrue();

        // VERIFY: comprobar llamadas al mock si corresponde.
        verify(repository).findById(pacienteId);
        verify(repository).findByRun("55555555-5");
        verify(repository).findByEmailIgnoreCase("elena.torres@test.cl");
        verify(repository).save(pacienteExistente);

        // Un ejemplo de falla posible.
        // Se esperaba: paciente actualizado con nombre Elena.
        // Se obtuvo: paciente con nombre antiguo.
        // QA deberia reportarlo asi: El metodo actualizar() responde correctamente, pero no persiste los nuevos datos del paciente.
        // Desarrollo deberia revisar: PacienteServiceImpl.actualizar(), las asignaciones de campos y PacienteRepository.save().
    }

    @Test
    void eliminar_debeBuscarPacienteYEliminarlo() {
        // ARRANGE: preparar datos y mocks.
        Long pacienteId = 8L;
        Paciente paciente = Paciente.builder()
                .id(pacienteId)
                .authUserId(80L)
                .run("66666666-6")
                .nombre("Paula")
                .apellido("Castro")
                .email("paula.castro@test.cl")
                .telefono("+56966666666")
                .fechaNacimiento(LocalDate.of(1989, 9, 9))
                .direccion("Calle Eliminar 101")
                .activo(true)
                .build();

        when(repository.findById(pacienteId)).thenReturn(Optional.of(paciente));

        // ACT: ejecutar método o endpoint.
        service.eliminar(pacienteId);

        // ASSERT: verificar resultado esperado.
        assertThat(paciente.getId()).isEqualTo(8L);

        // VERIFY: comprobar llamadas al mock si corresponde.
        verify(repository).findById(pacienteId);
        verify(repository).delete(paciente);

        // Un ejemplo de falla posible.
        // Se esperaba: paciente eliminado.
        // Se obtuvo: paciente sigue existiendo.
        // QA deberia reportarlo asi: El metodo eliminar() finaliza sin error, pero el paciente no fue eliminado.
        // Desarrollo deberia revisar: PacienteServiceImpl.eliminar(), obtenerEntidadPorId() y PacienteRepository.delete().
    }

    @Test
    void activar_debeGuardarPacienteActivoYRetornarPacienteMapeado() {
        // ARRANGE: preparar datos y mocks.
        Long pacienteId = 9L;
        Paciente pacienteInactivo = Paciente.builder()
                .id(pacienteId)
                .authUserId(90L)
                .run("77777777-7")
                .nombre("Ricardo")
                .apellido("Vega")
                .email("ricardo.vega@test.cl")
                .telefono("+56977777777")
                .fechaNacimiento(LocalDate.of(1987, 10, 11))
                .direccion("Calle Activar 202")
                .activo(false)
                .build();

        Paciente pacienteActivo = Paciente.builder()
                .id(pacienteId)
                .authUserId(90L)
                .run("77777777-7")
                .nombre("Ricardo")
                .apellido("Vega")
                .email("ricardo.vega@test.cl")
                .telefono("+56977777777")
                .fechaNacimiento(LocalDate.of(1987, 10, 11))
                .direccion("Calle Activar 202")
                .activo(true)
                .build();

        when(repository.findById(pacienteId)).thenReturn(Optional.of(pacienteInactivo));
        when(repository.save(pacienteInactivo)).thenReturn(pacienteActivo);

        // ACT: ejecutar método o endpoint.
        PacienteResponseDTO resultado = service.activar(pacienteId);

        // ASSERT: verificar resultado esperado.
        assertThat(resultado.getId()).isEqualTo(9L);
        assertThat(resultado.getRun()).isEqualTo("77777777-7");
        assertThat(resultado.getNombre()).isEqualTo("Ricardo");
        assertThat(resultado.getActivo()).isTrue();

        // VERIFY: comprobar llamadas al mock si corresponde.
        verify(repository).findById(pacienteId);
        verify(repository).save(pacienteInactivo);

        // Un ejemplo de falla posible.
        // Se esperaba: paciente con activo=true.
        // Se obtuvo: paciente con activo=false.
        // QA deberia reportarlo asi: El metodo activar() responde correctamente, pero el paciente sigue inactivo.
        // Desarrollo deberia revisar: PacienteServiceImpl.activar(), la asignacion de activo y PacienteRepository.save().
    }

    @Test
    void desactivar_debeGuardarPacienteInactivoYRetornarPacienteMapeado() {
        // ARRANGE: preparar datos y mocks.
        Long pacienteId = 10L;
        Paciente pacienteActivo = Paciente.builder()
                .id(pacienteId)
                .authUserId(100L)
                .run("88888888-8")
                .nombre("Valeria")
                .apellido("Navarro")
                .email("valeria.navarro@test.cl")
                .telefono("+56988888888")
                .fechaNacimiento(LocalDate.of(1994, 2, 14))
                .direccion("Calle Desactivar 303")
                .activo(true)
                .build();

        Paciente pacienteInactivo = Paciente.builder()
                .id(pacienteId)
                .authUserId(100L)
                .run("88888888-8")
                .nombre("Valeria")
                .apellido("Navarro")
                .email("valeria.navarro@test.cl")
                .telefono("+56988888888")
                .fechaNacimiento(LocalDate.of(1994, 2, 14))
                .direccion("Calle Desactivar 303")
                .activo(false)
                .build();

        when(repository.findById(pacienteId)).thenReturn(Optional.of(pacienteActivo));
        when(repository.save(pacienteActivo)).thenReturn(pacienteInactivo);

        // ACT: ejecutar método o endpoint.
        PacienteResponseDTO resultado = service.desactivar(pacienteId);

        // ASSERT: verificar resultado esperado.
        assertThat(resultado.getId()).isEqualTo(10L);
        assertThat(resultado.getRun()).isEqualTo("88888888-8");
        assertThat(resultado.getNombre()).isEqualTo("Valeria");
        assertThat(resultado.getActivo()).isFalse();

        // VERIFY: comprobar llamadas al mock si corresponde.
        verify(repository).findById(pacienteId);
        verify(repository).save(pacienteActivo);

        // Un ejemplo de falla posible.
        // Se esperaba: paciente con activo=false.
        // Se obtuvo: paciente con activo=true.
        // QA deberia reportarlo asi: El metodo desactivar() responde correctamente, pero el paciente sigue activo.
        // Desarrollo deberia revisar: PacienteServiceImpl.desactivar(), la asignacion de activo y PacienteRepository.save().
    }

    @Test
    void buscarPorId_cuandoNoExisteDebeLanzarPacienteNotFoundException() {
        // ARRANGE: preparar datos y mocks.
        Long pacienteId = 99L;
        when(repository.findById(pacienteId)).thenReturn(Optional.empty());

        // ACT: ejecutar método o endpoint.
        Throwable exception = org.assertj.core.api.Assertions.catchThrowable(() -> service.buscarPorId(pacienteId));

        // ASSERT: verificar resultado esperado.
        assertThat(exception)
                .isInstanceOf(PacienteNotFoundException.class)
                .hasMessage("Paciente con ID 99 no encontrado");

        // VERIFY: comprobar llamadas al mock si corresponde.
        verify(repository).findById(pacienteId);
        verify(repository, never()).save(any(Paciente.class));

        // Un ejemplo de falla posible.
        // Se esperaba: PacienteNotFoundException.
        // Se obtuvo: respuesta nula sin excepcion.
        // QA deberia reportarlo asi: El metodo buscarPorId(99) no informa correctamente que el paciente no existe.
        // Desarrollo deberia revisar: PacienteServiceImpl.buscarPorId(), obtenerEntidadPorId() y el manejo de Optional.empty().
    }

    @Test
    void buscarPorRun_cuandoNoExisteDebeLanzarPacienteNotFoundException() {
        // ARRANGE: preparar datos y mocks.
        String run = "99999999-9";
        when(repository.findByRun(run)).thenReturn(Optional.empty());

        // ACT: ejecutar método o endpoint.
        Throwable exception = org.assertj.core.api.Assertions.catchThrowable(() -> service.buscarPorRun(run));

        // ASSERT: verificar resultado esperado.
        assertThat(exception)
                .isInstanceOf(PacienteNotFoundException.class)
                .hasMessage("Paciente con RUN 99999999-9 no encontrado");

        // VERIFY: comprobar llamadas al mock si corresponde.
        verify(repository).findByRun(run);
        verify(repository, never()).save(any(Paciente.class));

        // Un ejemplo de falla posible.
        // Se esperaba: PacienteNotFoundException.
        // Se obtuvo: respuesta nula sin excepcion.
        // QA deberia reportarlo asi: El metodo buscarPorRun("99999999-9") no informa correctamente que el paciente no existe.
        // Desarrollo deberia revisar: PacienteServiceImpl.buscarPorRun() y el manejo de Optional.empty().
    }

    @Test
    void crear_cuandoRunExisteDebeLanzarRuntimeException() {
        // ARRANGE: preparar datos y mocks.
        PacienteRequestDTO request = new PacienteRequestDTO();
        request.setRun("44444444-4");
        request.setNombre("Diego");
        request.setApellido("Silva");
        request.setEmail("diego.silva@test.cl");
        request.setTelefono("+56944444444");
        request.setFechaNacimiento(LocalDate.of(1990, 3, 10));
        request.setDireccion("Calle Admin 456");

        when(repository.existsByRun("44444444-4")).thenReturn(true);

        // ACT: ejecutar método o endpoint.
        Throwable exception = org.assertj.core.api.Assertions.catchThrowable(() -> service.crear(request));

        // ASSERT: verificar resultado esperado.
        assertThat(exception)
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Ya existe un paciente registrado con ese RUN");

        // VERIFY: comprobar llamadas al mock si corresponde.
        verify(repository).existsByRun("44444444-4");
        verify(repository, never()).existsByEmailIgnoreCase("diego.silva@test.cl");
        verify(repository, never()).save(any(Paciente.class));

        // Un ejemplo de falla posible.
        // Se esperaba: RuntimeException por RUN duplicado.
        // Se obtuvo: paciente guardado con RUN duplicado.
        // QA deberia reportarlo asi: El metodo crear() permite registrar dos pacientes con el mismo RUN.
        // Desarrollo deberia revisar: PacienteServiceImpl.crear() y la validacion repository.existsByRun().
    }

    @Test
    void crear_cuandoEmailExisteDebeLanzarRuntimeException() {
        // ARRANGE: preparar datos y mocks.
        PacienteRequestDTO request = new PacienteRequestDTO();
        request.setRun("44444444-4");
        request.setNombre("Diego");
        request.setApellido("Silva");
        request.setEmail("diego.silva@test.cl");
        request.setTelefono("+56944444444");
        request.setFechaNacimiento(LocalDate.of(1990, 3, 10));
        request.setDireccion("Calle Admin 456");

        when(repository.existsByRun("44444444-4")).thenReturn(false);
        when(repository.existsByEmailIgnoreCase("diego.silva@test.cl")).thenReturn(true);

        // ACT: ejecutar método o endpoint.
        Throwable exception = org.assertj.core.api.Assertions.catchThrowable(() -> service.crear(request));

        // ASSERT: verificar resultado esperado.
        assertThat(exception)
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Ya existe un paciente registrado con ese email");

        // VERIFY: comprobar llamadas al mock si corresponde.
        verify(repository).existsByRun("44444444-4");
        verify(repository).existsByEmailIgnoreCase("diego.silva@test.cl");
        verify(repository, never()).save(any(Paciente.class));

        // Un ejemplo de falla posible.
        // Se esperaba: RuntimeException por email duplicado.
        // Se obtuvo: paciente guardado con email duplicado.
        // QA deberia reportarlo asi: El metodo crear() permite registrar dos pacientes con el mismo email.
        // Desarrollo deberia revisar: PacienteServiceImpl.crear() y la validacion repository.existsByEmailIgnoreCase().
    }

    @Test
    void registrar_cuandoRunExisteDebeLanzarRuntimeException() {
        // ARRANGE: preparar datos y mocks.
        PacienteRequestDTO request = crearRequestRegistroPaciente();

        when(repository.existsByRun("33333333-3")).thenReturn(true);

        // ACT: ejecutar método o endpoint.
        Throwable exception = org.assertj.core.api.Assertions.catchThrowable(() -> service.registrar(request));

        // ASSERT: verificar resultado esperado.
        assertThat(exception)
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Ya existe un paciente registrado con ese RUN");

        // VERIFY: comprobar llamadas al mock si corresponde.
        verify(repository).existsByRun("33333333-3");
        verify(repository, never()).existsByEmailIgnoreCase("sofia.diaz@test.cl");
        verify(authClient, never()).registerUser(any(), any());
        verify(repository, never()).save(any(Paciente.class));

        // Un ejemplo de falla posible.
        // Se esperaba: RuntimeException por RUN duplicado.
        // Se obtuvo: usuario creado en ms-auth y paciente guardado.
        // QA deberia reportarlo asi: El metodo registrar() permite registrar un paciente con RUN duplicado.
        // Desarrollo deberia revisar: PacienteServiceImpl.registrar() y la validacion repository.existsByRun().
    }

    @Test
    void registrar_cuandoEmailExisteDebeLanzarRuntimeException() {
        // ARRANGE: preparar datos y mocks.
        PacienteRequestDTO request = crearRequestRegistroPaciente();

        when(repository.existsByRun("33333333-3")).thenReturn(false);
        when(repository.existsByEmailIgnoreCase("sofia.diaz@test.cl")).thenReturn(true);

        // ACT: ejecutar método o endpoint.
        Throwable exception = org.assertj.core.api.Assertions.catchThrowable(() -> service.registrar(request));

        // ASSERT: verificar resultado esperado.
        assertThat(exception)
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Ya existe un paciente registrado con ese email");

        // VERIFY: comprobar llamadas al mock si corresponde.
        verify(repository).existsByRun("33333333-3");
        verify(repository).existsByEmailIgnoreCase("sofia.diaz@test.cl");
        verify(authClient, never()).registerUser(any(), any());
        verify(repository, never()).save(any(Paciente.class));

        // Un ejemplo de falla posible.
        // Se esperaba: RuntimeException por email duplicado.
        // Se obtuvo: usuario creado en ms-auth y paciente guardado.
        // QA deberia reportarlo asi: El metodo registrar() permite registrar un paciente con email duplicado.
        // Desarrollo deberia revisar: PacienteServiceImpl.registrar() y la validacion repository.existsByEmailIgnoreCase().
    }

    @Test
    void registrar_cuandoAuthClientFallaDebeLanzarRuntimeException() {
        // ARRANGE: preparar datos y mocks.
        PacienteRequestDTO request = crearRequestRegistroPaciente();

        when(repository.existsByRun("33333333-3")).thenReturn(false);
        when(repository.existsByEmailIgnoreCase("sofia.diaz@test.cl")).thenReturn(false);
        when(authClient.registerUser(any(), org.mockito.ArgumentMatchers.eq("PATIENT")))
                .thenThrow(new RuntimeException("ms-auth no disponible"));

        // ACT: ejecutar método o endpoint.
        Throwable exception = org.assertj.core.api.Assertions.catchThrowable(() -> service.registrar(request));

        // ASSERT: verificar resultado esperado.
        assertThat(exception)
                .isInstanceOf(RuntimeException.class)
                .hasMessage("No se pudo crear el usuario de autenticación. ms-auth no disponible");

        // VERIFY: comprobar llamadas al mock si corresponde.
        verify(repository).existsByRun("33333333-3");
        verify(repository).existsByEmailIgnoreCase("sofia.diaz@test.cl");
        verify(authClient).registerUser(any(), org.mockito.ArgumentMatchers.eq("PATIENT"));
        verify(repository, never()).save(any(Paciente.class));

        // Un ejemplo de falla posible.
        // Se esperaba: RuntimeException por falla de ms-auth.
        // Se obtuvo: paciente guardado sin usuario de autenticacion.
        // QA deberia reportarlo asi: El metodo registrar() guarda el paciente aunque falla la creacion del usuario en ms-auth.
        // Desarrollo deberia revisar: PacienteServiceImpl.registrar(), el bloque try/catch de AuthClient y el orden de guardado.
    }

    @Test
    void actualizar_cuandoPacienteNoExisteDebeLanzarPacienteNotFoundException() {
        // ARRANGE: preparar datos y mocks.
        Long pacienteId = 77L;
        PacienteRequestDTO request = new PacienteRequestDTO();
        request.setRun("55555555-5");
        request.setNombre("Elena");
        request.setApellido("Torres");
        request.setEmail("elena.torres@test.cl");
        request.setTelefono("+56955555555");
        request.setFechaNacimiento(LocalDate.of(1988, 7, 15));
        request.setDireccion("Direccion Actualizada 789");

        when(repository.findById(pacienteId)).thenReturn(Optional.empty());

        // ACT: ejecutar método o endpoint.
        Throwable exception = org.assertj.core.api.Assertions.catchThrowable(() -> service.actualizar(pacienteId, request));

        // ASSERT: verificar resultado esperado.
        assertThat(exception)
                .isInstanceOf(PacienteNotFoundException.class)
                .hasMessage("Paciente con ID 77 no encontrado");

        // VERIFY: comprobar llamadas al mock si corresponde.
        verify(repository).findById(pacienteId);
        verify(repository, never()).findByRun("55555555-5");
        verify(repository, never()).findByEmailIgnoreCase("elena.torres@test.cl");
        verify(repository, never()).save(any(Paciente.class));

        // Un ejemplo de falla posible.
        // Se esperaba: PacienteNotFoundException.
        // Se obtuvo: paciente creado o actualizado sin existir previamente.
        // QA deberia reportarlo asi: El metodo actualizar(77) no informa correctamente que el paciente no existe.
        // Desarrollo deberia revisar: PacienteServiceImpl.actualizar(), obtenerEntidadPorId() y PacienteRepository.findById().
    }

    private PacienteRequestDTO crearRequestRegistroPaciente() {
        PacienteRequestDTO request = new PacienteRequestDTO();
        request.setRun("33333333-3");
        request.setNombre("Sofia");
        request.setApellido("Diaz");
        request.setEmail("sofia.diaz@test.cl");
        request.setTelefono("+56933333333");
        request.setFechaNacimiento(LocalDate.of(1995, 5, 20));
        request.setDireccion("Calle Registro 123");
        request.setUsername("sofia.diaz");
        request.setPassword("password123");
        return request;
    }
}
