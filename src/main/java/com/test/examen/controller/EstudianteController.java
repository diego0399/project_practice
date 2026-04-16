package com.test.examen.controller;

import com.test.examen.dto.request.EstudiantesDTORequest;
import com.test.examen.dto.response.EstudiantesDTO;
import com.test.examen.entity.Estudiantes;
import com.test.examen.repository.EstudiantesRepository;
import com.test.examen.service.CiudadService;
import com.test.examen.service.EstudianteService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/estudiantes")
public class EstudianteController {
    private final EstudianteService service;
    private  final CiudadService ciudadService;

    public EstudianteController(EstudianteService service, CiudadService ciudadService) {
        this.service = service;
        this.ciudadService = ciudadService;
    }

    @GetMapping
    public String listar(Model model, HttpSession session){
        if (session.getAttribute("usuarioLogueado") == null) {
            return "redirect:/login";
        }

        List<EstudiantesDTO> estudiantes = service.listar();
        model.addAttribute("estudiantes", estudiantes);

        model.addAttribute("estudiante", new EstudiantesDTORequest());

        model.addAttribute("ciudades", ciudadService.listar());
        model.addAttribute("editando", false);

        return "estudiante";
    }

    @PostMapping
    public String insertar(@ModelAttribute("estudiante") EstudiantesDTORequest request,
                           HttpSession session,
                           Model model,
                           RedirectAttributes redirectAttributes) {

        // Si no hay sesión, redirigimos al login
        if (session.getAttribute("usuarioLogueado") == null) {
            return "redirect:/login";
        }

        // Llama al servicio para insertar
        EstudiantesDTO resp = service.insertar(request);

        // Si tu servicio usa res/msj para errores, puedes validarlo aquí
        if (resp.getRes() != null && resp.getRes() < 0) {
            model.addAttribute("estudiantes", service.listar());
            model.addAttribute("estudiante", request);
            model.addAttribute("editando", false);
            model.addAttribute("mensaje", resp.getMsj());
            return "estudiante";
        }

        // Si todo sale bien, vuelve al listado
        model.addAttribute("ciudades", ciudadService.listar());
        redirectAttributes.addFlashAttribute("mensaje", "Estudiante guardado correctamente");
        return "redirect:/estudiantes";
    }

    @PostMapping("/actualizar")
    public String actualizar(@ModelAttribute("estudiante") EstudiantesDTORequest request,
                             HttpSession session,
                             Model model,
                             RedirectAttributes redirectAttributes) {
        // Si no hay sesión, redirigimos al login
        if (session.getAttribute("usuarioLogueado") == null) {
            return "redirect:/login";
        }

        // Llama al servicio para actualizar
        EstudiantesDTO resp = service.actualizar(request);

        // Si hubo error, vuelve a cargar la vista con el mensaje
        if (resp.getRes() != null && resp.getRes() < 0) {
            model.addAttribute("estudiantes", service.listar());
            model.addAttribute("cliente", request);
            model.addAttribute("editando", true);
            model.addAttribute("mensaje", resp.getMsj());
            return "estudiante";
        }

        model.addAttribute("ciudades", ciudadService.listar());
        // Si sale bien, redirige al listado
        redirectAttributes.addFlashAttribute(
                "mensaje",
                "Estudiante  actualizado correctamente"
        );
        return "redirect:/estudiantes";
    }

    @GetMapping("/eliminar/{id}")
    public String eliminar(@PathVariable Long id,
                           HttpSession session,
                           RedirectAttributes redirectAttributes) {

        // Si no hay sesión, redirigimos al login
        if (session.getAttribute("usuarioLogueado") == null) {
            return "redirect:/login";
        }

        // Llama al servicio para eliminar
        service.eliminar(id);

        // Redirige nuevamente al listado
        redirectAttributes.addFlashAttribute(
                "mensaje",
                "Registro eliminado correctamente."
        );
        return "redirect:/estudiantes";
    }

    @GetMapping("/editar/{id}")
    public String editar(@PathVariable Long id,
                         Model model,
                         HttpSession session) {

        if (session.getAttribute("usuarioLogueado") == null) {
            return "redirect:/estudiantes";
        }

        Estudiantes e = service.findEntityById(id);

        EstudiantesDTORequest dto = new EstudiantesDTORequest();
        dto.setId(e.getId());
        dto.setNombre(e.getNombre());
        dto.setEdad(e.getEdad());
        dto.setCiudadId(e.getCiudad() != null ? e.getCiudad().getId() : null);

        model.addAttribute("estudiante", dto);
        model.addAttribute("estudiantes", service.listar());
        model.addAttribute("editando", true);
        model.addAttribute("ciudades", ciudadService.listar());

        return "estudiante";
    }
}
