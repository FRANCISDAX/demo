package com.cibersoft.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.cibersoft.demo.entity.Usuario;
import com.cibersoft.demo.service.UsuarioService;

import jakarta.validation.Valid;

@Controller
public class AuthController {

    @Autowired
    private UsuarioService usuarioService;

    @GetMapping("/registro")
    public String registroForm(Model model) {
        model.addAttribute("usuario", new Usuario());
        return "registro";
    }

    @PostMapping("/registro")
    public String registrar(@Valid @ModelAttribute Usuario usuario, BindingResult result, RedirectAttributes redirectAttributes ) {

        if(result.hasErrors()) {
            return "/registro";
        }

        String resultado = usuarioService.registrar(usuario);

        if (resultado.equals("EMAIL ya está registrado.")) {
            redirectAttributes.addFlashAttribute("error", resultado);
            return "redirect:/registro";    
        }

        redirectAttributes.addFlashAttribute("exito", resultado);
        return "redirect:/login";
    }

    @GetMapping("/login")
        public String loginForm(Model model) {
        model.addAttribute("usuario", new Usuario());
        return "login";
    }
    
    @PostMapping("/login")
    public String login(@Valid @ModelAttribute("usuario") Usuario usuario,
                        BindingResult result,
                        Model model ) {
                
        if (result.hasErrors()) {
            return "login";
        }

        return "redirect:/";
    }
        
}
