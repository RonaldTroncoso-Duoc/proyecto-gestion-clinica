package example.ms_citas.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient(name = "ms-horarios")
public interface HorarioClient {

    @GetMapping("/api/horarios/{id}")
    Object obtenerHorario(@PathVariable("id") Long id);

    @PutMapping("/api/horarios/{id}/ocupar")
    Object ocuparHorario(@PathVariable("id") Long id);

    @PutMapping("/api/horarios/{id}/liberar")
    Object liberarHorario(@PathVariable("id") Long id);
}
