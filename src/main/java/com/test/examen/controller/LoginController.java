package com.test.examen.controller;

import com.test.examen.dto.request.UsuarioDTORequest;
import com.test.examen.dto.response.UsuariosDTO;
import com.test.examen.service.UsuarioService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class LoginController {
    private final UsuarioService service;



    public LoginController(UsuarioService service) {
        this.service = service;
    }

    @GetMapping("/login")
    public String loginForm(Model model) {
        model.addAttribute("usuario", new UsuarioDTORequest());

        // Retorna templates/login.html
        return "login";
    }
    @PostMapping("/login")
    public String login(@ModelAttribute("usuario") UsuarioDTORequest request,
                        Model model,
                        HttpSession session,
                        RedirectAttributes redirectAttributes)
    {
        UsuariosDTO response = service.validarLogin(request);

        if(response.getRes() !=null && response.getRes()<0){
            model.addAttribute("usuario", request);
            model.addAttribute("mensaje", response.getMsj());
            return "login";
        }

        session.setAttribute("usuarioLogueado", response.getNombre());
        redirectAttributes.addFlashAttribute("mensaje", "Inicio de sesión exitoso.");
        return "redirect:/estudiantes";

    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {

        session.invalidate();

        // Redirige al login
        return "redirect:/login";
    }

}
