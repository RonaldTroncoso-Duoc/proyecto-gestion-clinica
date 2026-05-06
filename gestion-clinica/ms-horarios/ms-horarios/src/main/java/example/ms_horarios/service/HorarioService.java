package example.ms_horarios.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import example.ms_horarios.client.MedicoClient;
import example.ms_horarios.exception.HorarioNotFoundException;
import example.ms_horarios.model.Horario;
import example.ms_horarios.repository.HorarioRepository;

@Service
public class HorarioService {

    @Autowired
    private HorarioRepository repository;

    @Autowired
    private MedicoClient medicoClient;

    public List<Horario> listarTodos() {
        return repository.findAll();
    }

    public List<Horario> listarDisponibles() {
        return repository.findByDisponibleTrue();
    }
    
    public List<Horario> listarPorMedico(Long medicoId) {
        medicoClient.obtenerMedico(medicoId); // Verificar que el médico existe
        return repository.findByMedicoId(medicoId);
    }

    public List<Horario> listarDisponiblesPorMedico(Long medicoId) {
        medicoClient.obtenerMedico(medicoId); // Verificar que el médico existe
        return repository.findByMedicoIdDisponibleTrue(medicoId);
    }

    public Horario buscarPorId(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new HorarioNotFoundException("Horario con ID " + id + " no encontrado"));
    }

    public Horario crear(Horario horario) {
        medicoClient.obtenerMedico(horario.getMedicoId()); // Verificar que el médico existe

        if (horario.getHoraTermino().isBefore(horario.getHoraInicio()) ||
            horario.getHoraTermino().equals(horario.getHoraInicio())) {
            throw new IllegalArgumentException("La hora de término debe ser posterior a la hora de inicio");
        }

        boolean existe = repository.existsByMedicoIdFechaHorario(
                horario.getMedicoId(),
                horario.getFecha(),
                horario.getHoraInicio(),
                horario.getHoraTermino()
        );

        if (existe) {
            throw new RuntimeException("Ya existe un horario registrado para ese médico en la misma fecha y hora");
        }

        horario.setDisponible(true);
        return repository.save(horario);
    }

}
