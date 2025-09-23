package com.cibersoft.demo.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.cibersoft.demo.entity.Producto;

@Repository
public interface ProductoRepository extends JpaRepository<Producto, Long> {

    List<Producto> findByDestacado(boolean destacado);

    List<Producto> findByNombreContainingAllIgnoreCase(String nombre);

    List<Producto> findByCategoria_Id(Long categoriaId);

    boolean existsByCategoria_Id(Long categoriaId);
    
    boolean existsByNombre(String nombre);
    
}
