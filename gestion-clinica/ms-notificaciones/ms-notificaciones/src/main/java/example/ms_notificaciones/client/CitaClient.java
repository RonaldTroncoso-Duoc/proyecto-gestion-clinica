package example.ms_notificaciones.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient(name = "ms-citas")
public interface CitaClient {

    @GetMapping("/api/citas/{id}")
    Object obtenerCita(@PathVariable("id") Long id);
}
