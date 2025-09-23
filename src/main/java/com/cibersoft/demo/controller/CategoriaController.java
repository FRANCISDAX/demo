package com.cibersoft.demo.controller;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.cibersoft.demo.entity.Categoria;
import com.cibersoft.demo.service.CategoriaService;
import com.cibersoft.demo.service.ProductoService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/admin/categorias")
@RequiredArgsConstructor
public class CategoriaController {

    private final CategoriaService categoriaService;

    private final ProductoService productoService;

    @GetMapping
    public String listarCategorias(Model model) {
        model.addAttribute("categorias", categoriaService.listarTodas());
        return "admin/categorias/listar";
    }

    @GetMapping("/crear")
    public String mostrarFormularioCrear(Model model) {
        model.addAttribute("categoria", new Categoria());
        return "admin/categorias/crear";
    }

    @PostMapping("/crear")
    public String guardarCategoria(@Valid 
        @ModelAttribute("categoria") Categoria categoria,
        BindingResult bindingResult,
        RedirectAttributes redirectAttributes,
        Model model) {

        if (bindingResult.hasErrors()) {
            return "admin/categorias/crear";
        }

        try {
            categoriaService.guardar(categoria);
            model.addAttribute("exito", "✅ Categoría registrado correctamente.");
            redirectAttributes.addFlashAttribute("successMessage", "Categoría registrado correctamente");
            return "redirect:/admin/categorias";
        } catch (DataIntegrityViolationException e) {
            bindingResult.rejectValue("nombre", "error.categoria", "⚠️ El nombre de la categoría ya existe.");
            return "admin/categorias/crear";
        }
    }

    @PostMapping("/guardar")
    public String guardar(@Valid @ModelAttribute("categoria") Categoria categoria,
                          BindingResult result) {
        if (result.hasErrors()) {
            return "admin/categorias/crear";
        }
        categoriaService.guardar(categoria);
        return "redirect:/admin/categorias";
    }

    @GetMapping("/editar/{id}")
    public String mostrarFormularioEditar(@PathVariable Long id, Model model) {
        Categoria categoria = categoriaService.buscarPorId(id)
                .orElseThrow(() -> new IllegalArgumentException("Categoría no encontrada: " + id));
        model.addAttribute("categoria", categoria);
        return "admin/categorias/editar";
    }

    @GetMapping("/eliminar/{id}")
    public String eliminar(@PathVariable Long id,
        RedirectAttributes redirectAttributes) {
        if (!productoService.existeCategoriaPorId(id)) {
            categoriaService.eliminar(id);
            redirectAttributes.addFlashAttribute("successMessage", "Categoría eliminado correctamente.");
        } else {
            redirectAttributes.addFlashAttribute("warningMessage", "No se pudo eliminar esta Categoría porque tiene Productos asociados.");
        }
        return "redirect:/admin/categorias";
    }

}
