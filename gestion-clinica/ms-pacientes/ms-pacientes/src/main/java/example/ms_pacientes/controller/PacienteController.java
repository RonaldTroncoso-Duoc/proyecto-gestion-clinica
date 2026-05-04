package example.ms_pacientes.controller;

import example.ms_pacientes.model.Paciente;
import example.ms_pacientes.service.PacienteService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/pacientes")
public class PacienteController {

    @Autowired
    private PacienteService pacienteService;

    @GetMapping
    public List<Paciente> listar() {
        return pacienteService.listarTodos();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Paciente> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(pacienteService.buscarPorId(id));
    }

    @GetMapping("/rut/{rut}")
    public ResponseEntity<Paciente> obtenerPorRut(@PathVariable String rut) {
        return ResponseEntity.ok(pacienteService.buscarPorRut(rut));
    }

    @PostMapping
    public ResponseEntity<Paciente> crear(@Valid @RequestBody Paciente paciente) {
        return new ResponseEntity<>(pacienteService.guardar(paciente), HttpStatus.CREATED);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        pacienteService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}