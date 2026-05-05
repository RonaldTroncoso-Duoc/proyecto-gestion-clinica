package example.ms_especialidades.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import example.ms_especialidades.model.Especialidad;
import example.ms_especialidades.service.EspecialidadService;
import jakarta.validation.Valid;


@RestController
@RequestMapping("/api/especialidades")
public class EspecialidadController {

    @Autowired
    private EspecialidadService service;
    
    @GetMapping
    public List<Especialidad> listarTodas() {
        return service.listarTodas();
    }

    @GetMapping("/activas")
    public List<Especialidad> listarActivas() {
        return service.listarActivas();
    }

    @GetMapping("/{id}")
    public Especialidad obtenerPorId(@PathVariable Long id) {
        return service.buscarPorId(id);
    }

    @PostMapping
    public Especialidad crear(@Valid @RequestBody Especialidad especialidad) {
        return service.guardar(especialidad);
    }

    @PutMapping("/{id}")
    public Especialidad actualizar(@PathVariable Long id, @Valid @RequestBody Especialidad especialidad) {
        return service.actualizar(id, especialidad);
    }

    @PutMapping("/{id}/desactivar")
    public Especialidad desactivar(@PathVariable Long id) {
        return service.desactivar(id);
    }

    @PutMapping("/{id}/activar")
    public Especialidad activar(@PathVariable Long id) {
        return service.activar(id);
    }
    

}
