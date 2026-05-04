package example.ms_citas.service;

import example.ms_citas.client.MedicoClient;
import example.ms_citas.client.PacienteClient;
import example.ms_citas.model.Cita;
import example.ms_citas.repository.CitaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CitaService {

    @Autowired 
    private CitaRepository repository;

    @Autowired 
    private PacienteClient pacienteClient;

    @Autowired 
    private MedicoClient medicoClient;

    public List<Cita> listarTodas() {
        return repository.findAll();
    }

    public Cita agendarCita(Cita cita) {
        // Estas llamadas usan Feign para verificar que existan en los otros MS
        // Si no existen, Feign lanzará una excepción (puedes mejorar esto con un Try/Catch)
        pacienteClient.obtenerPaciente(cita.getPacienteId());
        medicoClient.obtenerMedico(cita.getMedicoId());
        
        return repository.save(cita);
    }
}