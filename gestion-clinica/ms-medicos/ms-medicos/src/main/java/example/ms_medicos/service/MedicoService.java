package example.ms_medicos.service;

import example.ms_medicos.exception.MedicoNotFoundException;
import example.ms_medicos.model.Medico;
import example.ms_medicos.repository.MedicoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class MedicoService {
    @Autowired
    private MedicoRepository repository;

    public List<Medico> listarTodos() { return repository.findAll(); }
    public Medico guardar(Medico medico) { return repository.save(medico); }
    public Medico buscarPorId(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new MedicoNotFoundException("Médico no encontrado"));
    }
}