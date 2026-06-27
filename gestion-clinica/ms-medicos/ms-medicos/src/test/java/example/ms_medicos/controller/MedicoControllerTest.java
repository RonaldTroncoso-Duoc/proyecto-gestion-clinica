package example.ms_medicos.controller;

import example.ms_medicos.dto.MedicoResponseDTO;
import example.ms_medicos.service.MedicoService;
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
import static org.mockito.ArgumentMatchers.eq;
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

/**
 * Pruebas unitarias para MedicoController.
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
 *  Para ejecutar SOLO este test (desde el proyecto padre):
 *    mvn -pl ms-medicos -Dtest=MedicoControllerTest test
 *
 *  Para ejecutar TODOS los tests del microservicio:
 *    mvn -pl ms-medicos test
 *
 * ───────────────────────────────────────────────────────────────────────────────
 *  DÓNDE REVISAR EL REPORTE
 * ───────────────────────────────────────────────────────────────────────────────
 *  ms-medicos/target/surefire-reports/
 *  Archivo: TEST-example.ms_medicos.controller.MedicoControllerTest.xml
 * ═══════════════════════════════════════════════════════════════════════════════
 */
@ExtendWith(MockitoExtension.class)
class MedicoControllerTest {

    @Mock
    private MedicoService medicoService;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        MedicoController controller = new MedicoController(medicoService);
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    // ── helpers ──────────────────────────────────────────────────────────────

    private MedicoResponseDTO crearMedicoResponse(Long id, String run, String nombre,
                                                   String apellido, String email,
                                                   String telefono, Long especialidadId,
                                                   Boolean activo) {
        return MedicoResponseDTO.builder()
                .id(id).run(run).nombre(nombre).apellido(apellido)
                .email(email).telefono(telefono)
                .especialidadId(especialidadId).activo(activo)
                .build();
    }

    // ── TEST 1: GET /api/medicos ──────────────────────────────────────────────

    @Test
    void listarTodos_debeRetornarOkConMedicos() throws Exception {
        // ARRANGE
        MedicoResponseDTO medico = crearMedicoResponse(1L, "12345678-9",
                "Carlos", "Lopez", "carlos.lopez@test.cl",
                "+56912345678", 1L, true);
        when(medicoService.listarTodos()).thenReturn(List.of(medico));

        // ACT
        mockMvc.perform(get("/api/medicos"))
                // ASSERT
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].run").value("12345678-9"))
                .andExpect(jsonPath("$[0].nombre").value("Carlos"))
                .andExpect(jsonPath("$[0].apellido").value("Lopez"))
                .andExpect(jsonPath("$[0].email").value("carlos.lopez@test.cl"))
                .andExpect(jsonPath("$[0].especialidadId").value(1))
                .andExpect(jsonPath("$[0].activo").value(true));

        // VERIFY
        verify(medicoService).listarTodos();

