package example.ms_horarios.controller;

import example.ms_horarios.dto.HorarioResponseDTO;
import example.ms_horarios.service.HorarioService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class HorarioControllerTest {

    @Mock
    private HorarioService horarioService;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        HorarioController controller = new HorarioController(horarioService);
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
    void listarTodos_debeRetornarOkConHorarios() throws Exception {
        // ARRANGE: preparar datos y mocks.
        HorarioResponseDTO horario = crearHorarioResponse(true);
        when(horarioService.listarTodos()).thenReturn(List.of(horario));

        // ACT: ejecutar método o endpoint.
        mockMvc.perform(get("/api/horarios"))
                // ASSERT: verificar resultado esperado.
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].medicoId").value(10L))
                .andExpect(jsonPath("$[0].fecha[0]").value(2026))
                .andExpect(jsonPath("$[0].fecha[1]").value(7))
                .andExpect(jsonPath("$[0].fecha[2]").value(1))
                .andExpect(jsonPath("$[0].horaInicio[0]").value(9))
                .andExpect(jsonPath("$[0].horaInicio[1]").value(0))
                .andExpect(jsonPath("$[0].horaFin[0]").value(10))
                .andExpect(jsonPath("$[0].horaFin[1]").value(0))
                .andExpect(jsonPath("$[0].disponible").value(true));

        // VERIFY: comprobar llamadas al mock si corresponde.
        verify(horarioService).listarTodos();

        // Un ejemplo de falla posible.
        // Se esperaba: HTTP 200 OK con listado de horarios.
        // Se obtuvo: listado vacio o error inesperado.
        // QA deberia reportarlo asi: El endpoint GET /api/horarios no retorna los horarios registrados.
        // Desarrollo deberia revisar: HorarioController.listarTodos() y HorarioService.listarTodos().
    }

    @Test
    void listarDisponibles_debeRetornarOkConHorariosDisponibles() throws Exception {
        // ARRANGE: preparar datos y mocks.
        HorarioResponseDTO horario = crearHorarioResponse(true);
        when(horarioService.listarDisponibles()).thenReturn(List.of(horario));

        // ACT: ejecutar método o endpoint.
        mockMvc.perform(get("/api/horarios/disponibles"))
                // ASSERT: verificar resultado esperado.
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].disponible").value(true));

        // VERIFY: comprobar llamadas al mock si corresponde.
        verify(horarioService).listarDisponibles();

        // Un ejemplo de falla posible.
        // Se esperaba: solo horarios disponibles.
        // Se obtuvo: horarios ocupados dentro de la respuesta.
        // QA deberia reportarlo asi: El endpoint GET /api/horarios/disponibles retorna horarios no disponibles.
        // Desarrollo deberia revisar: HorarioController.listarDisponibles() y HorarioService.listarDisponibles().
    }

    @Test
    void buscarPorId_debeRetornarOkConHorario() throws Exception {
        // ARRANGE: preparar datos y mocks.
        HorarioResponseDTO horario = crearHorarioResponse(true);
        when(horarioService.buscarPorId(1L)).thenReturn(horario);

        // ACT: ejecutar método o endpoint.
        mockMvc.perform(get("/api/horarios/1"))
                // ASSERT: verificar resultado esperado.
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.medicoId").value(10L));

        // VERIFY: comprobar llamadas al mock si corresponde.
        verify(horarioService).buscarPorId(1L);

        // Un ejemplo de falla posible.
        // Se esperaba: horario con ID 1.
        // Se obtuvo: horario con otro ID.
        // QA deberia reportarlo asi: El endpoint GET /api/horarios/{id} retorna un horario distinto al solicitado.
        // Desarrollo deberia revisar: HorarioController.buscarPorId() y HorarioService.buscarPorId().
    }

    @Test
    void listarPorMedico_debeRetornarOkConHorariosDelMedico() throws Exception {
        // ARRANGE: preparar datos y mocks.
        HorarioResponseDTO horario = crearHorarioResponse(true);
        when(horarioService.listarPorMedico(10L)).thenReturn(List.of(horario));

        // ACT: ejecutar método o endpoint.
        mockMvc.perform(get("/api/horarios/medico/10"))
                // ASSERT: verificar resultado esperado.
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].medicoId").value(10L));

        // VERIFY: comprobar llamadas al mock si corresponde.
        verify(horarioService).listarPorMedico(10L);

        // Un ejemplo de falla posible.
        // Se esperaba: horarios del medico 10.
        // Se obtuvo: horarios de otro medico.
        // QA deberia reportarlo asi: El endpoint GET /api/horarios/medico/{medicoId} retorna horarios de otro medico.
        // Desarrollo deberia revisar: HorarioController.listarPorMedico() y HorarioService.listarPorMedico().
    }

    @Test
    void listarDisponiblesPorMedico_debeRetornarOkConHorariosDisponiblesDelMedico() throws Exception {
        // ARRANGE: preparar datos y mocks.
        HorarioResponseDTO horario = crearHorarioResponse(true);
        when(horarioService.listarDisponiblesPorMedico(10L)).thenReturn(List.of(horario));

        // ACT: ejecutar método o endpoint.
        mockMvc.perform(get("/api/horarios/medico/10/disponibles"))
                // ASSERT: verificar resultado esperado.
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].medicoId").value(10L))
                .andExpect(jsonPath("$[0].disponible").value(true));

        // VERIFY: comprobar llamadas al mock si corresponde.
        verify(horarioService).listarDisponiblesPorMedico(10L);

        // Un ejemplo de falla posible.
        // Se esperaba: horarios disponibles del medico 10.
        // Se obtuvo: horarios ocupados o de otro medico.
        // QA deberia reportarlo asi: El endpoint GET /api/horarios/medico/{medicoId}/disponibles retorna horarios incorrectos.
        // Desarrollo deberia revisar: HorarioController.listarDisponiblesPorMedico() y HorarioService.listarDisponiblesPorMedico().
    }

    @Test
    void crear_debeRetornarCreatedConHorarioCreado() throws Exception {
        // ARRANGE: preparar datos y mocks.
        String requestJson = crearHorarioRequestJson();
        HorarioResponseDTO horario = crearHorarioResponse(true);
        when(horarioService.crear(any())).thenReturn(horario);

        // ACT: ejecutar método o endpoint.
        mockMvc.perform(post("/api/horarios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                // ASSERT: verificar resultado esperado.
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.disponible").value(true));

        // VERIFY: comprobar llamadas al mock si corresponde.
        verify(horarioService).crear(any());

        // Un ejemplo de falla posible.
        // Se esperaba: HTTP 201 Created.
        // Se obtuvo: HTTP 200 OK o error inesperado.
        // QA deberia reportarlo asi: El endpoint POST /api/horarios crea el horario pero responde un estado incorrecto.
        // Desarrollo deberia revisar: HorarioController.crear() y el ResponseEntity con HttpStatus.CREATED.
    }

    @Test
    void actualizar_debeRetornarOkConHorarioActualizado() throws Exception {
        // ARRANGE: preparar datos y mocks.
        String requestJson = crearHorarioRequestJson();
        HorarioResponseDTO horario = crearHorarioResponse(true);
        when(horarioService.actualizar(eq(1L), any())).thenReturn(horario);

        // ACT: ejecutar método o endpoint.
        mockMvc.perform(put("/api/horarios/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                // ASSERT: verificar resultado esperado.
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.medicoId").value(10L));

        // VERIFY: comprobar llamadas al mock si corresponde.
        verify(horarioService).actualizar(eq(1L), any());

        // Un ejemplo de falla posible.
        // Se esperaba: horario actualizado con ID 1.
        // Se obtuvo: respuesta sin aplicar cambios.
        // QA deberia reportarlo asi: El endpoint PUT /api/horarios/{id} no actualiza correctamente el horario.
        // Desarrollo deberia revisar: HorarioController.actualizar() y HorarioService.actualizar().
    }

    @Test
    void ocupar_debeRetornarOkConHorarioOcupado() throws Exception {
        // ARRANGE: preparar datos y mocks.
        HorarioResponseDTO horario = crearHorarioResponse(false);
        when(horarioService.ocupar(1L)).thenReturn(horario);

        // ACT: ejecutar método o endpoint.
        mockMvc.perform(put("/api/horarios/1/ocupar"))
                // ASSERT: verificar resultado esperado.
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.disponible").value(false));

        // VERIFY: comprobar llamadas al mock si corresponde.
        verify(horarioService).ocupar(1L);

        // Un ejemplo de falla posible.
        // Se esperaba: horario marcado como ocupado.
        // Se obtuvo: horario sigue disponible.
        // QA deberia reportarlo asi: El endpoint PUT /api/horarios/{id}/ocupar no marca el horario como ocupado.
        // Desarrollo deberia revisar: HorarioController.ocupar() y HorarioService.ocupar().
    }

    @Test
    void liberar_debeRetornarOkConHorarioDisponible() throws Exception {
        // ARRANGE: preparar datos y mocks.
        HorarioResponseDTO horario = crearHorarioResponse(true);
        when(horarioService.liberar(1L)).thenReturn(horario);

        // ACT: ejecutar método o endpoint.
        mockMvc.perform(put("/api/horarios/1/liberar"))
                // ASSERT: verificar resultado esperado.
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.disponible").value(true));

        // VERIFY: comprobar llamadas al mock si corresponde.
        verify(horarioService).liberar(1L);

        // Un ejemplo de falla posible.
        // Se esperaba: horario marcado como disponible.
        // Se obtuvo: horario sigue ocupado.
        // QA deberia reportarlo asi: El endpoint PUT /api/horarios/{id}/liberar no marca el horario como disponible.
        // Desarrollo deberia revisar: HorarioController.liberar() y HorarioService.liberar().
    }

    @Test
    void eliminar_debeRetornarNoContent() throws Exception {
        // ARRANGE: preparar datos y mocks.
        doNothing().when(horarioService).eliminar(1L);

        // ACT: ejecutar método o endpoint.
        mockMvc.perform(delete("/api/horarios/1"))
                // ASSERT: verificar resultado esperado.
                .andExpect(status().isNoContent());

        // VERIFY: comprobar llamadas al mock si corresponde.
        verify(horarioService).eliminar(1L);

        // Un ejemplo de falla posible.
        // Se esperaba: HTTP 204 No Content.
        // Se obtuvo: HTTP 200 OK con cuerpo de respuesta.
        // QA deberia reportarlo asi: El endpoint DELETE /api/horarios/{id} elimina el horario pero responde un estado incorrecto.
        // Desarrollo deberia revisar: HorarioController.eliminar() y ResponseEntity.noContent().
    }

    private HorarioResponseDTO crearHorarioResponse(Boolean disponible) {
        return HorarioResponseDTO.builder()
                .id(1L)
                .medicoId(10L)
                .fecha(LocalDate.of(2026, 7, 1))
                .horaInicio(LocalTime.of(9, 0))
                .horaFin(LocalTime.of(10, 0))
                .disponible(disponible)
                .build();
    }

    private String crearHorarioRequestJson() {
        return """
                {
                  "medicoId": 10,
                  "fecha": "2026-07-01",
                  "horaInicio": "09:00:00",
                  "horaFin": "10:00:00"
                }
                """;
    }
}
