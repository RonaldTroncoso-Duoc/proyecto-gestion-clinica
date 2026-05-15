package example.ms_recetas.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient(name = "ms-medicos")
public interface MedicoClient {

    @GetMapping("/api/medicos/{id}")
    Object obtenerMedico(@PathVariable("id") Long id);
}
