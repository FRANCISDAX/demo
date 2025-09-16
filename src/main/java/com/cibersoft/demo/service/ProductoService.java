package com.cibersoft.demo.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cibersoft.demo.entity.Producto;
import com.cibersoft.demo.repository.ProductoRepository;

@Service
public class ProductoService {

    @Autowired
    private ProductoRepository productoRepository;

    public List<Producto> obtenerTodos(){
        return productoRepository.findAll();
    }

    public Optional<Producto> obtenerPorId(Long id){
        return productoRepository.findById(id);
    }

    public Producto guardar(Producto producto){
        return productoRepository.save(producto);
    }

    public void eliminar(Long id){
        productoRepository.deleteById(id);
    }

    public boolean existe(Long id){
        return productoRepository.existsById(id);
    }

    public List<Producto> buscarPorCategoria(String categoria){
        return productoRepository.findByCategoria(categoria);
    }

    public List<Producto> buscarPorNombre(String nombre){
        return productoRepository.findByNombreContainingIgnoreCase(nombre);
    }

}