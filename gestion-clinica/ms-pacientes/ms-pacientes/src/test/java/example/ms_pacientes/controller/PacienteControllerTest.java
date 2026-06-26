package example.ms_pacientes.controller;

import example.ms_pacientes.dto.PacienteResponseDTO;
import example.ms_pacientes.service.PacienteService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

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
class PacienteControllerTest {

    @Mock
    private PacienteService pacienteService;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        PacienteController controller = new PacienteController(pacienteService);
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
    void listarTodos_debeRetornarOkConPacientes() throws Exception {
        // ARRANGE: preparar datos y mocks.
        PacienteResponseDTO paciente = PacienteResponseDTO.builder()
                .id(1L)
                .authUserId(10L)
                .run("12345678-9")
                .nombre("Ana")
                .apellido("Perez")
                .email("ana.perez@test.cl")
                .telefono("+56912345678")
                .direccion("Direccion 123")
                .activo(true)
                .build();

        when(pacienteService.listarTodos()).thenReturn(List.of(paciente));

        // ACT: ejecutar método o endpoint.
        mockMvc.perform(get("/api/pacientes"))
                // ASSERT: verificar resultado esperado.
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].run").value("12345678-9"))
                .andExpect(jsonPath("$[0].nombre").value("Ana"))
                .andExpect(jsonPath("$[0].apellido").value("Perez"))
                .andExpect(jsonPath("$[0].email").value("ana.perez@test.cl"))
                .andExpect(jsonPath("$[0].activo").value(true));

        // VERIFY: comprobar llamadas al mock si corresponde.
        verify(pacienteService).listarTodos();

