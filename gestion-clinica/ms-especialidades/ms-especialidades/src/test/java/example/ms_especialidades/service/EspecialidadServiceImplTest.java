package example.ms_especialidades.service;

import example.ms_especialidades.dto.EspecialidadRequestDTO;
import example.ms_especialidades.dto.EspecialidadResponseDTO;
import example.ms_especialidades.exception.EspecialidadNotFoundException;
import example.ms_especialidades.model.Especialidad;
import example.ms_especialidades.repository.EspecialidadRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EspecialidadServiceImplTest {

    @Mock
    private EspecialidadRepository repository;

    @InjectMocks
    private EspecialidadServiceImpl service;

    @Test
    void listarTodas_debeRetornarEspecialidadesMapeadas() {
        // ARRANGE: preparar datos y mocks.
        Especialidad especialidad = Especialidad.builder()
                .id(1L)
                .nombre("Cardiologia")
                .descripcion("Atencion de enfermedades cardiovasculares")
                .activo(true)
                .build();

        when(repository.findAll()).thenReturn(List.of(especialidad));

        // ACT: ejecutar método o endpoint.
        List<EspecialidadResponseDTO> resultado = service.listarTodas();

        // ASSERT: verificar resultado esperado.
        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).getId()).isEqualTo(1L);
        assertThat(resultado.get(0).getNombre()).isEqualTo("Cardiologia");
        assertThat(resultado.get(0).getDescripcion()).isEqualTo("Atencion de enfermedades cardiovasculares");
        assertThat(resultado.get(0).getActivo()).isTrue();

        // VERIFY: comprobar llamadas al mock si corresponde.
        verify(repository).findAll();

        // Un ejemplo de falla posible.
        // Se esperaba: lista con 1 especialidad.
        // Se obtuvo: lista vacia.
        // QA deberia reportarlo asi: El metodo listarTodas() no retorna las especialidades existentes.
        // Desarrollo deberia revisar: EspecialidadServiceImpl.listarTodas(), EspecialidadRepository.findAll() y el mapeo a EspecialidadResponseDTO.
    }

    @Test
    void listarActivas_debeRetornarEspecialidadesActivasMapeadas() {
        // ARRANGE: preparar datos y mocks.
        Especialidad especialidadActiva = Especialidad.builder()
                .id(2L)
                .nombre("Pediatria")
                .descripcion("Atencion medica infantil")
                .activo(true)
                .build();

        when(repository.findByActivoTrue()).thenReturn(List.of(especialidadActiva));

        // ACT: ejecutar método o endpoint.
        List<EspecialidadResponseDTO> resultado = service.listarActivas();

        // ASSERT: verificar resultado esperado.
        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).getId()).isEqualTo(2L);
        assertThat(resultado.get(0).getNombre()).isEqualTo("Pediatria");
        assertThat(resultado.get(0).getDescripcion()).isEqualTo("Atencion medica infantil");
        assertThat(resultado.get(0).getActivo()).isTrue();

        // VERIFY: comprobar llamadas al mock si corresponde.
        verify(repository).findByActivoTrue();

        // Un ejemplo de falla posible.
        // Se esperaba: lista con especialidades activas.
        // Se obtuvo: lista vacia.
        // QA deberia reportarlo asi: El metodo listarActivas() no retorna especialidades activas existentes.
        // Desarrollo deberia revisar: EspecialidadServiceImpl.listarActivas(), EspecialidadRepository.findByActivoTrue() y el mapeo a EspecialidadResponseDTO.
    }

    @Test
    void buscarPorId_debeRetornarEspecialidadMapeada() {
        // ARRANGE: preparar datos y mocks.
        Long especialidadId = 3L;
        Especialidad especialidad = Especialidad.builder()
                .id(especialidadId)
                .nombre("Dermatologia")
                .descripcion("Atencion de enfermedades de la piel")
                .activo(true)
                .build();

        when(repository.findById(especialidadId)).thenReturn(Optional.of(especialidad));

        // ACT: ejecutar método o endpoint.
        EspecialidadResponseDTO resultado = service.buscarPorId(especialidadId);

        // ASSERT: verificar resultado esperado.
        assertThat(resultado.getId()).isEqualTo(3L);
        assertThat(resultado.getNombre()).isEqualTo("Dermatologia");
        assertThat(resultado.getDescripcion()).isEqualTo("Atencion de enfermedades de la piel");
        assertThat(resultado.getActivo()).isTrue();

        // VERIFY: comprobar llamadas al mock si corresponde.
        verify(repository).findById(especialidadId);

        // Un ejemplo de falla posible.
        // Se esperaba: especialidad con ID 3.
        // Se obtuvo: especialidad con otro ID.
        // QA deberia reportarlo asi: El metodo buscarPorId(3) retorna una especialidad distinta a la solicitada.
        // Desarrollo deberia revisar: EspecialidadServiceImpl.buscarPorId(), obtenerEntidadPorId() y EspecialidadRepository.findById().
    }

    @Test
    void crear_debeGuardarEspecialidadYRetornarEspecialidadMapeada() {
        // ARRANGE: preparar datos y mocks.
        EspecialidadRequestDTO request = new EspecialidadRequestDTO();
        request.setNombre("Neurologia");
        request.setDescripcion("Atencion de enfermedades del sistema nervioso");

        Especialidad especialidadGuardada = Especialidad.builder()
                .id(4L)
                .nombre("Neurologia")
                .descripcion("Atencion de enfermedades del sistema nervioso")
                .activo(true)
                .build();

        when(repository.existsByNombreIgnoreCase("Neurologia")).thenReturn(false);
        when(repository.save(any(Especialidad.class))).thenReturn(especialidadGuardada);

        // ACT: ejecutar método o endpoint.
        EspecialidadResponseDTO resultado = service.crear(request);

        // ASSERT: verificar resultado esperado.
        assertThat(resultado.getId()).isEqualTo(4L);
        assertThat(resultado.getNombre()).isEqualTo("Neurologia");
        assertThat(resultado.getDescripcion()).isEqualTo("Atencion de enfermedades del sistema nervioso");
        assertThat(resultado.getActivo()).isTrue();

        // VERIFY: comprobar llamadas al mock si corresponde.
        verify(repository).existsByNombreIgnoreCase("Neurologia");
        verify(repository).save(any(Especialidad.class));

        // Un ejemplo de falla posible.
        // Se esperaba: especialidad creada con activo=true.
        // Se obtuvo: especialidad creada con activo=false.
        // QA deberia reportarlo asi: El metodo crear() guarda la especialidad, pero queda inactiva despues de la creacion.
        // Desarrollo deberia revisar: EspecialidadServiceImpl.crear(), la construccion de Especialidad y EspecialidadRepository.save().
    }

    @Test
    void actualizar_debeGuardarCambiosYRetornarEspecialidadActualizada() {
        // ARRANGE: preparar datos y mocks.
        Long especialidadId = 5L;
        EspecialidadRequestDTO request = new EspecialidadRequestDTO();
        request.setNombre("Traumatologia");
        request.setDescripcion("Atencion de lesiones del sistema musculoesqueletico");

        Especialidad especialidadExistente = Especialidad.builder()
                .id(especialidadId)
                .nombre("Nombre Antiguo")
                .descripcion("Descripcion antigua")
                .activo(true)
                .build();

        Especialidad especialidadActualizada = Especialidad.builder()
                .id(especialidadId)
                .nombre("Traumatologia")
                .descripcion("Atencion de lesiones del sistema musculoesqueletico")
                .activo(true)
                .build();

        when(repository.findById(especialidadId)).thenReturn(Optional.of(especialidadExistente));
        when(repository.findByNombreIgnoreCase("Traumatologia")).thenReturn(Optional.empty());
        when(repository.save(especialidadExistente)).thenReturn(especialidadActualizada);

        // ACT: ejecutar método o endpoint.
        EspecialidadResponseDTO resultado = service.actualizar(especialidadId, request);

        // ASSERT: verificar resultado esperado.
        assertThat(resultado.getId()).isEqualTo(5L);
        assertThat(resultado.getNombre()).isEqualTo("Traumatologia");
        assertThat(resultado.getDescripcion()).isEqualTo("Atencion de lesiones del sistema musculoesqueletico");
        assertThat(resultado.getActivo()).isTrue();

        // VERIFY: comprobar llamadas al mock si corresponde.
        verify(repository).findById(especialidadId);
        verify(repository).findByNombreIgnoreCase("Traumatologia");
        verify(repository).save(especialidadExistente);

        // Un ejemplo de falla posible.
        // Se esperaba: especialidad actualizada con nombre Traumatologia.
        // Se obtuvo: especialidad con nombre antiguo.
        // QA deberia reportarlo asi: El metodo actualizar() responde correctamente, pero no persiste los nuevos datos de la especialidad.
        // Desarrollo deberia revisar: EspecialidadServiceImpl.actualizar(), las asignaciones de campos y EspecialidadRepository.save().
    }

    @Test
    void eliminar_debeBuscarEspecialidadYEliminarla() {
        // ARRANGE: preparar datos y mocks.
        Long especialidadId = 6L;
        Especialidad especialidad = Especialidad.builder()
                .id(especialidadId)
                .nombre("Oftalmologia")
                .descripcion("Atencion de enfermedades visuales")
                .activo(true)
                .build();

        when(repository.findById(especialidadId)).thenReturn(Optional.of(especialidad));

        // ACT: ejecutar método o endpoint.
        service.eliminar(especialidadId);

        // ASSERT: verificar resultado esperado.
        assertThat(especialidad.getId()).isEqualTo(6L);

        // VERIFY: comprobar llamadas al mock si corresponde.
        verify(repository).findById(especialidadId);
        verify(repository).delete(especialidad);

        // Un ejemplo de falla posible.
        // Se esperaba: especialidad eliminada.
        // Se obtuvo: especialidad sigue existiendo.
        // QA deberia reportarlo asi: El metodo eliminar() finaliza sin error, pero la especialidad no fue eliminada.
        // Desarrollo deberia revisar: EspecialidadServiceImpl.eliminar(), obtenerEntidadPorId() y EspecialidadRepository.delete().
    }

    @Test
    void activar_debeGuardarEspecialidadActivaYRetornarEspecialidadMapeada() {
        // ARRANGE: preparar datos y mocks.
        Long especialidadId = 7L;
        Especialidad especialidadInactiva = Especialidad.builder()
                .id(especialidadId)
                .nombre("Oftalmologia")
                .descripcion("Atencion de enfermedades visuales")
                .activo(false)
                .build();

        Especialidad especialidadActiva = Especialidad.builder()
                .id(especialidadId)
                .nombre("Oftalmologia")
                .descripcion("Atencion de enfermedades visuales")
                .activo(true)
                .build();

        when(repository.findById(especialidadId)).thenReturn(Optional.of(especialidadInactiva));
        when(repository.save(especialidadInactiva)).thenReturn(especialidadActiva);

        // ACT: ejecutar método o endpoint.
        EspecialidadResponseDTO resultado = service.activar(especialidadId);

        // ASSERT: verificar resultado esperado.
        assertThat(resultado.getId()).isEqualTo(7L);
        assertThat(resultado.getNombre()).isEqualTo("Oftalmologia");
        assertThat(resultado.getDescripcion()).isEqualTo("Atencion de enfermedades visuales");
        assertThat(resultado.getActivo()).isTrue();

        // VERIFY: comprobar llamadas al mock si corresponde.
        verify(repository).findById(especialidadId);
        verify(repository).save(especialidadInactiva);

        // Un ejemplo de falla posible.
        // Se esperaba: especialidad con activo=true.
        // Se obtuvo: especialidad con activo=false.
        // QA deberia reportarlo asi: El metodo activar() responde correctamente, pero la especialidad sigue inactiva.
        // Desarrollo deberia revisar: EspecialidadServiceImpl.activar(), la asignacion de activo y EspecialidadRepository.save().
    }

    @Test
    void desactivar_debeGuardarEspecialidadInactivaYRetornarEspecialidadMapeada() {
        // ARRANGE: preparar datos y mocks.
        Long especialidadId = 8L;
        Especialidad especialidadActiva = Especialidad.builder()
                .id(especialidadId)
                .nombre("Kinesiologia")
                .descripcion("Atencion de rehabilitacion fisica")
                .activo(true)
                .build();

        Especialidad especialidadInactiva = Especialidad.builder()
                .id(especialidadId)
                .nombre("Kinesiologia")
                .descripcion("Atencion de rehabilitacion fisica")
                .activo(false)
                .build();

        when(repository.findById(especialidadId)).thenReturn(Optional.of(especialidadActiva));
        when(repository.save(especialidadActiva)).thenReturn(especialidadInactiva);

        // ACT: ejecutar método o endpoint.
        EspecialidadResponseDTO resultado = service.desactivar(especialidadId);

        // ASSERT: verificar resultado esperado.
        assertThat(resultado.getId()).isEqualTo(8L);
        assertThat(resultado.getNombre()).isEqualTo("Kinesiologia");
        assertThat(resultado.getDescripcion()).isEqualTo("Atencion de rehabilitacion fisica");
        assertThat(resultado.getActivo()).isFalse();

        // VERIFY: comprobar llamadas al mock si corresponde.
        verify(repository).findById(especialidadId);
        verify(repository).save(especialidadActiva);

        // Un ejemplo de falla posible.
        // Se esperaba: especialidad con activo=false.
        // Se obtuvo: especialidad con activo=true.
        // QA deberia reportarlo asi: El metodo desactivar() responde correctamente, pero la especialidad sigue activa.
        // Desarrollo deberia revisar: EspecialidadServiceImpl.desactivar(), la asignacion de activo y EspecialidadRepository.save().
    }

    @Test
    void buscarPorId_cuandoNoExisteDebeLanzarEspecialidadNotFoundException() {
        // ARRANGE: preparar datos y mocks.
        Long especialidadId = 99L;
        when(repository.findById(especialidadId)).thenReturn(Optional.empty());

        // ACT: ejecutar método o endpoint.
        Throwable exception = org.assertj.core.api.Assertions.catchThrowable(() -> service.buscarPorId(especialidadId));

        // ASSERT: verificar resultado esperado.
        assertThat(exception)
                .isInstanceOf(EspecialidadNotFoundException.class)
                .hasMessage("Especialidad con ID 99 no encontrada");

        // VERIFY: comprobar llamadas al mock si corresponde.
        verify(repository).findById(especialidadId);
        verify(repository, never()).save(any(Especialidad.class));

        // Un ejemplo de falla posible.
        // Se esperaba: EspecialidadNotFoundException.
        // Se obtuvo: respuesta nula sin excepcion.
        // QA deberia reportarlo asi: El metodo buscarPorId(99) no informa correctamente que la especialidad no existe.
        // Desarrollo deberia revisar: EspecialidadServiceImpl.buscarPorId(), obtenerEntidadPorId() y el manejo de Optional.empty().
    }

    @Test
    void crear_cuandoNombreExisteDebeLanzarRuntimeException() {
        // ARRANGE: preparar datos y mocks.
        EspecialidadRequestDTO request = new EspecialidadRequestDTO();
        request.setNombre("Neurologia");
        request.setDescripcion("Atencion de enfermedades del sistema nervioso");

        when(repository.existsByNombreIgnoreCase("Neurologia")).thenReturn(true);

        // ACT: ejecutar método o endpoint.
        Throwable exception = org.assertj.core.api.Assertions.catchThrowable(() -> service.crear(request));

        // ASSERT: verificar resultado esperado.
        assertThat(exception)
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Ya existe una especialidad registrada con ese nombre");

        // VERIFY: comprobar llamadas al mock si corresponde.
        verify(repository).existsByNombreIgnoreCase("Neurologia");
        verify(repository, never()).save(any(Especialidad.class));

        // Un ejemplo de falla posible.
        // Se esperaba: RuntimeException por nombre duplicado.
        // Se obtuvo: especialidad guardada con nombre duplicado.
        // QA deberia reportarlo asi: El metodo crear() permite registrar dos especialidades con el mismo nombre.
        // Desarrollo deberia revisar: EspecialidadServiceImpl.crear() y la validacion repository.existsByNombreIgnoreCase().
    }

    @Test
    void actualizar_cuandoNombrePerteneceAOtraEspecialidadDebeLanzarRuntimeException() {
        // ARRANGE: preparar datos y mocks.
        Long especialidadId = 5L;
        EspecialidadRequestDTO request = new EspecialidadRequestDTO();
        request.setNombre("Traumatologia");
        request.setDescripcion("Atencion de lesiones del sistema musculoesqueletico");

        Especialidad especialidadExistente = Especialidad.builder()
                .id(especialidadId)
                .nombre("Nombre Antiguo")
                .descripcion("Descripcion antigua")
                .activo(true)
                .build();

        Especialidad otraEspecialidad = Especialidad.builder()
                .id(6L)
                .nombre("Traumatologia")
                .descripcion("Otra especialidad")
                .activo(true)
                .build();

        when(repository.findById(especialidadId)).thenReturn(Optional.of(especialidadExistente));
        when(repository.findByNombreIgnoreCase("Traumatologia")).thenReturn(Optional.of(otraEspecialidad));

        // ACT: ejecutar método o endpoint.
        Throwable exception = org.assertj.core.api.Assertions.catchThrowable(() -> service.actualizar(especialidadId, request));

        // ASSERT: verificar resultado esperado.
        assertThat(exception)
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Ya existe otra especialidad con ese nombre");

        // VERIFY: comprobar llamadas al mock si corresponde.
        verify(repository).findById(especialidadId);
        verify(repository).findByNombreIgnoreCase("Traumatologia");
        verify(repository, never()).save(any(Especialidad.class));

        // Un ejemplo de falla posible.
        // Se esperaba: RuntimeException por nombre duplicado en otra especialidad.
        // Se obtuvo: especialidad actualizada con nombre duplicado.
        // QA deberia reportarlo asi: El metodo actualizar() permite que dos especialidades tengan el mismo nombre.
        // Desarrollo deberia revisar: EspecialidadServiceImpl.actualizar() y la validacion repository.findByNombreIgnoreCase().
    }

    @Test
    void actualizar_cuandoEspecialidadNoExisteDebeLanzarEspecialidadNotFoundException() {
        // ARRANGE: preparar datos y mocks.
        Long especialidadId = 77L;
        EspecialidadRequestDTO request = new EspecialidadRequestDTO();
        request.setNombre("Traumatologia");
        request.setDescripcion("Atencion de lesiones del sistema musculoesqueletico");

        when(repository.findById(especialidadId)).thenReturn(Optional.empty());

        // ACT: ejecutar método o endpoint.
        Throwable exception = org.assertj.core.api.Assertions.catchThrowable(() -> service.actualizar(especialidadId, request));

        // ASSERT: verificar resultado esperado.
        assertThat(exception)
                .isInstanceOf(EspecialidadNotFoundException.class)
                .hasMessage("Especialidad con ID 77 no encontrada");

        // VERIFY: comprobar llamadas al mock si corresponde.
        verify(repository).findById(especialidadId);
        verify(repository, never()).findByNombreIgnoreCase("Traumatologia");
        verify(repository, never()).save(any(Especialidad.class));

        // Un ejemplo de falla posible.
        // Se esperaba: EspecialidadNotFoundException.
        // Se obtuvo: especialidad creada o actualizada sin existir previamente.
        // QA deberia reportarlo asi: El metodo actualizar(77) no informa correctamente que la especialidad no existe.
        // Desarrollo deberia revisar: EspecialidadServiceImpl.actualizar(), obtenerEntidadPorId() y EspecialidadRepository.findById().
    }
}
