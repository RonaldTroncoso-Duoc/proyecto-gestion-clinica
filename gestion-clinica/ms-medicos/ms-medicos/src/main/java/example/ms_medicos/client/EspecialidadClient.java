package example.ms_medicos.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "ms-especialidades")
public interface EspecialidadClient {

    @GetMapping("/api/especialidades/{id}")
    Object obtenerEspecialidad(@PathVariable("id") Long id);
}