        // FALLA: HTTP 403 Forbidden en vez de 200 OK.
        // QA: GET /api/medicos no retorna lista.
        // Desarrollo: revisar seguridad del endpoint.
    }

    // ── TEST 2: GET /api/medicos/activos ──────────────────────────────────────

    @Test
    void listarActivos_debeRetornarOkConMedicosActivos() throws Exception {
        // ARRANGE
        MedicoResponseDTO m = crearMedicoResponse(2L, "98765432-1",
                "Ana", "Martinez", "ana.martinez@test.cl",
                "+56987654321", 2L, true);
        when(medicoService.listarActivos()).thenReturn(List.of(m));

        // ACT
        mockMvc.perform(get("/api/medicos/activos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(2L))
                .andExpect(jsonPath("$[0].nombre").value("Ana"))
                .andExpect(jsonPath("$[0].activo").value(true));

        verify(medicoService).listarActivos();
        // FALLA: lista con médicos inactivos.
    }

    // ── TEST 3: GET /api/medicos/{id} ─────────────────────────────────────────

    @Test
    void buscarPorId_debeRetornarOkConMedico() throws Exception {
        // ARRANGE
        Long medicoId = 3L;
        MedicoResponseDTO m = crearMedicoResponse(medicoId, "11111111-1",
                "Pedro", "Gonzalez", "pedro.gonzalez@test.cl",
                "+56911111111", 1L, true);
        when(medicoService.buscarPorId(medicoId)).thenReturn(m);

        // ACT
        mockMvc.perform(get("/api/medicos/{id}", medicoId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(3L))
                .andExpect(jsonPath("$.nombre").value("Pedro"))
                .andExpect(jsonPath("$.activo").value(true));

        verify(medicoService).buscarPorId(medicoId);
        // FALLA: 404 Not Found para médico existente.
    }

    // ── TEST 4: GET /api/medicos/especialidad/{especialidadId} ────────────────

    @Test
    void buscarPorEspecialidad_debeRetornarOkConMedicos() throws Exception {
        // ARRANGE
        Long espId = 1L;
        MedicoResponseDTO m = crearMedicoResponse(4L, "22222222-2",
                "Laura", "Diaz", "laura.diaz@test.cl",
                "+56922222222", espId, true);
        when(medicoService.buscarPorEspecialidad(espId)).thenReturn(List.of(m));

        // ACT
        mockMvc.perform(get("/api/medicos/especialidad/{especialidadId}", espId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(4L))
                .andExpect(jsonPath("$[0].nombre").value("Laura"))
                .andExpect(jsonPath("$[0].especialidadId").value(1));

        verify(medicoService).buscarPorEspecialidad(espId);
        // FALLA: médicos de otra especialidad en el listado.
    }

    // ── TEST 5: GET /api/medicos/especialidad/{especialidadId}/activos ────────

    @Test
    void buscarActivosPorEspecialidad_debeRetornarOkConMedicosActivos() throws Exception {
        // ARRANGE
        Long espId = 2L;
        MedicoResponseDTO m = crearMedicoResponse(5L, "33333333-3",
                "Sofia", "Ramirez", "sofia.ramirez@test.cl",
                "+56933333333", espId, true);
        when(medicoService.buscarActivosPorEspecialidad(espId)).thenReturn(List.of(m));

        // ACT
        mockMvc.perform(get("/api/medicos/especialidad/{especialidadId}/activos", espId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(5L))
                .andExpect(jsonPath("$[0].especialidadId").value(2))
                .andExpect(jsonPath("$[0].activo").value(true));

        verify(medicoService).buscarActivosPorEspecialidad(espId);
        // FALLA: médicos inactivos en el listado de activos por especialidad.
    }

    // ── TEST 6: POST /api/medicos ─────────────────────────────────────────────

    @Test
    void crear_debeRetornarCreatedConMedicoCreado() throws Exception {
        // ARRANGE
        String json = """
                { "run": "12345678-9", "nombre": "Carlos", "apellido": "Lopez",
                  "email": "carlos.lopez@test.cl", "telefono": "+56912345678",
                  "especialidadId": 1 }
                """;
        MedicoResponseDTO r = crearMedicoResponse(6L, "12345678-9",
                "Carlos", "Lopez", "carlos.lopez@test.cl",
                "+56912345678", 1L, true);
        when(medicoService.crear(any())).thenReturn(r);

        // ACT
        mockMvc.perform(post("/api/medicos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(6L))
                .andExpect(jsonPath("$.nombre").value("Carlos"))
                .andExpect(jsonPath("$.activo").value(true));

        verify(medicoService).crear(any());
        // FALLA: HTTP 200 OK en vez de 201 Created.
    }

    // ── TEST 7: PUT /api/medicos/{id} ─────────────────────────────────────────

    @Test
    void actualizar_debeRetornarOkConMedicoActualizado() throws Exception {
        // ARRANGE
        Long id = 7L;
        String json = """
                { "run": "44444444-4", "nombre": "Miguel", "apellido": "Torres",
                  "email": "miguel.torres@test.cl", "telefono": "+56944444444",
                  "especialidadId": 1 }
                """;
        MedicoResponseDTO r = crearMedicoResponse(id, "44444444-4",
                "Miguel", "Torres", "miguel.torres@test.cl",
                "+56944444444", 1L, true);
        when(medicoService.actualizar(eq(id), any())).thenReturn(r);

        // ACT
        mockMvc.perform(put("/api/medicos/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(7L))
                .andExpect(jsonPath("$.nombre").value("Miguel"))
                .andExpect(jsonPath("$.activo").value(true));

        verify(medicoService).actualizar(eq(id), any());
        // FALLA: HTTP 404 Not Found para médico existente.
    }

    // ── TEST 8: PATCH /api/medicos/{id}/activar ───────────────────────────────

    @Test
    void activar_debeRetornarOkConMedicoActivo() throws Exception {
        // ARRANGE
        Long id = 8L;
        MedicoResponseDTO r = crearMedicoResponse(id, "55555555-5",
                "Elena", "Vargas", "elena.vargas@test.cl",
                "+56955555555", 1L, true);
        when(medicoService.activar(id)).thenReturn(r);

        // ACT
        mockMvc.perform(patch("/api/medicos/{id}/activar", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(8L))
                .andExpect(jsonPath("$.nombre").value("Elena"))
                .andExpect(jsonPath("$.activo").value(true));

        verify(medicoService).activar(id);
        // FALLA: activo=false después de activar.
    }

    // ── TEST 9: PATCH /api/medicos/{id}/desactivar ────────────────────────────

    @Test
    void desactivar_debeRetornarOkConMedicoInactivo() throws Exception {
        // ARRANGE
        Long id = 9L;
        MedicoResponseDTO r = crearMedicoResponse(id, "66666666-6",
                "Jorge", "Muñoz", "jorge.munoz@test.cl",
                "+56966666666", 2L, false);
        when(medicoService.desactivar(id)).thenReturn(r);

        // ACT
        mockMvc.perform(patch("/api/medicos/{id}/desactivar", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(9L))
                .andExpect(jsonPath("$.nombre").value("Jorge"))
                .andExpect(jsonPath("$.activo").value(false));

        verify(medicoService).desactivar(id);
        // FALLA: activo=true después de desactivar.
    }

    // ── TEST 10: DELETE /api/medicos/{id} ─────────────────────────────────────

    @Test
    void eliminar_debeRetornarNoContent() throws Exception {
        // ARRANGE
        Long id = 10L;
        doNothing().when(medicoService).eliminar(id);

        // ACT
        mockMvc.perform(delete("/api/medicos/{id}", id))
                .andExpect(status().isNoContent());

        verify(medicoService).eliminar(id);
        // FALLA: HTTP 200 OK en vez de 204 No Content.
    }
}