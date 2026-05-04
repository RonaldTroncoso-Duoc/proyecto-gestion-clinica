package example.ms_pacientes.service;

import example.ms_pacientes.exception.PacienteNotFoundException;
import example.ms_pacientes.model.Paciente;
import example.ms_pacientes.repository.PacienteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PacienteService {

    @Autowired
    private PacienteRepository pacienteRepository;

    public List<Paciente> listarTodos() {
        return pacienteRepository.findAll();
    }

    public Paciente guardar(Paciente paciente) {
        return pacienteRepository.save(paciente);
    }

    public Paciente buscarPorId(Long id) {
        return pacienteRepository.findById(id)
                .orElseThrow(() -> new PacienteNotFoundException("Paciente con ID " + id + " no encontrado"));
    }

    public Paciente buscarPorRut(String rut) {
        return pacienteRepository.findByRut(rut)
                .orElseThrow(() -> new PacienteNotFoundException("Paciente con RUT " + rut + " no encontrado"));
    }

    public void eliminar(Long id) {
        Paciente p = buscarPorId(id);
        pacienteRepository.delete(p);
    }
}