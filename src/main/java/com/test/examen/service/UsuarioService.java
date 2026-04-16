package com.test.examen.service;

import com.test.examen.dto.request.UsuarioDTORequest;
import com.test.examen.dto.response.UsuariosDTO;
import com.test.examen.entity.Usuarios;
import com.test.examen.repository.UsuariosRepository;
import org.springframework.stereotype.Service;

@Service
public class UsuarioService {

    private final UsuariosRepository repository;

    public UsuarioService(UsuariosRepository repository) {
        this.repository = repository;
    }

    public UsuariosDTO validarLogin(UsuarioDTORequest request){
        Usuarios users = repository.findByNombreIgnoreCase(request.getLogin()).orElse(null);


        if(request.getLogin().isEmpty() || request.getPass().isEmpty()){
            return new UsuariosDTO(0,"Debe completar los datos");
        }

        if(users == null){
            return new UsuariosDTO(-1,"El usuario no existe");
        }

        if(!users.getPass().equals(request.getPass())){
            return new UsuariosDTO(-2,"La contraseña es incorrecta");
        }

        System.out.println("ID"+users.getId()+ " Nombre:"+users.getNombre() );
        return new UsuariosDTO(users.getNombre(),users.getId());
    }


}
