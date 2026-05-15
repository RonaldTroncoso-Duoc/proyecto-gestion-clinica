package example.ms_recetas.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient(name = "ms-fichas-clinicas")
public interface FichaClinicaClient {

    @GetMapping("/api/fichas-clinicas/{id}")
    Object obtenerFicha(@PathVariable("id") Long id);
}
