package example.ms_especialidades.controller;

import example.ms_especialidades.dto.EspecialidadResponseDTO;
import example.ms_especialidades.service.EspecialidadService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class EspecialidadControllerTest {

    @Mock
    private EspecialidadService especialidadService;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        EspecialidadController controller = new EspecialidadController(especialidadService);
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
    void listarTodas_debeRetornarOkConEspecialidades() throws Exception {
        // ARRANGE: preparar datos y mocks.
        EspecialidadResponseDTO especialidad = EspecialidadResponseDTO.builder()
                .id(1L)
                .nombre("Cardiologia")
                .descripcion("Atencion de enfermedades cardiovasculares")
                .activo(true)
                .build();

        when(especialidadService.listarTodas()).thenReturn(List.of(especialidad));

        // ACT: ejecutar método o endpoint.
        mockMvc.perform(get("/api/especialidades"))
                // ASSERT: verificar resultado esperado.
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].nombre").value("Cardiologia"))
                .andExpect(jsonPath("$[0].descripcion").value("Atencion de enfermedades cardiovasculares"))
                .andExpect(jsonPath("$[0].activo").value(true));

        // VERIFY: comprobar llamadas al mock si corresponde.
        verify(especialidadService).listarTodas();

        // Un ejemplo de falla posible.
        // Se esperaba: HTTP 200 OK.
        // Se obtuvo: HTTP 403 Forbidden.
        // QA deberia reportarlo asi: El endpoint GET /api/especialidades no retorna la lista de especialidades.
        // Desarrollo deberia revisar: EspecialidadController.listarTodas() y la configuracion de seguridad del endpoint.
    }

    @Test
    void listarActivas_debeRetornarOkConEspecialidadesActivas() throws Exception {
        // ARRANGE: preparar datos y mocks.
        EspecialidadResponseDTO especialidadActiva = EspecialidadResponseDTO.builder()
                .id(2L)
                .nombre("Pediatria")
                .descripcion("Atencion medica infantil")
                .activo(true)
                .build();

        when(especialidadService.listarActivas()).thenReturn(List.of(especialidadActiva));

        // ACT: ejecutar método o endpoint.
        mockMvc.perform(get("/api/especialidades/activas"))
                // ASSERT: verificar resultado esperado.
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(2L))
                .andExpect(jsonPath("$[0].nombre").value("Pediatria"))
                .andExpect(jsonPath("$[0].descripcion").value("Atencion medica infantil"))
                .andExpect(jsonPath("$[0].activo").value(true));

        // VERIFY: comprobar llamadas al mock si corresponde.
        verify(especialidadService).listarActivas();

        // Un ejemplo de falla posible.
        // Se esperaba: lista con especialidades activas.
        // Se obtuvo: lista con una especialidad inactiva.
        // QA deberia reportarlo asi: El endpoint GET /api/especialidades/activas retorna especialidades inactivas.
        // Desarrollo deberia revisar: EspecialidadController.listarActivas() y EspecialidadService.listarActivas().
    }

    @Test
    void buscarPorId_debeRetornarOkConEspecialidad() throws Exception {
        // ARRANGE: preparar datos y mocks.
        Long especialidadId = 3L;
        EspecialidadResponseDTO especialidad = EspecialidadResponseDTO.builder()
                .id(especialidadId)
                .nombre("Dermatologia")
                .descripcion("Atencion de enfermedades de la piel")
                .activo(true)
                .build();

        when(especialidadService.buscarPorId(especialidadId)).thenReturn(especialidad);

        // ACT: ejecutar método o endpoint.
        mockMvc.perform(get("/api/especialidades/{id}", especialidadId))
                // ASSERT: verificar resultado esperado.
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(3L))
                .andExpect(jsonPath("$.nombre").value("Dermatologia"))
                .andExpect(jsonPath("$.descripcion").value("Atencion de enfermedades de la piel"))
                .andExpect(jsonPath("$.activo").value(true));

        // VERIFY: comprobar llamadas al mock si corresponde.
        verify(especialidadService).buscarPorId(especialidadId);

        // Un ejemplo de falla posible.
        // Se esperaba: especialidad con ID 3.
        // Se obtuvo: especialidad con otro ID.
        // QA deberia reportarlo asi: El endpoint GET /api/especialidades/3 retorna una especialidad distinta a la solicitada.
        // Desarrollo deberia revisar: EspecialidadController.buscarPorId() y EspecialidadService.buscarPorId().
    }

    @Test
    void crear_debeRetornarCreatedConEspecialidadCreada() throws Exception {
        // ARRANGE: preparar datos y mocks.
        String requestJson = """
                {
                  "nombre": "Neurologia",
                  "descripcion": "Atencion de enfermedades del sistema nervioso"
                }
                """;

        EspecialidadResponseDTO especialidadCreada = EspecialidadResponseDTO.builder()
                .id(4L)
                .nombre("Neurologia")
                .descripcion("Atencion de enfermedades del sistema nervioso")
                .activo(true)
                .build();

        when(especialidadService.crear(any())).thenReturn(especialidadCreada);

        // ACT: ejecutar método o endpoint.
        mockMvc.perform(post("/api/especialidades")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                // ASSERT: verificar resultado esperado.
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(4L))
                .andExpect(jsonPath("$.nombre").value("Neurologia"))
                .andExpect(jsonPath("$.descripcion").value("Atencion de enfermedades del sistema nervioso"))
                .andExpect(jsonPath("$.activo").value(true));

        // VERIFY: comprobar llamadas al mock si corresponde.
        verify(especialidadService).crear(any());

        // Un ejemplo de falla posible.
        // Se esperaba: HTTP 201 Created.
        // Se obtuvo: HTTP 200 OK.
        // QA deberia reportarlo asi: El endpoint POST /api/especialidades crea la especialidad pero responde un estado incorrecto.
        // Desarrollo deberia revisar: EspecialidadController.crear() y el ResponseEntity con HttpStatus.CREATED.
    }

    @Test
    void actualizar_debeRetornarOkConEspecialidadActualizada() throws Exception {
        // ARRANGE: preparar datos y mocks.
        Long especialidadId = 5L;
        String requestJson = """
                {
                  "nombre": "Traumatologia",
                  "descripcion": "Atencion de lesiones del sistema musculoesqueletico"
                }
                """;

        EspecialidadResponseDTO especialidadActualizada = EspecialidadResponseDTO.builder()
                .id(especialidadId)
                .nombre("Traumatologia")
                .descripcion("Atencion de lesiones del sistema musculoesqueletico")
                .activo(true)
                .build();

        when(especialidadService.actualizar(org.mockito.ArgumentMatchers.eq(especialidadId), any()))
                .thenReturn(especialidadActualizada);

        // ACT: ejecutar método o endpoint.
        mockMvc.perform(put("/api/especialidades/{id}", especialidadId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                // ASSERT: verificar resultado esperado.
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(5L))
                .andExpect(jsonPath("$.nombre").value("Traumatologia"))
                .andExpect(jsonPath("$.descripcion").value("Atencion de lesiones del sistema musculoesqueletico"))
                .andExpect(jsonPath("$.activo").value(true));

        // VERIFY: comprobar llamadas al mock si corresponde.
        verify(especialidadService).actualizar(org.mockito.ArgumentMatchers.eq(especialidadId), any());

        // Un ejemplo de falla posible.
        // Se esperaba: HTTP 200 OK.
        // Se obtuvo: HTTP 404 Not Found.
        // QA deberia reportarlo asi: El endpoint PUT /api/especialidades/5 no actualiza una especialidad existente.
        // Desarrollo deberia revisar: EspecialidadController.actualizar() y EspecialidadService.actualizar().
    }

    @Test
    void eliminar_debeRetornarNoContent() throws Exception {
        // ARRANGE: preparar datos y mocks.
        Long especialidadId = 6L;
        doNothing().when(especialidadService).eliminar(especialidadId);

        // ACT: ejecutar método o endpoint.
        mockMvc.perform(delete("/api/especialidades/{id}", especialidadId))
                // ASSERT: verificar resultado esperado.
                .andExpect(status().isNoContent());

        // VERIFY: comprobar llamadas al mock si corresponde.
        verify(especialidadService).eliminar(especialidadId);

        // Un ejemplo de falla posible.
        // Se esperaba: HTTP 204 No Content.
        // Se obtuvo: HTTP 200 OK.
        // QA deberia reportarlo asi: El endpoint DELETE /api/especialidades/6 elimina la especialidad pero responde un estado incorrecto.
        // Desarrollo deberia revisar: EspecialidadController.eliminar() y la respuesta noContent().
    }

    @Test
    void activar_debeRetornarOkConEspecialidadActiva() throws Exception {
        // ARRANGE: preparar datos y mocks.
        Long especialidadId = 7L;
        EspecialidadResponseDTO especialidadActiva = EspecialidadResponseDTO.builder()
                .id(especialidadId)
                .nombre("Oftalmologia")
                .descripcion("Atencion de enfermedades visuales")
                .activo(true)
                .build();

        when(especialidadService.activar(especialidadId)).thenReturn(especialidadActiva);

        // ACT: ejecutar método o endpoint.
        mockMvc.perform(patch("/api/especialidades/{id}/activar", especialidadId))
                // ASSERT: verificar resultado esperado.
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(7L))
                .andExpect(jsonPath("$.nombre").value("Oftalmologia"))
                .andExpect(jsonPath("$.descripcion").value("Atencion de enfermedades visuales"))
                .andExpect(jsonPath("$.activo").value(true));

        // VERIFY: comprobar llamadas al mock si corresponde.
        verify(especialidadService).activar(especialidadId);

        // Un ejemplo de falla posible.
        // Se esperaba: especialidad con activo=true.
        // Se obtuvo: especialidad con activo=false.
        // QA deberia reportarlo asi: El endpoint PATCH /api/especialidades/7/activar responde correctamente, pero la especialidad sigue inactiva.
        // Desarrollo deberia revisar: EspecialidadController.activar() y EspecialidadService.activar().
    }

    @Test
    void desactivar_debeRetornarOkConEspecialidadInactiva() throws Exception {
        // ARRANGE: preparar datos y mocks.
        Long especialidadId = 8L;
        EspecialidadResponseDTO especialidadInactiva = EspecialidadResponseDTO.builder()
                .id(especialidadId)
                .nombre("Kinesiologia")
                .descripcion("Atencion de rehabilitacion fisica")
                .activo(false)
                .build();

        when(especialidadService.desactivar(especialidadId)).thenReturn(especialidadInactiva);

        // ACT: ejecutar método o endpoint.
        mockMvc.perform(patch("/api/especialidades/{id}/desactivar", especialidadId))
                // ASSERT: verificar resultado esperado.
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(8L))
                .andExpect(jsonPath("$.nombre").value("Kinesiologia"))
                .andExpect(jsonPath("$.descripcion").value("Atencion de rehabilitacion fisica"))
                .andExpect(jsonPath("$.activo").value(false));

        // VERIFY: comprobar llamadas al mock si corresponde.
        verify(especialidadService).desactivar(especialidadId);

        // Un ejemplo de falla posible.
        // Se esperaba: especialidad con activo=false.
        // Se obtuvo: especialidad con activo=true.
        // QA deberia reportarlo asi: El endpoint PATCH /api/especialidades/8/desactivar responde correctamente, pero la especialidad sigue activa.
        // Desarrollo deberia revisar: EspecialidadController.desactivar() y EspecialidadService.desactivar().
    }
}
