package example.ms_especialidades.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import example.ms_especialidades.exception.EspecialidadNotFoundException;
import example.ms_especialidades.model.Especialidad;
import example.ms_especialidades.repository.EspecialidadRepository;

@Service
public class EspecialidadService {

    @Autowired
    private EspecialidadRepository repository;

    public List<Especialidad> listarTodas() {
        return repository.findAll();
    }
    
    public List<Especialidad> listarActivas() {
        return repository.findByActivoTrue();
    }

    public Especialidad buscarPorId(Long id) {
        return repository.findById(id).orElseThrow(() 
        -> new EspecialidadNotFoundException("Especialidad con ID " + id + " no encontrada"));
    }

    public Especialidad guardar(Especialidad especialidad) {
        if (repository.existsByNombre(especialidad.getNombre())) {
            throw new RuntimeException("Ya existe una especialidad con ese nombre");
        }
        especialidad.setActivo(true);
        return repository.save(especialidad);
    }

    public Especialidad actualizar(Long id, Especialidad datos) {
        Especialidad especialidad = buscarPorId(id);

        especialidad.setNombre(datos.getNombre());
        especialidad.setDescripcion(datos.getDescripcion());
        return repository.save(especialidad);
    }

    public Especialidad desactivar(Long id) {
        Especialidad especialidad = buscarPorId(id);
        especialidad.setActivo(false);
        return repository.save(especialidad);
    }

    public Especialidad activar(Long id) {
        Especialidad especialidad = buscarPorId(id);
        especialidad.setActivo(true);
        return repository.save(especialidad);
    }

}
