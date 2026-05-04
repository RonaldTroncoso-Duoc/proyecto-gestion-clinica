package example.ms_citas.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "ms-pacientes") // Nombre exacto en Eureka
public interface PacienteClient {
    @GetMapping("/api/pacientes/{id}")
    Object obtenerPaciente(@PathVariable("id") Long id);
}