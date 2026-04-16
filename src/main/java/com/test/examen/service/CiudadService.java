package com.test.examen.service;

import com.test.examen.entity.Ciudad;
import com.test.examen.repository.CiudadRepository;
import com.test.examen.repository.EstudiantesRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CiudadService {

    public final CiudadRepository repository;


    public CiudadService(CiudadRepository repository) {
        this.repository = repository;
    }

    public List<Ciudad> listar() {
        return repository.findAll();
    }
}
