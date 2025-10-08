package com.cibersoft.demo.controller;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.cibersoft.demo.repository.ProductoRepository;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/carrito")
public class CarritoController {

    @Autowired
    private ProductoRepository productoRepository;

    @GetMapping
    public String listarCarrito(Model model, HttpSession session) {
        List<Map<String, Object>> carrito = (List<Map<String, Object>>) session.getAttribute("carrito");
        if (carrito == null) carrito = new ArrayList<>();

        // Calcular subtotal general (suma de subtotales)
        BigDecimal subtotal = carrito.stream()
            .filter(p -> p.get("subtotal") != null)
            .map(p -> new BigDecimal(p.get("subtotal").toString()))
            .reduce(BigDecimal.ZERO, BigDecimal::add);

        // Calcular IGV (18%)
        BigDecimal igv = subtotal.multiply(new BigDecimal("0.18"));

        // Descuento
        BigDecimal descuento = BigDecimal.ZERO;

        // Total general
        BigDecimal total = subtotal.add(igv).subtract(descuento);

        // Enviar datos al modelo
        model.addAttribute("carrito", carrito); 
        model.addAttribute("subtotal", subtotal); 
        model.addAttribute("igv", igv); 
        model.addAttribute("descuento", descuento); 
        model.addAttribute("total", total);        
        
        return "user/carrito/listar";

    }

    @PostMapping("/agregar")
    @ResponseBody
    public Map<String, Object> agregarAlCarrito(@RequestBody Map<String, Object> datos, HttpSession session) {
        Long productoId = Long.valueOf(datos.get("productoId").toString());
        int cantidad = Integer.parseInt(datos.get("cantidad").toString());
        
        List<Map<String, Object>> carrito = (List<Map<String, Object>>) session.getAttribute("carrito");
        if (carrito == null) carrito = new ArrayList<>();

        Optional<Map<String, Object>> existente = carrito.stream()
                .filter(p -> p.get("productoId").equals(productoId))
                .findFirst();

        if (existente.isPresent()) {
            int nuevaCantidad = (int) existente.get().get("cantidad") + cantidad;
            existente.get().put("cantidad", nuevaCantidad);

            BigDecimal precio = new BigDecimal(existente.get().get("precio").toString());
            existente.get().put("subtotal", precio.multiply(BigDecimal.valueOf(nuevaCantidad)));

        } else {
            var producto = productoRepository.findById(productoId).orElse(null);
            if (producto == null) {
                Map<String, Object> error = new HashMap<>();
                error.put("ok", false);
                error.put("mensaje", "Producto no encontrado");
                return error;
            }

            Map<String, Object> item = new HashMap<>();
            item.put("productoId", producto.getId());
            item.put("nombre", producto.getNombre());
            BigDecimal precio = new BigDecimal(producto.getPrecio().toString());
            item.put("precio", producto.getPrecio());
            item.put("imagenUrl", producto.getImagenUrl());
            item.put("cantidad", cantidad);
            item.put("subtotal", precio.multiply(BigDecimal.valueOf(cantidad)));

            carrito.add(item);
            
        }

        session.setAttribute("carrito", carrito);

        int totalItems = carrito.stream().mapToInt(p -> (int) p.get("cantidad")).sum();
        BigDecimal totalImporte = carrito.stream()
                .map(p -> (BigDecimal) p.get("subtotal"))
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        Map<String, Object> respuesta = new HashMap<>();
        respuesta.put("ok", true);
        respuesta.put("totalItems", totalItems);
        respuesta.put("totalImporte", totalImporte);
        return respuesta;
    }

    @PostMapping("/actualizar")
    @ResponseBody
    public Map<String, Object> actualizarCantidad(
        @RequestParam Long productoId,
        @RequestParam int cantidad,
        HttpSession session) {

        List<Map<String, Object>> carrito = 
            (List<Map<String, Object>>) session.getAttribute("carrito");

        if (carrito == null) carrito = new ArrayList<>();

        carrito.stream()
            .filter(p -> p.get("productoId").equals(productoId))
            .findFirst()
            .ifPresent(p -> {
                BigDecimal precio = new BigDecimal(p.get("precio").toString());
                p.put("cantidad", cantidad);
                p.put("subtotal", precio.multiply(BigDecimal.valueOf(cantidad)));
            });

        // Recalcular totales
        BigDecimal subtotal = carrito.stream()
            .filter(p -> p.get("subtotal") != null)
            .map(p -> new BigDecimal(p.get("subtotal").toString()))
            .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal igv = subtotal.multiply(new BigDecimal("0"));
        BigDecimal total = subtotal.add(igv);

        session.setAttribute("carrito", carrito);

        Map<String, Object> response = new HashMap<>();
        response.put("subtotal", subtotal);
        response.put("igv", igv);
        response.put("total", total);

        return response;
        
    }

    @PostMapping("/eliminar")
    @ResponseBody
    public Map<String, Object> eliminarDelCarrito(@RequestBody Map<String, Object> datos, HttpSession session) {
        Long productoId = Long.valueOf(datos.get("productoId").toString());

        List<Map<String, Object>> carrito = (List<Map<String, Object>>) session.getAttribute("carrito");
        if (carrito == null) carrito = new ArrayList<>();

        carrito.removeIf(p -> p.get("productoId").equals(productoId));

        session.setAttribute("carrito", carrito);

        int totalItems = carrito.stream()
                .mapToInt(p -> (int) p.get("cantidad"))
                .sum();

        BigDecimal totalImporte = carrito.stream()
                .map(p -> new BigDecimal(p.get("subtotal").toString()))
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        Map<String, Object> respuesta = new HashMap<>();
        respuesta.put("ok", true);
        respuesta.put("mensaje", "Producto eliminado del carrito");
        respuesta.put("totalItems", totalItems);
        respuesta.put("totalImporte", totalImporte);

        return respuesta;
    }

}
