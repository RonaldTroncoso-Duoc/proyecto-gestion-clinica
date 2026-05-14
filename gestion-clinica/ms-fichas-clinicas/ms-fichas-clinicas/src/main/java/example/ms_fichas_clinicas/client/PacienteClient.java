package example.ms_fichas_clinicas.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient(name = "ms-pacientes")
public interface PacienteClient {

    @GetMapping("/api/pacientes/{id}")
    Object obtenerPaciente(@PathVariable("id") Long id);
}
