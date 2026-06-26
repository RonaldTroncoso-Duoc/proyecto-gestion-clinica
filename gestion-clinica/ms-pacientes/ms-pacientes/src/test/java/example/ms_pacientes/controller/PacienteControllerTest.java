package example.ms_pacientes.controller;

import example.ms_pacientes.dto.PacienteResponseDTO;
import example.ms_pacientes.service.PacienteService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
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

        // Caso hipotetico de falla para QA: se esperaba HTTP 200 OK y se obtuvo HTTP 403 Forbidden.
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

        // Caso hipotetico de falla para QA: se esperaba solo pacientes activos y se obtuvo un paciente inactivo.
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

        // Caso hipotetico de falla para QA: se esperaba el paciente ID 3 y se obtuvo otro ID.
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

        // Caso hipotetico de falla para QA: se esperaba el RUN 22222222-2 y se obtuvo otro RUN.
    }
}