        // Un ejemplo de falla posible.
        // Se esperaba: HTTP 200 OK.
        // Se obtuvo: HTTP 403 Forbidden.
        // QA deberia reportarlo asi: El endpoint GET /api/pacientes no retorna la lista de pacientes.
        // Desarrollo deberia revisar: PacienteController.listarTodos() y la configuracion de seguridad del endpoint.
    }

    @Test
    void listarActivos_debeRetornarOkConPacientesActivos() throws Exception {
        // ARRANGE: preparar datos y mocks.
        PacienteResponseDTO pacienteActivo = PacienteResponseDTO.builder()
                .id(2L)
                .authUserId(20L)
                .run("98765432-1")
                .nombre("Luis")
                .apellido("Gomez")
                .email("luis.gomez@test.cl")
                .telefono("+56987654321")
                .direccion("Calle Activa 456")
                .activo(true)
                .build();

        when(pacienteService.listarActivos()).thenReturn(List.of(pacienteActivo));

        // ACT: ejecutar método o endpoint.
        mockMvc.perform(get("/api/pacientes/activos"))
                // ASSERT: verificar resultado esperado.
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(2L))
                .andExpect(jsonPath("$[0].run").value("98765432-1"))
                .andExpect(jsonPath("$[0].nombre").value("Luis"))
                .andExpect(jsonPath("$[0].apellido").value("Gomez"))
                .andExpect(jsonPath("$[0].email").value("luis.gomez@test.cl"))
                .andExpect(jsonPath("$[0].activo").value(true));

        // VERIFY: comprobar llamadas al mock si corresponde.
        verify(pacienteService).listarActivos();

        // Un ejemplo de falla posible.
        // Se esperaba: lista con pacientes activos.
        // Se obtuvo: lista con un paciente inactivo.
        // QA deberia reportarlo asi: El endpoint GET /api/pacientes/activos retorna pacientes inactivos.
        // Desarrollo deberia revisar: PacienteController.listarActivos() y PacienteService.listarActivos().
    }

    @Test
    void buscarPorId_debeRetornarOkConPaciente() throws Exception {
        // ARRANGE: preparar datos y mocks.
        Long pacienteId = 3L;
        PacienteResponseDTO paciente = PacienteResponseDTO.builder()
                .id(pacienteId)
                .authUserId(30L)
                .run("11111111-1")
                .nombre("Marta")
                .apellido("Rojas")
                .email("marta.rojas@test.cl")
                .telefono("+56911111111")
                .direccion("Pasaje 789")
                .activo(true)
                .build();

        when(pacienteService.buscarPorId(pacienteId)).thenReturn(paciente);

        // ACT: ejecutar método o endpoint.
        mockMvc.perform(get("/api/pacientes/{id}", pacienteId))
                // ASSERT: verificar resultado esperado.
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(3L))
                .andExpect(jsonPath("$.run").value("11111111-1"))
                .andExpect(jsonPath("$.nombre").value("Marta"))
                .andExpect(jsonPath("$.apellido").value("Rojas"))
                .andExpect(jsonPath("$.email").value("marta.rojas@test.cl"))
                .andExpect(jsonPath("$.activo").value(true));

        // VERIFY: comprobar llamadas al mock si corresponde.
        verify(pacienteService).buscarPorId(pacienteId);

        // Un ejemplo de falla posible.
        // Se esperaba: paciente con ID 3.
        // Se obtuvo: paciente con otro ID.
        // QA deberia reportarlo asi: El endpoint GET /api/pacientes/3 retorna un paciente distinto al solicitado.
        // Desarrollo deberia revisar: PacienteController.buscarPorId() y PacienteService.buscarPorId().
    }

    @Test
    void buscarPorRun_debeRetornarOkConPaciente() throws Exception {
        // ARRANGE: preparar datos y mocks.
        String run = "22222222-2";
        PacienteResponseDTO paciente = PacienteResponseDTO.builder()
                .id(4L)
                .authUserId(40L)
                .run(run)
                .nombre("Carlos")
                .apellido("Munoz")
                .email("carlos.munoz@test.cl")
                .telefono("+56922222222")
                .direccion("Avenida 100")
                .activo(true)
                .build();

        when(pacienteService.buscarPorRun(run)).thenReturn(paciente);

        // ACT: ejecutar método o endpoint.
        mockMvc.perform(get("/api/pacientes/run/{run}", run))
                // ASSERT: verificar resultado esperado.
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(4L))
                .andExpect(jsonPath("$.run").value("22222222-2"))
                .andExpect(jsonPath("$.nombre").value("Carlos"))
                .andExpect(jsonPath("$.apellido").value("Munoz"))
                .andExpect(jsonPath("$.email").value("carlos.munoz@test.cl"))
                .andExpect(jsonPath("$.activo").value(true));

        // VERIFY: comprobar llamadas al mock si corresponde.
        verify(pacienteService).buscarPorRun(run);

        // Un ejemplo de falla posible.
        // Se esperaba: paciente con RUN 22222222-2.
        // Se obtuvo: paciente con otro RUN.
        // QA deberia reportarlo asi: El endpoint GET /api/pacientes/run/22222222-2 retorna un RUN incorrecto.
        // Desarrollo deberia revisar: PacienteController.buscarPorRun() y PacienteService.buscarPorRun().
    }

    @Test
    void register_debeRetornarCreatedConPacienteRegistrado() throws Exception {
        // ARRANGE: preparar datos y mocks.
        String requestJson = """
                {
                  "run": "33333333-3",
                  "nombre": "Sofia",
                  "apellido": "Diaz",
                  "email": "sofia.diaz@test.cl",
                  "telefono": "+56933333333",
                  "fechaNacimiento": "1995-05-20",
                  "direccion": "Calle Registro 123",
                  "username": "sofia.diaz",
                  "password": "password123"
                }
                """;

        PacienteResponseDTO pacienteRegistrado = PacienteResponseDTO.builder()
                .id(5L)
                .authUserId(50L)
                .run("33333333-3")
                .nombre("Sofia")
                .apellido("Diaz")
                .email("sofia.diaz@test.cl")
                .telefono("+56933333333")
                .direccion("Calle Registro 123")
                .activo(true)
                .build();

        when(pacienteService.registrar(org.mockito.ArgumentMatchers.any())).thenReturn(pacienteRegistrado);

        // ACT: ejecutar método o endpoint.
        mockMvc.perform(post("/api/pacientes/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                // ASSERT: verificar resultado esperado.
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(5L))
                .andExpect(jsonPath("$.authUserId").value(50L))
                .andExpect(jsonPath("$.run").value("33333333-3"))
                .andExpect(jsonPath("$.nombre").value("Sofia"))
                .andExpect(jsonPath("$.apellido").value("Diaz"))
                .andExpect(jsonPath("$.email").value("sofia.diaz@test.cl"))
                .andExpect(jsonPath("$.activo").value(true));

        // VERIFY: comprobar llamadas al mock si corresponde.
        verify(pacienteService).registrar(org.mockito.ArgumentMatchers.any());

        // Un ejemplo de falla posible.
        // Se esperaba: HTTP 201 Created.
        // Se obtuvo: HTTP 200 OK.
        // QA deberia reportarlo asi: El endpoint POST /api/pacientes/register registra el paciente pero responde un estado incorrecto.
        // Desarrollo deberia revisar: PacienteController.register() y el ResponseEntity con HttpStatus.CREATED.
    }

    @Test
    void crear_debeRetornarCreatedConPacienteCreado() throws Exception {
        // ARRANGE: preparar datos y mocks.
        String requestJson = """
                {
                  "run": "44444444-4",
                  "nombre": "Diego",
                  "apellido": "Silva",
                  "email": "diego.silva@test.cl",
                  "telefono": "+56944444444",
                  "fechaNacimiento": "1990-03-10",
                  "direccion": "Calle Admin 456"
                }
                """;

        PacienteResponseDTO pacienteCreado = PacienteResponseDTO.builder()
                .id(6L)
                .run("44444444-4")
                .nombre("Diego")
                .apellido("Silva")
                .email("diego.silva@test.cl")
                .telefono("+56944444444")
                .direccion("Calle Admin 456")
                .activo(true)
                .build();

        when(pacienteService.crear(org.mockito.ArgumentMatchers.any())).thenReturn(pacienteCreado);

        // ACT: ejecutar método o endpoint.
        mockMvc.perform(post("/api/pacientes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                // ASSERT: verificar resultado esperado.
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(6L))
                .andExpect(jsonPath("$.run").value("44444444-4"))
                .andExpect(jsonPath("$.nombre").value("Diego"))
                .andExpect(jsonPath("$.apellido").value("Silva"))
                .andExpect(jsonPath("$.email").value("diego.silva@test.cl"))
                .andExpect(jsonPath("$.activo").value(true));

        // VERIFY: comprobar llamadas al mock si corresponde.
        verify(pacienteService).crear(org.mockito.ArgumentMatchers.any());

        // Un ejemplo de falla posible.
        // Se esperaba: HTTP 201 Created.
        // Se obtuvo: HTTP 400 Bad Request.
        // QA deberia reportarlo asi: El endpoint POST /api/pacientes rechaza un JSON valido para crear paciente.
        // Desarrollo deberia revisar: PacienteController.crear(), PacienteRequestDTO y las validaciones aplicadas.
    }

    @Test
    void actualizar_debeRetornarOkConPacienteActualizado() throws Exception {
        // ARRANGE: preparar datos y mocks.
        Long pacienteId = 7L;
        String requestJson = """
                {
                  "run": "55555555-5",
                  "nombre": "Elena",
                  "apellido": "Torres",
                  "email": "elena.torres@test.cl",
                  "telefono": "+56955555555",
                  "fechaNacimiento": "1988-07-15",
                  "direccion": "Direccion Actualizada 789"
                }
                """;

        PacienteResponseDTO pacienteActualizado = PacienteResponseDTO.builder()
                .id(pacienteId)
                .run("55555555-5")
                .nombre("Elena")
                .apellido("Torres")
                .email("elena.torres@test.cl")
                .telefono("+56955555555")
                .direccion("Direccion Actualizada 789")
                .activo(true)
                .build();

        when(pacienteService.actualizar(org.mockito.ArgumentMatchers.eq(pacienteId), org.mockito.ArgumentMatchers.any()))
                .thenReturn(pacienteActualizado);

        // ACT: ejecutar método o endpoint.
        mockMvc.perform(put("/api/pacientes/{id}", pacienteId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                // ASSERT: verificar resultado esperado.
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(7L))
                .andExpect(jsonPath("$.run").value("55555555-5"))
                .andExpect(jsonPath("$.nombre").value("Elena"))
                .andExpect(jsonPath("$.apellido").value("Torres"))
                .andExpect(jsonPath("$.email").value("elena.torres@test.cl"))
                .andExpect(jsonPath("$.activo").value(true));

        // VERIFY: comprobar llamadas al mock si corresponde.
        verify(pacienteService).actualizar(org.mockito.ArgumentMatchers.eq(pacienteId), org.mockito.ArgumentMatchers.any());

        // Un ejemplo de falla posible.
        // Se esperaba: HTTP 200 OK.
        // Se obtuvo: HTTP 404 Not Found.
        // QA deberia reportarlo asi: El endpoint PUT /api/pacientes/7 no actualiza un paciente existente.
        // Desarrollo deberia revisar: PacienteController.actualizar() y PacienteService.actualizar().
    }

    @Test
    void activar_debeRetornarOkConPacienteActivo() throws Exception {
        // ARRANGE: preparar datos y mocks.
        Long pacienteId = 8L;
        PacienteResponseDTO pacienteActivo = PacienteResponseDTO.builder()
                .id(pacienteId)
                .run("66666666-6")
                .nombre("Paula")
                .apellido("Castro")
                .email("paula.castro@test.cl")
                .telefono("+56966666666")
                .direccion("Calle Activar 101")
                .activo(true)
                .build();

        when(pacienteService.activar(pacienteId)).thenReturn(pacienteActivo);

        // ACT: ejecutar método o endpoint.
        mockMvc.perform(patch("/api/pacientes/{id}/activar", pacienteId))
                // ASSERT: verificar resultado esperado.
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(8L))
                .andExpect(jsonPath("$.run").value("66666666-6"))
                .andExpect(jsonPath("$.nombre").value("Paula"))
                .andExpect(jsonPath("$.apellido").value("Castro"))
                .andExpect(jsonPath("$.email").value("paula.castro@test.cl"))
                .andExpect(jsonPath("$.activo").value(true));

        // VERIFY: comprobar llamadas al mock si corresponde.
        verify(pacienteService).activar(pacienteId);

        // Un ejemplo de falla posible.
        // Se esperaba: activo=true.
        // Se obtuvo: activo=false.
        // QA deberia reportarlo asi: El endpoint PATCH /api/pacientes/8/activar responde sin activar el paciente.
        // Desarrollo deberia revisar: PacienteController.activar() y PacienteService.activar().
    }

    @Test
    void desactivar_debeRetornarOkConPacienteInactivo() throws Exception {
        // ARRANGE: preparar datos y mocks.
        Long pacienteId = 9L;
        PacienteResponseDTO pacienteInactivo = PacienteResponseDTO.builder()
                .id(pacienteId)
                .run("77777777-7")
                .nombre("Ricardo")
                .apellido("Vega")
                .email("ricardo.vega@test.cl")
                .telefono("+56977777777")
                .direccion("Calle Desactivar 202")
                .activo(false)
                .build();

        when(pacienteService.desactivar(pacienteId)).thenReturn(pacienteInactivo);

        // ACT: ejecutar método o endpoint.
        mockMvc.perform(patch("/api/pacientes/{id}/desactivar", pacienteId))
                // ASSERT: verificar resultado esperado.
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(9L))
                .andExpect(jsonPath("$.run").value("77777777-7"))
                .andExpect(jsonPath("$.nombre").value("Ricardo"))
                .andExpect(jsonPath("$.apellido").value("Vega"))
                .andExpect(jsonPath("$.email").value("ricardo.vega@test.cl"))
                .andExpect(jsonPath("$.activo").value(false));

        // VERIFY: comprobar llamadas al mock si corresponde.
        verify(pacienteService).desactivar(pacienteId);

        // Un ejemplo de falla posible.
        // Se esperaba: activo=false.
        // Se obtuvo: activo=true.
        // QA deberia reportarlo asi: El endpoint PATCH /api/pacientes/9/desactivar responde sin desactivar el paciente.
        // Desarrollo deberia revisar: PacienteController.desactivar() y PacienteService.desactivar().
    }

    @Test
    void eliminar_debeRetornarNoContent() throws Exception {
        // ARRANGE: preparar datos y mocks.
        Long pacienteId = 10L;
        doNothing().when(pacienteService).eliminar(pacienteId);

        // ACT: ejecutar método o endpoint.
        mockMvc.perform(delete("/api/pacientes/{id}", pacienteId))
                // ASSERT: verificar resultado esperado.
                .andExpect(status().isNoContent());

        // VERIFY: comprobar llamadas al mock si corresponde.
        verify(pacienteService).eliminar(pacienteId);

        // Un ejemplo de falla posible.
        // Se esperaba: HTTP 204 No Content.
        // Se obtuvo: HTTP 200 OK.
        // QA deberia reportarlo asi: El endpoint DELETE /api/pacientes/10 elimina el paciente pero responde un estado incorrecto.
        // Desarrollo deberia revisar: PacienteController.eliminar() y la respuesta noContent().
    }
}
