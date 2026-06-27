package example.ms_recetas.controller;

import example.ms_recetas.dto.RecetaResponseDTO;
import example.ms_recetas.service.RecetaService;
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
 * Pruebas unitarias RecetaController.
 * NOTA: Se eliminó MsRecetasApplicationTests.java (@SpringBootTest).
 * COMANDO: mvn -pl ms-recetas -Dtest=RecetaControllerTest test
 * REPORTE: ms-recetas/target/surefire-reports/
 */
@ExtendWith(MockitoExtension.class)
class RecetaControllerTest {

    @Mock private RecetaService service;
    private MockMvc mockMvc;

    @BeforeEach void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(new RecetaController(service)).build();
    }

    private RecetaResponseDTO receta(Long id, Long fichaId, Long pacienteId, Long medicoId, Boolean activa) {
        return RecetaResponseDTO.builder().id(id).fichaClinicaId(fichaId).pacienteId(pacienteId)
                .medicoId(medicoId).medicamento("Paracetamol").dosis("500mg")
                .indicaciones("Cada 8 horas").duracionDias(5)
                .fechaEmision(LocalDateTime.of(2026,7,1,10,0)).activa(activa).build();
    }

    @Test void listarTodas_debeRetornarOkConRecetas() throws Exception {
        when(service.listarTodas()).thenReturn(List.of(receta(1L,10L,20L,30L,true)));
        mockMvc.perform(get("/api/recetas")).andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L));
        verify(service).listarTodas();
    }

    @Test void listarActivas_debeRetornarOkConRecetasActivas() throws Exception {
        when(service.listarActivas()).thenReturn(List.of(receta(2L,10L,20L,30L,true)));
        mockMvc.perform(get("/api/recetas/activas")).andExpect(status().isOk())
                .andExpect(jsonPath("$[0].activa").value(true));
        verify(service).listarActivas();
    }

    @Test void buscarPorId_debeRetornarOkConReceta() throws Exception {
        when(service.buscarPorId(3L)).thenReturn(receta(3L,10L,20L,30L,true));
        mockMvc.perform(get("/api/recetas/{id}",3L)).andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(3L));
        verify(service).buscarPorId(3L);
    }

    @Test void listarPorPaciente_debeRetornarOkConRecetas() throws Exception {
        when(service.listarPorPaciente(20L)).thenReturn(List.of(receta(4L,10L,20L,30L,true)));
        mockMvc.perform(get("/api/recetas/paciente/{pacienteId}",20L)).andExpect(status().isOk())
                .andExpect(jsonPath("$[0].pacienteId").value(20L));
        verify(service).listarPorPaciente(20L);
    }

    @Test void listarPorMedico_debeRetornarOkConRecetas() throws Exception {
        when(service.listarPorMedico(30L)).thenReturn(List.of(receta(5L,10L,20L,30L,true)));
        mockMvc.perform(get("/api/recetas/medico/{medicoId}",30L)).andExpect(status().isOk())
                .andExpect(jsonPath("$[0].medicoId").value(30L));
        verify(service).listarPorMedico(30L);
    }

    @Test void listarPorFichaClinica_debeRetornarOkConRecetas() throws Exception {
        when(service.listarPorFichaClinica(10L)).thenReturn(List.of(receta(6L,10L,20L,30L,true)));
        mockMvc.perform(get("/api/recetas/ficha-clinica/{fichaClinicaId}",10L)).andExpect(status().isOk())
                .andExpect(jsonPath("$[0].fichaClinicaId").value(10L));
        verify(service).listarPorFichaClinica(10L);
    }

    @Test void crear_debeRetornarCreatedConRecetaCreada() throws Exception {
        String json = """
                {"fichaClinicaId":10,"pacienteId":20,"medicoId":30,"medicamento":"Paracetamol",
                 "dosis":"500mg","indicaciones":"Cada 8 horas","duracionDias":5}
                """;
        when(service.crear(any())).thenReturn(receta(7L,10L,20L,30L,true));
        mockMvc.perform(post("/api/recetas").contentType(MediaType.APPLICATION_JSON).content(json))
                .andExpect(status().isCreated()).andExpect(jsonPath("$.id").value(7L));
        verify(service).crear(any());
    }

    @Test void actualizar_debeRetornarOkConRecetaActualizada() throws Exception {
        String json = """
                {"fichaClinicaId":11,"pacienteId":21,"medicoId":31,"medicamento":"Ibuprofeno",
                 "dosis":"400mg","indicaciones":"Cada 12 horas","duracionDias":3}
                """;
        when(service.actualizar(eq(8L), any())).thenReturn(receta(8L,11L,21L,31L,true));
        mockMvc.perform(put("/api/recetas/{id}",8L).contentType(MediaType.APPLICATION_JSON).content(json))
                .andExpect(status().isOk()).andExpect(jsonPath("$.fichaClinicaId").value(11L));
        verify(service).actualizar(eq(8L), any());
    }

    @Test void activarYDesactivar_debenRetornarOk() throws Exception {
        when(service.activar(9L)).thenReturn(receta(9L,10L,20L,30L,true));
        mockMvc.perform(put("/api/recetas/{id}/activar",9L)).andExpect(status().isOk())
                .andExpect(jsonPath("$.activa").value(true));
        verify(service).activar(9L);

        when(service.desactivar(9L)).thenReturn(receta(9L,10L,20L,30L,false));
        mockMvc.perform(put("/api/recetas/{id}/desactivar",9L)).andExpect(status().isOk())
                .andExpect(jsonPath("$.activa").value(false));
        verify(service).desactivar(9L);
    }

    @Test void eliminar_debeRetornarNoContent() throws Exception {
        doNothing().when(service).eliminar(10L);
        mockMvc.perform(delete("/api/recetas/{id}",10L)).andExpect(status().isNoContent());
        verify(service).eliminar(10L);
    }
}