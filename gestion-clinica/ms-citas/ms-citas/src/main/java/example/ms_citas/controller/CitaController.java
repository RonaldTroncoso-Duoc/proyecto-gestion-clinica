package example.ms_citas.controller;

import example.ms_citas.model.Cita;
import example.ms_citas.service.CitaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/citas")
public class CitaController {

    @Autowired
    private CitaService citaService;

    @GetMapping
    public List<Cita> listar() {
        return citaService.listarTodas();
    }

    @PostMapping
    public ResponseEntity<Cita> agendar(@RequestBody Cita cita) {
        return new ResponseEntity<>(citaService.agendarCita(cita), HttpStatus.CREATED);
    }
}