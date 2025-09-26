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
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.cibersoft.demo.entity.Producto;
import com.cibersoft.demo.service.CategoriaService;
import com.cibersoft.demo.service.CloudinaryService;
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
        resultados = productoService.buscarPorNombre(query);
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

        @Autowired
        private CategoriaService categoriaService;

        @Autowired
        private CloudinaryService cloudinaryService;

        @GetMapping
        public String listarProductos(Model model) {
             model.addAttribute("productos", productoService.obtenerTodos());
             return "admin/productos/listar";
        }

        @GetMapping("/crear")
        public String mostrarFormularioCrear(Model model) {
            Producto producto = new Producto();
            model.addAttribute("producto", producto);
            model.addAttribute("categorias", categoriaService.listarCategoriasActivas());
            return "admin/productos/crear";
        }

        @PostMapping("/crear")
        public String guardarProducto(@Valid 
            @ModelAttribute("producto") Producto producto,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes,
            @RequestParam("imagen") MultipartFile imagen,
            Model model) {

            if (producto.getCategoria() == null) {
                bindingResult.rejectValue("categoria", "error.producto", "⚠️ Seleccione una Categoría.");
            }

            if (productoService.existePorNombre(producto.getNombre())) {
                bindingResult.rejectValue("nombre", "error.producto", "⚠️ El nombre del producto ya existe.");
            }

            if (bindingResult.hasErrors()) {
                model.addAttribute("categorias", categoriaService.listarCategoriasActivas());
                return "admin/productos/crear";
            }

            try {
                if (!imagen.isEmpty()) {
                    String urlImagen = cloudinaryService.uploadFile(imagen);
                    System.out.println("📸 Imagen subida a Cloudinary: " + urlImagen);
                    producto.setImagenUrl(urlImagen);
                }
                productoService.guardar(producto);
            } catch (Exception e) {
                e.printStackTrace();
            }
            
            model.addAttribute("exito", "✅ Producto registrado correctamente.");
            redirectAttributes.addFlashAttribute("successMessage", "Producto registrado correctamente");
            return "redirect:/admin/productos";
            
        }

        @GetMapping("/editar/{id}")
        public String mostrarFormularioEditar(@PathVariable Long id, Model model) {
            productoService.obtenerPorId(id).ifPresent(p -> model.addAttribute("producto", p));
            model.addAttribute("categorias", categoriaService.listarTodas());
            return "admin/productos/editar";
        }

        @PostMapping("/actualizar/{id}")
        public String actualizarProducto(@PathVariable Long id,
            @Valid @ModelAttribute Producto producto,
            BindingResult result, 
            RedirectAttributes redirectAttributes,
            @RequestParam("imagen") MultipartFile imagen) {

            if (result.hasErrors()) return "/admin/productos/editar";

            try {
                Producto productoExistente = productoService.obtenerPorId(id)
                    .orElseThrow(() -> new IllegalArgumentException("Producto no encontrado con id: " + id));

                if (!imagen.isEmpty()) {
                    String urlImagen = cloudinaryService.uploadFile(imagen);
                    producto.setImagenUrl(urlImagen);
                } else {
                    producto.setImagenUrl(productoExistente.getImagenUrl());
                }
                productoService.guardar(producto);
            } catch (Exception e) {
                e.printStackTrace();
            }
            redirectAttributes.addFlashAttribute("successMessage", "Producto actualizado correctamente");
            return "redirect:/admin/productos?actualizado";
        }

        @GetMapping("/eliminar/{id}")
        public String eliminarProducto(@PathVariable Long id,
            RedirectAttributes redirectAttributes) {
            if (productoService.existe(id)){
                productoService.eliminar(id);
                redirectAttributes.addFlashAttribute("successMessage", "Producto eliminado correctamente");
            } 
            return "redirect:/admin/productos?eliminado";
        }
    }

}