package com.cibersoft.demo.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.cibersoft.demo.entity.Producto;
import com.cibersoft.demo.service.ProductoService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

@Controller
public class ProductoController {

    @Autowired
    private ProductoService productoService;

 // ---------------------- PUBLICAS ----------------------
    @GetMapping({"/", "/productos", "/nosotros", "/politicas"})
    public String page(HttpServletRequest request, Model model) {
        String uri = request.getRequestURI();
        model.addAttribute("currentUri", uri);

        if ("/".equals(uri)) {
            List<Producto> productosDestacados = productoService.obtenerDestacados();
            model.addAttribute("productosDestacados", productosDestacados);
            return "index";
        }

        if ("/productos".equals(uri)) {
            List<Producto> productos = productoService.obtenerTodos();
            model.addAttribute("productos", productos);
            return "productos";
        }

        if ("/nosotros".equals(uri)) {
            return "nosotros";
        }

        if ("/politicas".equals(uri)) {
            return "politicas";
        }

        return "index";
    }

    @GetMapping("/productos/buscar")
    public String buscarProductosPublico(@RequestParam("q") String query,
                                         @RequestParam(value = "destacados", defaultValue = "false") boolean destacados,
                                         Model model) {
        List<Producto> resultados;
        if (destacados) {
            resultados = productoService.buscarPorCategoria(query);
        } else {
            resultados = productoService.buscarPorNombre(query);
        }
        model.addAttribute("productos", resultados);
        model.addAttribute("query", query);
        return destacados ? "index" : "productos";
    }

    // ---------------------- ADMIN ----------------------
    @Controller
    @RequestMapping("/admin/productos")
    @PreAuthorize("hasRole('ADMIN')")
    public class AdminProductoController {

        @Autowired
        private ProductoService productoService;

        @GetMapping
        public String listarProductos(Model model) {
             model.addAttribute("productos", productoService.obtenerTodos());
             return "admin/productos/listar";
        }

        @GetMapping("/crear")
        public String mostrarFormularioCrear(Model model) {
            model.addAttribute("producto", new Producto());
            return "admin/productos/crear";
        }

        @PostMapping("/guardar")
        public String guardarProducto(@Valid @ModelAttribute Producto producto, BindingResult result) {
            if (result.hasErrors()) return "/admin/productos/crear";
            productoService.guardar(producto);
            return "redirect:/admin/productos?exito";
        }

        @GetMapping("/editar/{id}")
        public String mostrarFormularioEditar(@PathVariable Long id, Model model) {
            productoService.obtenerPorId(id).ifPresent(p -> model.addAttribute("producto", p));
            return "admin/productos/editar";
        }

        @PostMapping("/actualizar/{id}")
        public String actualizarProducto(@PathVariable Long id, @Valid @ModelAttribute Producto producto, BindingResult result) {
            if (result.hasErrors()) return "/admin/productos/editar";
            productoService.guardar(producto);
            return "redirect:/admin/productos?actualizado";
        }

        @GetMapping("/eliminar/{id}")
        public String eliminarProducto(@PathVariable Long id) {
            if (productoService.existe(id)) productoService.eliminar(id);
            return "redirect:/admin/productos?eliminado";
        }
    }

}