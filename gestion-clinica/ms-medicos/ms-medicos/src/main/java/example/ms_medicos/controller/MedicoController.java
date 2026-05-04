package example.ms_medicos.controller;

import example.ms_medicos.model.Medico;
import example.ms_medicos.service.MedicoService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/medicos")
public class MedicoController {
    @Autowired
    private MedicoService service;

    @GetMapping
    public List<Medico> listar() { return service.listarTodos(); }

    @PostMapping
    public Medico crear(@Valid @RequestBody Medico medico) { return service.guardar(medico); }

    @GetMapping("/{id}")
    public Medico obtener(@PathVariable Long id) { return service.buscarPorId(id); }
}