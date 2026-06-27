package example.ms_notificaciones.controller;

import example.ms_notificaciones.dto.NotificacionResponseDTO;
import example.ms_notificaciones.service.NotificacionService;
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
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Pruebas unitarias NotificacionController.
 * NOTA: Se eliminó MsNotificacionesApplicationTests.java (@SpringBootTest).
 * COMANDO: mvn -pl ms-notificaciones -Dtest=NotificacionControllerTest test
 * REPORTE: ms-notificaciones/target/surefire-reports/
 */
@ExtendWith(MockitoExtension.class)
class NotificacionControllerTest {

    @Mock private NotificacionService service;
    private MockMvc mockMvc;

    @BeforeEach void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(new NotificacionController(service)).build();
    }

    private NotificacionResponseDTO notif(Long id, Long pacienteId, Long citaId, String tipo, String estado) {
        return NotificacionResponseDTO.builder()
                .id(id).pacienteId(pacienteId).citaId(citaId).tipo(tipo)
                .mensaje("Mensaje de prueba").estado(estado)
                .fechaEnvio(LocalDateTime.of(2026,7,1,10,0)).build();
    }

    @Test void listarTodas_debeRetornarOkConNotificaciones() throws Exception {
        when(service.listarTodas()).thenReturn(List.of(notif(1L,10L,20L,"CITA","PENDIENTE")));
        mockMvc.perform(get("/api/notificaciones")).andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].estado").value("PENDIENTE"));
        verify(service).listarTodas();
    }

    @Test void buscarPorId_debeRetornarOkConNotificacion() throws Exception {
        when(service.buscarPorId(2L)).thenReturn(notif(2L,10L,20L,"CITA","PENDIENTE"));
        mockMvc.perform(get("/api/notificaciones/{id}",2L)).andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(2L));
        verify(service).buscarPorId(2L);
    }

    @Test void listarPorPaciente_debeRetornarOkConNotificaciones() throws Exception {
        when(service.listarPorPaciente(10L)).thenReturn(List.of(notif(3L,10L,20L,"CITA","PENDIENTE")));
        mockMvc.perform(get("/api/notificaciones/paciente/{pacienteId}",10L)).andExpect(status().isOk())
                .andExpect(jsonPath("$[0].pacienteId").value(10L));
        verify(service).listarPorPaciente(10L);
    }

    @Test void listarPorEstado_debeRetornarOkConNotificaciones() throws Exception {
        when(service.listarPorEstado("PENDIENTE")).thenReturn(List.of(notif(4L,10L,20L,"CITA","PENDIENTE")));
        mockMvc.perform(get("/api/notificaciones/estado/{estado}","PENDIENTE")).andExpect(status().isOk())
                .andExpect(jsonPath("$[0].estado").value("PENDIENTE"));
        verify(service).listarPorEstado("PENDIENTE");
    }

    @Test void listarPorTipo_debeRetornarOkConNotificaciones() throws Exception {
        when(service.listarPorTipo("CITA")).thenReturn(List.of(notif(5L,10L,20L,"CITA","PENDIENTE")));
        mockMvc.perform(get("/api/notificaciones/tipo/{tipo}","CITA")).andExpect(status().isOk())
                .andExpect(jsonPath("$[0].tipo").value("CITA"));
        verify(service).listarPorTipo("CITA");
    }

    @Test void crear_debeRetornarCreatedConNotificacionCreada() throws Exception {
        String json = """
                {"pacienteId":10,"citaId":20,"tipo":"CITA","mensaje":"Recordatorio de cita"}
                """;
        when(service.crear(any())).thenReturn(notif(6L,10L,20L,"CITA","PENDIENTE"));
        mockMvc.perform(post("/api/notificaciones").contentType(MediaType.APPLICATION_JSON).content(json))
                .andExpect(status().isCreated()).andExpect(jsonPath("$.id").value(6L));
        verify(service).crear(any());
    }

    @Test void marcarEnviada_debeRetornarOk() throws Exception {
        when(service.marcarEnviada(7L)).thenReturn(notif(7L,10L,20L,"CITA","ENVIADA"));
        mockMvc.perform(put("/api/notificaciones/{id}/enviada",7L)).andExpect(status().isOk())
                .andExpect(jsonPath("$.estado").value("ENVIADA"));
        verify(service).marcarEnviada(7L);
    }

    @Test void marcarFallida_debeRetornarOk() throws Exception {
        when(service.marcarFallida(8L)).thenReturn(notif(8L,10L,20L,"CITA","FALLIDA"));
        mockMvc.perform(put("/api/notificaciones/{id}/fallida",8L)).andExpect(status().isOk())
                .andExpect(jsonPath("$.estado").value("FALLIDA"));
        verify(service).marcarFallida(8L);
    }

    @Test void eliminar_debeRetornarNoContent() throws Exception {
        doNothing().when(service).eliminar(9L);
        mockMvc.perform(delete("/api/notificaciones/{id}",9L)).andExpect(status().isNoContent());
        verify(service).eliminar(9L);
    }
}