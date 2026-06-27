package example.ms_fichas_clinicas.controller;

import example.ms_fichas_clinicas.dto.FichaClinicaResponseDTO;
import example.ms_fichas_clinicas.service.FichaClinicaService;
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
 * Pruebas unitarias FichaClinicaController.
 * NOTA: Se eliminó MsFichasClinicasApplicationTests.java (@SpringBootTest).
 * COMANDO: mvn -pl ms-fichas-clinicas -Dtest=FichaClinicaControllerTest test
 * REPORTE: ms-fichas-clinicas/target/surefire-reports/
 */
@ExtendWith(MockitoExtension.class)
class FichaClinicaControllerTest {

    @Mock private FichaClinicaService service;
    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(new FichaClinicaController(service)).build();
    }

    private FichaClinicaResponseDTO crearFicha(Long id, Long pacienteId, Long medicoId, Long citaId,
                                               String motivo, String diagnostico, String tratamiento,
                                               Boolean activo) {
        return FichaClinicaResponseDTO.builder()
                .id(id).pacienteId(pacienteId).medicoId(medicoId).citaId(citaId)
                .motivoConsulta(motivo).diagnostico(diagnostico).tratamiento(tratamiento)
                .observaciones("Sin observaciones")
                .fechaRegistro(LocalDateTime.of(2026, 7, 1, 10, 0))
                .activo(activo)
                .build();
    }

    @Test void listarTodas_debeRetornarOkConFichas() throws Exception {
        when(service.listarTodas()).thenReturn(List.of(crearFicha(1L,10L,20L,30L,"Motivo","Dx","Trat",true)));
        mockMvc.perform(get("/api/fichas-clinicas")).andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].activo").value(true));
        verify(service).listarTodas();
    }

    @Test void listarActivas_debeRetornarOkConFichasActivas() throws Exception {
        when(service.listarActivas()).thenReturn(List.of(crearFicha(2L,10L,20L,30L,"Motivo","Dx","Trat",true)));
        mockMvc.perform(get("/api/fichas-clinicas/activas")).andExpect(status().isOk())
                .andExpect(jsonPath("$[0].activo").value(true));
        verify(service).listarActivas();
    }

    @Test void buscarPorId_debeRetornarOkConFicha() throws Exception {
        when(service.buscarPorId(3L)).thenReturn(crearFicha(3L,10L,20L,30L,"Motivo","Dx","Trat",true));
        mockMvc.perform(get("/api/fichas-clinicas/{id}", 3L)).andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(3L));
        verify(service).buscarPorId(3L);
    }

    @Test void buscarPorCita_debeRetornarOkConFicha() throws Exception {
        when(service.buscarPorCita(30L)).thenReturn(crearFicha(4L,10L,20L,30L,"Motivo","Dx","Trat",true));
        mockMvc.perform(get("/api/fichas-clinicas/cita/{citaId}", 30L)).andExpect(status().isOk())
                .andExpect(jsonPath("$.citaId").value(30L));
        verify(service).buscarPorCita(30L);
    }

    @Test void listarPorPaciente_debeRetornarOkConFichas() throws Exception {
        when(service.listarPorPaciente(10L)).thenReturn(List.of(crearFicha(5L,10L,20L,30L,"Motivo","Dx","Trat",true)));
        mockMvc.perform(get("/api/fichas-clinicas/paciente/{pacienteId}", 10L)).andExpect(status().isOk())
                .andExpect(jsonPath("$[0].pacienteId").value(10L));
        verify(service).listarPorPaciente(10L);
    }

    @Test void listarPorMedico_debeRetornarOkConFichas() throws Exception {
        when(service.listarPorMedico(20L)).thenReturn(List.of(crearFicha(6L,10L,20L,30L,"Motivo","Dx","Trat",true)));
        mockMvc.perform(get("/api/fichas-clinicas/medico/{medicoId}", 20L)).andExpect(status().isOk())
                .andExpect(jsonPath("$[0].medicoId").value(20L));
        verify(service).listarPorMedico(20L);
    }

    @Test void crear_debeRetornarCreatedConFichaCreada() throws Exception {
        String json = """
                {"pacienteId":10,"medicoId":20,"citaId":30,"motivoConsulta":"Motivo",
                 "diagnostico":"Diagnostico","tratamiento":"Tratamiento","observaciones":"Obs"}
                """;
        when(service.crear(any())).thenReturn(crearFicha(7L,10L,20L,30L,"Motivo","Diagnostico","Tratamiento",true));
        mockMvc.perform(post("/api/fichas-clinicas").contentType(MediaType.APPLICATION_JSON).content(json))
                .andExpect(status().isCreated()).andExpect(jsonPath("$.id").value(7L));
        verify(service).crear(any());
    }

    @Test void actualizar_debeRetornarOkConFichaActualizada() throws Exception {
        String json = """
                {"pacienteId":11,"medicoId":21,"citaId":31,"motivoConsulta":"Motivo nuevo",
                 "diagnostico":"Dx nuevo","tratamiento":"Trat nuevo","observaciones":"Obs"}
                """;
        when(service.actualizar(eq(8L), any())).thenReturn(crearFicha(8L,11L,21L,31L,"Motivo nuevo","Dx nuevo","Trat nuevo",true));
        mockMvc.perform(put("/api/fichas-clinicas/{id}", 8L).contentType(MediaType.APPLICATION_JSON).content(json))
                .andExpect(status().isOk()).andExpect(jsonPath("$.motivoConsulta").value("Motivo nuevo"));
        verify(service).actualizar(eq(8L), any());
    }

    @Test void desactivarYActivar_debenRetornarOk() throws Exception {
        when(service.desactivar(9L)).thenReturn(crearFicha(9L,10L,20L,30L,"Motivo","Dx","Trat",false));
        mockMvc.perform(put("/api/fichas-clinicas/{id}/desactivar", 9L)).andExpect(status().isOk())
                .andExpect(jsonPath("$.activo").value(false));
        verify(service).desactivar(9L);

        when(service.activar(9L)).thenReturn(crearFicha(9L,10L,20L,30L,"Motivo","Dx","Trat",true));
        mockMvc.perform(put("/api/fichas-clinicas/{id}/activar", 9L)).andExpect(status().isOk())
                .andExpect(jsonPath("$.activo").value(true));
        verify(service).activar(9L);
    }

    @Test void eliminar_debeRetornarNoContent() throws Exception {
        doNothing().when(service).eliminar(10L);
        mockMvc.perform(delete("/api/fichas-clinicas/{id}", 10L)).andExpect(status().isNoContent());
        verify(service).eliminar(10L);
    }
}