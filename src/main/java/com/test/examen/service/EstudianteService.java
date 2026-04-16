package com.test.examen.service;

import com.test.examen.dto.request.EstudiantesDTORequest;
import com.test.examen.dto.response.EstudiantesDTO;
import com.test.examen.entity.Ciudad;
import com.test.examen.entity.Estudiantes;
import com.test.examen.repository.CiudadRepository;
import com.test.examen.repository.EstudiantesRepository;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class EstudianteService {

    public final EstudiantesRepository repository;
    private CiudadRepository ciudadRepository;

    public EstudianteService(EstudiantesRepository repository, CiudadRepository ciudadRepository) {
        this.repository = repository;
        this.ciudadRepository = ciudadRepository;
    }

    public List<EstudiantesDTO> listar(){
        return repository.findAll(Sort.by("id"))
                .stream()
                .map(est -> new EstudiantesDTO(
                        est.getId(),
                        est.getNombre(),
                        est.getEdad(),
                        est.getCiudad() != null ? est.getCiudad().getNombre() : "Sin ciudad"
                ))
                .toList();
    }

    @Transactional
    public EstudiantesDTO insertar(EstudiantesDTORequest request){

        if(!Validardatos(request)){
            return new EstudiantesDTO(0, "Debe enviar todos los datos correctamente");
        }

        Estudiantes e = new Estudiantes();
        e.setNombre(request.getNombre().trim());
        e.setEdad(request.getEdad());

        Ciudad ciudad = repository.findById(request.getCiudadId())
                .orElse(null).getCiudad();

        if (ciudad == null) {
            return new EstudiantesDTO(0, "Ciudad inválida");
        }

        e.setCiudad(ciudad);

        Estudiantes guardado = repository.save(e);

        if (guardado.getId() != null) {
            return new EstudiantesDTO(1, "Registro exitoso");
        }

        return new EstudiantesDTO(-1, "No se pudo insertar el cliente");
    }

    @Transactional
    public EstudiantesDTO actualizar(EstudiantesDTORequest request){

        if(!Validardatos(request)){
            return new EstudiantesDTO(0, "Debe enviar todos los datos correctamente");
        }

        Estudiantes e = repository.findById(request.getId())
                .orElse(null);

        if (e == null) {
            return new EstudiantesDTO(0, "Estudiante no encontrado");
        }

        e.setNombre(request.getNombre().trim());
        e.setEdad(request.getEdad());

        Ciudad ciudad = ciudadRepository.findById(request.getCiudadId())
                .orElse(null);

        if (ciudad == null) {
            return new EstudiantesDTO(0, "Ciudad inválida");
        }

        e.setCiudad(ciudad);

        Estudiantes guardado = repository.save(e);

        if (guardado.getId() != null) {
            return new EstudiantesDTO(1, "Actualizado correctamente");
        }

        return new EstudiantesDTO(-1, "No se pudo actualizar");
    }

    @Transactional
    public EstudiantesDTO eliminar(Long id){
        try{

            if (id == null) {
                return new EstudiantesDTO(-4, "El id del estudiante es obligatorio");
            }

            if (!repository.existsById(id)) {
                return new EstudiantesDTO(-3, "El estudiante no existe");
            }

            repository.deleteById(id);
            if (!repository.existsById(id)) {
                return new EstudiantesDTO(1, "Cliente eliminado correctamente");
            }

            return new EstudiantesDTO(-1, "No se pudo eliminar el estudiante");

        } catch (Exception e) {
            return new EstudiantesDTO(-2, "Ocurrió un error al eliminar el cliente");
        }
    }


    public Estudiantes findEntityById(Long id){
        return repository.findById(id).orElseThrow();
    }



    public boolean Validardatos(EstudiantesDTORequest request){
        if (request == null) {
            return false;
        }

        if (request.getNombre() == null || request.getNombre().trim().isEmpty()) {
            return false;
        }

        if (request.getEdad() == null || request.getEdad()<0) {
            return false;
        }

        if (request.getCiudadId() == null || request.getCiudadId()<0) {
            return false;
        }


        return true;
    }
}
