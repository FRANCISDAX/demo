package com.cibersoft.demo.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.cibersoft.demo.entity.Producto;
import com.cibersoft.demo.service.ProductoService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

@Controller
public class ProductoController {

    @Autowired
    private ProductoService productoService;

    // Página principal
    @GetMapping({"/", "/nosotros", "/politicas"})
    public String page(HttpServletRequest request, Model model) {
        String uri = request.getRequestURI();
        model.addAttribute("currentUri", uri);

        if ("/".equals(uri)) {
            List<Producto> productosDestacados = productoService.obtenerDestacados();
            model.addAttribute("productosDestacados", productosDestacados);
            return "index";
        }

        if ("/nosotros".equals(uri)) {
            return "nosotros";
        }

        if ("/politicas".equals(uri)) {
            return "politicas";
        }

        return "index";
    }

    // Listar productos
    @GetMapping("/productos")
    public String listarProductos(Model model) {
        model.addAttribute("productos", productoService.obtenerTodos());
        return "productos/listar";
    }

    // Buscar productos
    @GetMapping("/productos/buscar")
    public String buscarProductos(@RequestParam("q") String query, Model model) {
        List<Producto> resultados = productoService.buscarPorNombre(query);
        model.addAttribute("productos", resultados);
        model.addAttribute("query", query);
        return "productos/listar";
    }

    // Formulario crear producto
    @GetMapping("/productos/crear")
    public String mostrarFormularioCrear(Model model) {
        model.addAttribute("producto", new Producto());
        return "productos/crear";
    }

    // Guardar producto
    @PostMapping("/productos/guardar")
    public String guardarProducto(@Valid @ModelAttribute Producto producto, 
                                BindingResult result, Model model) {
        if (result.hasErrors()) {
            return "productos/crear";
        }
        productoService.guardar(producto);
        return "redirect:/productos?exito";
    }

    // Formulario editar producto
    @GetMapping("/productos/editar/{id}")
    public String mostrarFormularioEditar(@PathVariable Long id, Model model) {
        Optional<Producto> producto = productoService.obtenerPorId(id);
        if (producto.isPresent()) {
            model.addAttribute("producto", producto.get());
            return "productos/editar";
        }
        return "redirect:/productos?error";
    }

    // Actualizar producto
    @PostMapping("/productos/actualizar/{id}")
    public String actualizarProducto(@PathVariable Long id, 
                                   @Valid @ModelAttribute Producto producto,
                                   BindingResult result, Model model) {
        if (result.hasErrors()) {
            producto.setId(id);
            return "productos/editar";
        }
        productoService.guardar(producto);
        return "redirect:/productos?actualizado";
    }

    // Eliminar producto
    @GetMapping("/productos/eliminar/{id}")
    public String eliminarProducto(@PathVariable Long id) {
        if (productoService.existe(id)) {
            productoService.eliminar(id);
            return "redirect:/productos?eliminado";
        }
        return "redirect:/productos?error";
    }

}