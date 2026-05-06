package example.ms_horarios.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "ms-medicos") // Nombre exacto en Eureka
public interface MedicoClient {
    @GetMapping("/api/medicos/{id}")
    Object obtenerMedico(@PathVariable("id") Long id);
}