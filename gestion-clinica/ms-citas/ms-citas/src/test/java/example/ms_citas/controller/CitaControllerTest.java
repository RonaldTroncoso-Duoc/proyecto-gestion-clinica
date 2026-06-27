package example.ms_citas.controller;

import example.ms_citas.dto.CitaResponseDTO;
import example.ms_citas.service.CitaService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Pruebas unitarias CitaController.
 * NOTA: Se eliminó MsCitasApplicationTests.java (@SpringBootTest).
 * COMANDO: mvn -pl ms-citas -Dtest=CitaControllerTest test
 * REPORTE: ms-citas/target/surefire-reports/
 */
@ExtendWith(MockitoExtension.class)
class CitaControllerTest {

    @Mock
    private CitaService citaService;
    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(
                new CitaController(citaService)).build();
    }

    private CitaResponseDTO crearCitaResponse(Long id, Long pacId, Long medId,
                                               Long horId, String motivo, String est) {
        return CitaResponseDTO.builder().id(id).pacienteId(pacId).medicoId(medId)
                .horarioId(horId).motivo(motivo).estado(est)
                .fechaCreacion(LocalDateTime.of(2026, 7, 1, 10, 0)).build();
    }

    @Test void listarTodas_debeRetornarOkConCitas() throws Exception {
        CitaResponseDTO c = crearCitaResponse(1L, 10L, 20L, 30L, "Control", "AGENDADA");
        when(citaService.listarTodas()).thenReturn(List.of(c));
        mockMvc.perform(get("/api/citas")).andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].estado").value("AGENDADA"));
        verify(citaService).listarTodas();
    }

    @Test void buscarPorId_debeRetornarOkConCita() throws Exception {
        when(citaService.buscarPorId(2L))
                .thenReturn(crearCitaResponse(2L, 11L, 21L, 31L, "Rev", "AGENDADA"));
        mockMvc.perform(get("/api/citas/{id}", 2L)).andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(2L));
        verify(citaService).buscarPorId(2L);
    }

    @Test void listarPorPaciente_debeRetornarOkConCitas() throws Exception {
        when(citaService.listarPorPaciente(10L))
                .thenReturn(List.of(crearCitaResponse(3L, 10L, 20L, 30L, "C", "AGENDADA")));
        mockMvc.perform(get("/api/citas/paciente/{id}", 10L)).andExpect(status().isOk())
                .andExpect(jsonPath("$[0].pacienteId").value(10L));
        verify(citaService).listarPorPaciente(10L);
    }

    @Test void listarPorMedico_debeRetornarOkConCitas() throws Exception {
        when(citaService.listarPorMedico(20L))
                .thenReturn(List.of(crearCitaResponse(4L, 10L, 20L, 30L, "C", "AGENDADA")));
        mockMvc.perform(get("/api/citas/medico/{id}", 20L)).andExpect(status().isOk())
                .andExpect(jsonPath("$[0].medicoId").value(20L));
        verify(citaService).listarPorMedico(20L);
    }

    @Test void listarPorEstado_debeRetornarOkConCitas() throws Exception {
        when(citaService.listarPorEstado("AGENDADA"))
                .thenReturn(List.of(crearCitaResponse(5L, 10L, 20L, 30L, "U", "AGENDADA")));
        mockMvc.perform(get("/api/citas/estado/{estado}", "AGENDADA")).andExpect(status().isOk())
                .andExpect(jsonPath("$[0].estado").value("AGENDADA"));
        verify(citaService).listarPorEstado("AGENDADA");
    }

    @Test void crear_debeRetornarCreatedConCitaCreada() throws Exception {
        String json = """
                {"pacienteId":10,"medicoId":20,"horarioId":30,"motivo":"Control"}
                """;
        when(citaService.crear(any()))
                .thenReturn(crearCitaResponse(6L, 10L, 20L, 30L, "Control", "AGENDADA"));
        mockMvc.perform(post("/api/citas").contentType(MediaType.APPLICATION_JSON).content(json))
                .andExpect(status().isCreated()).andExpect(jsonPath("$.id").value(6L));
        verify(citaService).crear(any());
    }

    @Test void actualizar_debeRetornarOkConCitaActualizada() throws Exception {
        String json = """
                {"pacienteId":11,"medicoId":21,"horarioId":31,"motivo":"Nuevo motivo"}
                """;
        when(citaService.actualizar(eq(7L), any()))
                .thenReturn(crearCitaResponse(7L, 11L, 21L, 31L, "Nuevo motivo", "AGENDADA"));
        mockMvc.perform(put("/api/citas/{id}", 7L)
                        .contentType(MediaType.APPLICATION_JSON).content(json))
                .andExpect(status().isOk()).andExpect(jsonPath("$.motivo").value("Nuevo motivo"));
        verify(citaService).actualizar(eq(7L), any());
    }

    @Test void cancelar_debeRetornarOkConCitaCancelada() throws Exception {
        when(citaService.cancelar(8L))
                .thenReturn(crearCitaResponse(8L, 10L, 20L, 30L, "Control", "CANCELADA"));
        mockMvc.perform(patch("/api/citas/{id}/cancelar", 8L)).andExpect(status().isOk())
                .andExpect(jsonPath("$.estado").value("CANCELADA"));
        verify(citaService).cancelar(8L);
    }

    @Test void realizar_debeRetornarOkConCitaRealizada() throws Exception {
        when(citaService.realizar(9L))
                .thenReturn(crearCitaResponse(9L, 10L, 20L, 30L, "Cirugia", "REALIZADA"));
        mockMvc.perform(patch("/api/citas/{id}/realizar", 9L)).andExpect(status().isOk())
                .andExpect(jsonPath("$.estado").value("REALIZADA"));
        verify(citaService).realizar(9L);
    }

    @Test void eliminar_debeRetornarNoContent() throws Exception {
        doNothing().when(citaService).eliminar(10L);
        mockMvc.perform(delete("/api/citas/{id}", 10L)).andExpect(status().isNoContent());
        verify(citaService).eliminar(10L);
    }
}
