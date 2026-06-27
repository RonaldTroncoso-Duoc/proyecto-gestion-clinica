package example.ms_pagos.controller;

import example.ms_pagos.dto.PagoResponseDTO;
import example.ms_pagos.service.PagoService;
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
 * Pruebas unitarias PagoController.
 * NOTA: Se eliminó MsPagosApplicationTests.java (@SpringBootTest).
 * COMANDO: mvn -pl ms-pagos -Dtest=PagoControllerTest test
 * REPORTE: ms-pagos/target/surefire-reports/
 */
@ExtendWith(MockitoExtension.class)
class PagoControllerTest {

    @Mock private PagoService service;
    private MockMvc mockMvc;

    @BeforeEach void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(new PagoController(service)).build();
    }

    private PagoResponseDTO pago(Long id, Long pacienteId, Long citaId, Integer monto, String metodo, String estado) {
        return PagoResponseDTO.builder().id(id).pacienteId(pacienteId).citaId(citaId)
                .monto(monto).metodoPago(metodo).estado(estado)
                .fechaRegistro(LocalDateTime.of(2026,7,1,10,0)).build();
    }

    @Test void listarTodos_debeRetornarOkConPagos() throws Exception {
        when(service.listarTodos()).thenReturn(List.of(pago(1L,10L,20L,25000,"TARJETA","PENDIENTE")));
        mockMvc.perform(get("/api/pagos")).andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L));
        verify(service).listarTodos();
    }

    @Test void buscarPorId_debeRetornarOkConPago() throws Exception {
        when(service.buscarPorId(2L)).thenReturn(pago(2L,10L,20L,25000,"TARJETA","PENDIENTE"));
        mockMvc.perform(get("/api/pagos/{id}",2L)).andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(2L));
        verify(service).buscarPorId(2L);
    }

    @Test void listarPorPaciente_debeRetornarOkConPagos() throws Exception {
        when(service.listarPorPaciente(10L)).thenReturn(List.of(pago(3L,10L,20L,25000,"TARJETA","PENDIENTE")));
        mockMvc.perform(get("/api/pagos/paciente/{pacienteId}",10L)).andExpect(status().isOk())
                .andExpect(jsonPath("$[0].pacienteId").value(10L));
        verify(service).listarPorPaciente(10L);
    }

    @Test void listarPorEstado_debeRetornarOkConPagos() throws Exception {
        when(service.listarPorEstado("PENDIENTE")).thenReturn(List.of(pago(4L,10L,20L,25000,"TARJETA","PENDIENTE")));
        mockMvc.perform(get("/api/pagos/estado/{estado}","PENDIENTE")).andExpect(status().isOk())
                .andExpect(jsonPath("$[0].estado").value("PENDIENTE"));
        verify(service).listarPorEstado("PENDIENTE");
    }

    @Test void buscarPorCita_debeRetornarOkConPago() throws Exception {
        when(service.buscarPorCita(20L)).thenReturn(pago(5L,10L,20L,25000,"TARJETA","PENDIENTE"));
        mockMvc.perform(get("/api/pagos/cita/{citaId}",20L)).andExpect(status().isOk())
                .andExpect(jsonPath("$.citaId").value(20L));
        verify(service).buscarPorCita(20L);
    }

    @Test void crear_debeRetornarCreatedConPagoCreado() throws Exception {
        String json = """
                {"pacienteId":10,"citaId":20,"monto":25000,"metodoPago":"tarjeta"}
                """;
        when(service.crear(any())).thenReturn(pago(6L,10L,20L,25000,"TARJETA","PENDIENTE"));
        mockMvc.perform(post("/api/pagos").contentType(MediaType.APPLICATION_JSON).content(json))
                .andExpect(status().isCreated()).andExpect(jsonPath("$.id").value(6L));
        verify(service).crear(any());
    }

    @Test void actualizar_debeRetornarOkConPagoActualizado() throws Exception {
        String json = """
                {"pacienteId":11,"citaId":21,"monto":30000,"metodoPago":"efectivo"}
                """;
        when(service.actualizar(eq(7L), any())).thenReturn(pago(7L,11L,21L,30000,"EFECTIVO","PENDIENTE"));
        mockMvc.perform(put("/api/pagos/{id}",7L).contentType(MediaType.APPLICATION_JSON).content(json))
                .andExpect(status().isOk()).andExpect(jsonPath("$.monto").value(30000));
        verify(service).actualizar(eq(7L), any());
    }

    @Test void confirmarYAnular_debenRetornarOk() throws Exception {
        when(service.confirmar(8L)).thenReturn(pago(8L,10L,20L,25000,"TARJETA","PAGADO"));
        mockMvc.perform(put("/api/pagos/{id}/confirmar",8L)).andExpect(status().isOk())
                .andExpect(jsonPath("$.estado").value("PAGADO"));
        verify(service).confirmar(8L);

        when(service.anular(9L)).thenReturn(pago(9L,10L,20L,25000,"TARJETA","ANULADO"));
        mockMvc.perform(put("/api/pagos/{id}/anular",9L)).andExpect(status().isOk())
                .andExpect(jsonPath("$.estado").value("ANULADO"));
        verify(service).anular(9L);
    }

    @Test void eliminar_debeRetornarNoContent() throws Exception {
        doNothing().when(service).eliminar(10L);
        mockMvc.perform(delete("/api/pagos/{id}",10L)).andExpect(status().isNoContent());
        verify(service).eliminar(10L);
    }
}