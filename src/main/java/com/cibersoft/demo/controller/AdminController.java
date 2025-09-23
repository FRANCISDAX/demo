package com.cibersoft.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.cibersoft.demo.entity.Usuario;
import com.cibersoft.demo.service.UsuarioService;

@Controller
@RequestMapping("/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    @Autowired
    private UsuarioService usuarioService;

    // Usuarios
    @GetMapping("/usuarios")
    public String listarUsuarios(Model model) {
        model.addAttribute("title", "Usuarios");
        model.addAttribute("usuarios", usuarioService.listarUsuarios());
        model.addAttribute("usuario", new Usuario());
        model.addAttribute("contenido", "admin/usuarios/lista");
        return "admin/usuarios/layout";
    }

    @GetMapping("/usuarios/nuevo")
    public String nuevoUsuarioForm(Model model) {
        model.addAttribute("title", "Nuevo Usuario");
        model.addAttribute("usuario", new Usuario());
        model.addAttribute("contenido", "admin/usuarios/nuevo");
        return "admin/usuarios/layout";    
    }

    @PostMapping("/usuarios/nuevo")
    public String guardarUsuario(@ModelAttribute Usuario usuario,
        RedirectAttributes redirectAttributes,
        Model model) {
        usuarioService.guardarUsuario(usuario);
        redirectAttributes.addFlashAttribute("successMessage", "Usuario registrado correctamente");
        return "redirect:/admin/usuarios";
    }

    @GetMapping("/usuarios/editar/{id}")
    public String editarUsuarioForm(@PathVariable Long id, Model model) {
    Usuario usuario = usuarioService.obtenerUsuarioPorId(id);

        model.addAttribute("title", "Editar Usuario");
        model.addAttribute("usuario", usuario);
        model.addAttribute("contenido", "admin/usuarios/editar");

        return "admin/usuarios/layout";
}

    @PostMapping("/usuarios/editar/{id}")
    public String guardarUsuarioEditado(@PathVariable Long id,
        @ModelAttribute Usuario usuario,
        RedirectAttributes redirectAttributes) {
        usuarioService.guardarUsuario(usuario);
        redirectAttributes.addFlashAttribute("successMessage", "Usuario actualizado correctamente");
        return "redirect:/admin/usuarios";
    }

    @GetMapping("/usuarios/eliminar/{id}")
    public String eliminarUsuario(@PathVariable Long id,
        RedirectAttributes redirectAttributes) {
        usuarioService.eliminarUsuario(id);
        redirectAttributes.addFlashAttribute("successMessage", "Usuario eliminado correctamente");
        return "redirect:/admin/usuarios";
    }

    // Compras
    @GetMapping("/compras")
    public String listarComprasAdmin(Model model) { return "admin/compras/lista"; }

}